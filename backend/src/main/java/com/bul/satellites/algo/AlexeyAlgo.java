package com.bul.satellites.algo;

import com.bul.satellites.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.logging.log4j.util.TriConsumer;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.bul.satellites.service.AlgoUtils.*;

public class AlexeyAlgo implements Algorithm {

    @Builder
    @Getter
    static class SatelliteState {
        String name;
        long memory;
        String intention; // "scan"/"transmit"
    }


    @Builder
    @AllArgsConstructor
    public static class Visibility {
        public Interval interval;
        public String base;
    }


    public List<Visibility> durationDatasetToVisibility(List<DurationDataset> durationDatasets) {
        return durationDatasets.stream()
                .collect(Collectors.groupingBy(d -> d.satelliteBasePair.base)).entrySet().stream()
                .flatMap((kv) -> kv.getValue().stream().flatMap(d -> d.entries.stream().map(interval -> Visibility.builder().base(d.satelliteBasePair.base).interval(interval).build())))
                .collect(Collectors.toList());
    }

    @Override
    public Result apply(Given given) {
        Duration step = Duration.ofSeconds(3000);
        Instant end = given.limits.end;
        Duration duration = given.limits.duration();

        Instant t_current = given.limits.start;
        long totalDataLost = 0;
        long totalDataReceived = 0;
//        int output_schedule = [] .map(e -> Map.entry(e.getKey(), ))

        Map<String, List<Interval>> basesFree = given.availabilityByBase.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> new ArrayList<>(List.of(given.limits))));
        Map<String, List<Interval>> satellitesFree = given.availabilityBySatellite.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> new ArrayList<>(List.of(given.limits))));


        List<SatelliteState> satellites = new ArrayList<>(given.getAvailabilityBySatellite().keySet().stream()
                .map(name -> SatelliteState.builder().name(name).memory(0).intention("scan").build())
                .toList());
        Map<String, List<Visibility>> visibilities = given.getAvailabilityBySatellite().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, d -> durationDatasetToVisibility(d.getValue())));

        Map<SatelliteBasePair, List<Interval>> results = new HashMap<>();

        TriConsumer<String, String, List<Interval>> appendToResults = (String satelliteName, String baseName, List<Interval> inpIntervals) -> {
            SatelliteBasePair satelliteBasePair = new SatelliteBasePair(satelliteName, baseName);
            List<Interval> intervals = results.computeIfAbsent(satelliteBasePair, p -> new ArrayList<>());
            intervals.addAll(inpIntervals);
            results.put(satelliteBasePair, intervals);
        };

        while (t_current.isBefore(given.limits.end)) {
            Instant stepEnd = earliest(t_current.plus(step), end);
            satellites.sort(Comparator.comparing(SatelliteState::getMemory).reversed());
            //   Collections.shuffle(satellites);

            for (SatelliteState s : satellites) {

                List<Interval> russiaRanges = given.availabilityRussia.get(s.name).stream()
                        .flatMap(e -> e.entries.stream()).collect(Collectors.toList());

                Interval stepInterval = new Interval(t_current, stepEnd);
                if (s.intention.equals("scan")) {
                    List<Interval> formerCut = intersection(russiaRanges, List.of(stepInterval));
///sumOverIntervals(formerCut).toSeconds() *newAmount/gaindata
                    long gainedData = sumOverIntervals(formerCut).toSeconds() * given.rx_speed;

                    //    long newAmountOfData = Math.min(given.memory_limit, s.memory + gainedData);

                    long newAmountOfData = Math.min(((Integer.parseInt(s.getName().substring(s.getName().length() - 6)) > 111510) ? Given.memory_limit : Given.memory_limit2), s.memory + gainedData);

                    //((Integer.parseInt(p.getSatellite().substring(p.getSatellite().length() - 6)) > 111510) ? Given.tx_speedC : Given.tx_speed)
                    if (gainedData > 0) {
                        try {

                            //   System.out.println(s.name + " " + sumOverIntervals(formerCut).toSeconds() * newAmountOfData / gainedData);
                            System.out.println("Длительность без переполнения " + s.name + " " + sumOverIntervals(formerCut).toSeconds() * (newAmountOfData - s.memory) / gainedData);
                        } catch (ArithmeticException ae) {
                            System.out.println("ArithmeticException occured!");
                        }
                    }
                    totalDataLost += s.memory + gainedData - newAmountOfData;
                    s.memory = newAmountOfData;
                } else { // передающие спутники
                    List<Visibility> cutVisibilities = intervalsCut(visibilities.get(s.name), stepInterval);
                    List<Visibility> visibilityWithLoad = cutVisibilities.stream()
                            .flatMap(v -> intersection(List.of(v.interval), basesFree.get(v.base)).stream().map(i -> new Visibility(i, v.base)))
                            .toList();

                    for (Visibility v : visibilityWithLoad) {
                        List<Interval> intervalsBusy = intersection(List.of(v.interval), satellitesFree.get(s.name));
                        List<Interval> notIntervalsBusy = intervalsComplement(intervalsBusy, given.limits);
                        //long maxAmountOfData = given.tx_speed * sumOverIntervals(intervalsBusy).getSeconds();
                        long maxAmountOfData = ((Integer.parseInt(s.getName().substring(s.getName().length() - 6)) > 111510) ? Given.tx_speedC : Given.tx_speed) * sumOverIntervals(intervalsBusy).getSeconds();

                        if (maxAmountOfData < s.memory) {
                            basesFree.put(v.base, intersection(basesFree.get(v.base), notIntervalsBusy));
                            satellitesFree.put(s.name, intersection(satellitesFree.get(s.name), notIntervalsBusy));
                            appendToResults.accept(s.name, v.base, intervalsBusy);
                            totalDataReceived += maxAmountOfData;
                            s.memory -= maxAmountOfData;
                        } else {
                            intervalsBusy = intervalsCutBySum(intervalsBusy, Duration.ofSeconds(s.memory / ((Integer.parseInt(s.getName().substring(s.getName().length() - 6)) > 111510) ? Given.tx_speedC : Given.tx_speed)));
                            notIntervalsBusy = intervalsComplement(intervalsBusy, given.limits);

                            basesFree.put(v.base, intersection(basesFree.get(v.base), notIntervalsBusy));
                            satellitesFree.put(s.name, intersection(satellitesFree.get(s.name), notIntervalsBusy));
                            appendToResults.accept(s.name, v.base, intervalsBusy);
                            totalDataReceived += s.memory;
                            s.memory = 0;
                            break;
                        }
                    }
                }
                List<Instant> stateChangingTimes = russiaRanges.stream()
                        .map(e -> e.end.plusSeconds(3000))
                        .toList();
                for (Instant x : stateChangingTimes) {
                    if (t_current.compareTo(x) <= 0 && x.isBefore(t_current.plus(step))) {
                        if (s.intention.equals("scan")) {
                            s.intention = "transmit";
                        } else {
                            s.intention = "scan";
                        }
                    }
                }
            }
            t_current = t_current.plus(min(step, Duration.between(t_current, given.limits.end)));
        }
        List<DurationDataset> durationDatasets = results.entrySet().stream()
                .filter(d -> !d.getValue().isEmpty())
                .map(e -> DurationDataset.builder().satelliteBasePair(e.getKey()).entries(e.getValue()).build())
                .toList();

        return new Result(durationDatasets);
    }


    @Override
    public String name() {
        return "alex";
    }
}
