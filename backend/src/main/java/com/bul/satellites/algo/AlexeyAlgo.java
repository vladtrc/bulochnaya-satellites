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


        public long txSpeed(Params params) {
            return isKino() ? params.tx_speedC : params.tx_speed;
        }

        public long memoryLimit(Params params) {
            return isKino() ? params.memory_limit : params.memory_limit2;
        }

        public boolean isKino() {
            return Integer.parseInt(name.substring(name.length() - 6)) > 111510;
        }
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

        Instant end = given.params.limits.end;

        Instant t_current = given.params.limits.start;

        long totalDataReceived = 0;

        Map<String, List<Interval>> basesFree = given.availabilityByBase.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> new ArrayList<>(List.of(given.params.limits))));
        Map<String, List<Interval>> satellitesFree = given.availabilityBySatellite.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> new ArrayList<>(List.of(given.params.limits))));


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

        while (t_current.isBefore(given.params.limits.end)) {
            Instant stepEnd = earliest(t_current.plus(step), end);
            satellites.stream().filter(s -> s.memory < given.params.memory_limit);
            for (SatelliteState s : satellites) {
                List<Interval> russiaRanges = given.availabilityRussia.get(s.name).stream()
                        .flatMap(e -> e.entries.stream()).collect(Collectors.toList());

                Interval stepInterval = new Interval(t_current, stepEnd);
                List<Interval> formerCut = intersection(russiaRanges, List.of(stepInterval));
                List<Interval> formerCutFree = intersection(formerCut, satellitesFree.get(s.name));
                long maxGainedData = sumOverIntervals(formerCutFree).toSeconds() * given.params.rx_speed;


                if (maxGainedData != 0) {
                    long newAmountOfData = Math.min(s.memoryLimit(given.params), s.memory + maxGainedData);
                    long accum = sumOverIntervals(formerCut).toSeconds() * (newAmountOfData - s.memory) / maxGainedData;
                    List<Interval> fullLoadIntervals = intervalsCutBySum(formerCutFree, Duration.ofSeconds(accum));
                    try {
                        System.out.println("Длительность без переполнения " + s.name + " " + sumOverIntervals(formerCut).toSeconds() * (newAmountOfData - s.memory) / maxGainedData);
                    } catch (ArithmeticException ae) {
                        System.out.println("ArithmeticException occured!");
                    }
                    s.memory = newAmountOfData;

                    List<Interval> notFullLoadIntervals = intervalsComplement(fullLoadIntervals, given.params.limits);
                    satellitesFree.put(s.name, intersection(satellitesFree.get(s.name), notFullLoadIntervals));
                }
            }

            satellites.sort(Comparator.comparing(SatelliteState::getMemory).reversed());
            for (SatelliteState s : satellites) {

                List<Interval> russiaRanges = given.availabilityRussia.get(s.name).stream()
                        .flatMap(e -> e.entries.stream()).toList();

                Interval stepInterval = new Interval(t_current, stepEnd);
                List<Visibility> cutVisibilities = intervalsCut(visibilities.get(s.name), stepInterval);
                List<Visibility> visibilityWithLoad = cutVisibilities.stream()
                        .flatMap(v -> intersection(List.of(v.interval), basesFree.get(v.base)).stream().map(i -> new Visibility(i, v.base)))
                        .toList();


                for (Visibility v : visibilityWithLoad) {
                    List<Interval> intervalsBusy = intersection(List.of(v.interval), satellitesFree.get(s.name));
                    List<Interval> notIntervalsBusy = intervalsComplement(intervalsBusy, given.params.limits);
                    long maxAmountOfData = (s.txSpeed(given.params)) * sumOverIntervals(intervalsBusy).getSeconds();

                    if (maxAmountOfData < s.memory) {
                        basesFree.put(v.base, intersection(basesFree.get(v.base), notIntervalsBusy));
                        satellitesFree.put(s.name, intersection(satellitesFree.get(s.name), notIntervalsBusy));
                        appendToResults.accept(s.name, v.base, intervalsBusy);
                        totalDataReceived += maxAmountOfData;
                        s.memory -= maxAmountOfData;
                    } else {
                        intervalsBusy = intervalsCutBySum(intervalsBusy, Duration.ofSeconds(s.memory / s.txSpeed(given.params)));
                        notIntervalsBusy = intervalsComplement(intervalsBusy, given.params.limits);

                        basesFree.put(v.base, intersection(basesFree.get(v.base), notIntervalsBusy));
                        satellitesFree.put(s.name, intersection(satellitesFree.get(s.name), notIntervalsBusy));
                        appendToResults.accept(s.name, v.base, intervalsBusy);
                        totalDataReceived += s.memory;
                        s.memory = 0;
                        break;
                    }
                }
            }


            t_current = t_current.plus(min(step, Duration.between(t_current, given.params.limits.end)));
        }

        //  }
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
