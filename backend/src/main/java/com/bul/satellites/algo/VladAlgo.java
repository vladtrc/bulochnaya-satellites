package com.bul.satellites.algo;

import com.bul.satellites.model.Given;
import com.bul.satellites.model.Interval;
import com.bul.satellites.model.Result;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bul.satellites.service.AlgoUtils.intervalsContain;

public class VladAlgo implements Algorithm {
    @Override
    public Result apply(Given given) {

        Map<String, List<Instant>> eventsByBase = given.availabilityByBase.entrySet().stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream()
                                        .flatMap(durationDataset -> durationDataset.entries.stream().flatMap(interval -> Stream.of(interval.start, interval.end)))
                                        .distinct()
                                        .sorted()
                                        .collect(Collectors.toList())
                        )
                );
        Map<String, ArrayList<Map.Entry<Interval, List<String>>>> intervalsByBases = eventsByBase.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> {
                    // get to know which intervals correspond to which satellites
                    List<Instant> borders = e.getValue();
                    Stream<Instant> starts = Stream.concat(Stream.of(given.interval.start), borders.stream());
                    Stream<Instant> ends = Stream.concat(borders.stream(), Stream.of(given.interval.end));

                    Stream<Interval> intervals = Streams.zip(starts, ends, (start, end) -> Interval.builder().start(start).end(end).build());
                    Function<Interval, List<String>> satelliteListByInterval = interval -> given.availabilityByBase.get(e.getKey()).stream()
                            .filter(durationDataset -> intervalsContain(durationDataset.entries, interval))
                            .map(durationDataset -> durationDataset.satelliteBasePair.satellite)
                            .collect(Collectors.toList());
                    Map<Interval, List<String>> satellitesByIntervals = intervals.collect(Collectors.toMap(Function.identity(), satelliteListByInterval));

                    // join em together
                    List<Map.Entry<Interval, List<String>>> intervalsSorted = satellitesByIntervals.entrySet().stream().sorted(Comparator.comparing(i -> i.getKey().start)).toList();
                    Iterator<Map.Entry<Interval, List<String>>> iterator = intervalsSorted.iterator();
                    Map.Entry<Interval, List<String>> first = iterator.next();
                    Instant start = first.getKey().start;
                    List<String> value = first.getValue();
                    ArrayList<Map.Entry<Interval, List<String>>> intervalsJoined = new ArrayList<>(intervalsSorted.size());
                    while (iterator.hasNext()) {
                        Map.Entry<Interval, List<String>> next = iterator.next();
                        if (next.getValue().equals(value)) {
                            continue;
                        }
                        intervalsJoined.add(Maps.immutableEntry(new Interval(start, next.getKey().start), value));
                        value = next.getValue();
                        start = next.getKey().start;
                    }
                    return intervalsJoined;
                }));
        for (Map.Entry<String, ArrayList<Map.Entry<Interval, List<String>>>> intervalsByBase : intervalsByBases.entrySet()) {
            String base = intervalsByBase.getKey();
            ArrayList<Map.Entry<Interval, List<String>>> intervals = intervalsByBase.getValue();
            Collections.reverse(intervals);
            Iterator<Map.Entry<Interval, List<String>>> intervalIterator = intervals.iterator();
            Map.Entry<Interval, List<String>> interval = intervalIterator.next();
            while (intervalIterator.hasNext()) {

            }
        }
        return null;
    }

    @Override
    public String name() {
        return "vlad";
    }
}
