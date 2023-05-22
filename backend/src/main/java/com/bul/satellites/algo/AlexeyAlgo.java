package com.bul.satellites.algo;

import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.Interval;
import com.bul.satellites.model.Result;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bul.satellites.service.AlgoUtils.*;

public class AlexeyAlgo implements Algorithm {

    @Builder
    static class SatelliteState {
        String name;
        long memory;
        String intention; // "scan"/"transmit"
    }

    @Builder
    public static class Visibility {
        Interval interval;
        String base;
    }


    public List<Visibility> durationDatasetToVisibility(String name, List<DurationDataset> durationDatasets) {
        return durationDatasets.stream()
                .collect(Collectors.groupingBy(d -> d.satelliteBasePair.base)).entrySet().stream()
                .flatMap((kv) -> kv.getValue().stream().flatMap(d -> d.entries.stream().map(interval -> Visibility.builder().base(name).interval(interval).build())))
                .collect(Collectors.toList());
    }

    @Override
    public Result apply(Given given) {
        Duration step = Duration.ofSeconds(3000);
        Instant end = given.getInterval().end;
        Duration duration = given.getInterval().duration();

        Instant t_current = given.getInterval().start;
        int total_data_lost = 0;
        int total_data_received = 0;
//        int output_schedule = [] .map(e -> Map.entry(e.getKey(), ))

        Map<String, ArrayList<Duration>> basesFree = given.availabilityByBase.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> new ArrayList<>(List.of(duration))));
        Map<String, ArrayList<Duration>> satellitesFree = given.availabilityBySatellite.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e) -> new ArrayList<>(List.of(duration))));


        List<SatelliteState> satellites = given.getAvailabilityBySatellite().keySet().stream()
                .map(name -> SatelliteState.builder().name(name).memory(0).intention("scan").build())
                .toList();
        Map<String, List<Visibility>> visibilities = given.getAvailabilityBySatellite().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, d -> durationDatasetToVisibility(d.getKey(), d.getValue())));


        while (t_current.isBefore(given.getInterval().end)) {
            Instant stepEnd = earliest(t_current.plus(step), end);
            for (SatelliteState s : satellites) {
                List<Interval> russiaRanges = given.availabilityRussia.get(s.name).stream()
                        .flatMap(e -> e.entries.stream()).collect(Collectors.toList());


                if (s.intention.equals("scan")) {
                    Interval interval = Interval.builder().start(t_current).end(stepEnd).build();
                    List<Interval> formerCut = intersection(russiaRanges, List.of(interval));

                    long gainedData = sumOverIntervals(formerCut).toSeconds() * given.rx_speed;


                    long newAmountOfData = Math.min(given.memory_limit, s.memory + gainedData);
                    long totalDataLost = s.memory + gainedData - newAmountOfData;
                    s.memory = newAmountOfData;
                } else { // передающие спутники
                    List<Visibility> cutVisibilities = intervalsCut(visibilities.get(s.name), Interval.builder().start(t_current).end(stepEnd).build());
//                    cutVisibilities.stream().map(v -> intersection(List.of(v.interval), ))

                }
            }
        }
        return null;
    }

    @Override
    public String name() {
        return "alex";
    }
}
