package com.bul.satellites.algo;

import com.bul.satellites.model.Given;
import com.bul.satellites.model.Interval;
import com.bul.satellites.model.Result;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import lombok.Builder;
import org.apache.logging.log4j.util.TriConsumer;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bul.satellites.model.Given.limits;
import static com.bul.satellites.model.Given.rx_speed;
import static com.bul.satellites.service.AlgoUtils.*;

public class VladAlgo implements Algorithm {

    @Builder
    static class RxIntervals {
        List<Interval> rxIntervals;
        Duration duration;
    }

    @Builder
    static class SatelliteTaken {
        Interval interval;
        String base;
    }

    @Override
    public Result apply(Given given) {

        Map<String, List<Interval>> rxIntervalsBySatellite = given.availabilityRussia.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().flatMap(d -> d.entries.stream()).collect(Collectors.toList())));
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
                    Stream<Instant> starts = Stream.concat(Stream.of(limits.start), borders.stream());
                    Stream<Instant> ends = Stream.concat(borders.stream(), Stream.of(limits.end));

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
        Duration maxConnectionDuration = Duration.ofSeconds(Given.memory_limit / Given.tx_speed);

        Map<String, List<SatelliteTaken>> result = given.availabilityByBase.keySet().stream().collect(Collectors.toMap(Function.identity(), e -> new ArrayList<>()));
        Map<String, List<Interval>> satelliteFree = given.availabilityBySatellite.keySet().stream().collect(Collectors.toMap(Function.identity(), e -> new ArrayList<>(List.of(limits))));

        BiConsumer<String, Interval> takeInterval = (String satelliteName, Interval interval) -> {
            List<Interval> freeIntervals = satelliteFree.get(satelliteName);
            List<Interval> takenIntervals = intervalsComplement(freeIntervals, limits);
            takenIntervals.add(interval); // todo мб стоит тут объединить чтобы не образовалось пустых
            Collections.sort(takenIntervals, Comparator.comparing(i -> i.start));

            satelliteFree.put(satelliteName, intervalsComplement(takenIntervals, limits));
        };
        BiConsumer<String, List<Interval>> takeIntervals = (String satelliteName, List<Interval> intervals) -> {
            intervals.forEach(interval -> takeInterval.accept(satelliteName, interval));
        };
        TriConsumer<String, String, Interval> appendToResults = (String satelliteName, String baseName, Interval interval) -> {
            SatelliteTaken taken = SatelliteTaken.builder().base(baseName).interval(interval).build();
            List<SatelliteTaken> intervals = result.computeIfAbsent(satelliteName, p -> new ArrayList<>());
            intervals.add(taken);
            result.put(satelliteName, intervals);
        };

        for (Map.Entry<String, ArrayList<Map.Entry<Interval, List<String>>>> intervalsByBase : intervalsByBases.entrySet()) {
            String base = intervalsByBase.getKey();
            ArrayList<Map.Entry<Interval, List<String>>> intervals = intervalsByBase.getValue();
            int index = intervals.size() - 1;
            Instant rightMostPoint = intervals.get(index).getKey().end;
            while (index >= 0) {
                Interval interval = intervals.get(index).getKey();
                List<String> satellites = intervals.get(index).getValue();
                index -= 1;


                int finalI = index;
                Function<String, Instant> satelliteConnectStart = satelliteName -> {
                    int j = finalI;
                    while (intervals.get(j).getValue().contains(satelliteName)) {
                        j -= 1;
                    }
                    return intervals.get(j).getKey().start;
                };
                Instant rightMostPointCopy = rightMostPoint;
                Map<String, RxIntervals> rxBySatellite = satellites.stream().collect(Collectors.toMap(Function.identity(),
                        satelliteName -> {
                            Interval connectionWindow = new Interval(satelliteConnectStart.apply(satelliteName), rightMostPointCopy);

                            Duration possibleTxDuration = intersection(List.of(connectionWindow), satelliteFree.get(satelliteName)).stream()
                                    .filter(windowinterval -> windowinterval.end == rightMostPointCopy)
                                    .map(Interval::duration)
                                    .findAny()
                                    .orElse(Duration.ZERO);
                            Duration duration = clamp(possibleTxDuration, maxConnectionDuration);
                            Duration minDuration = Duration.ofSeconds(1);
                            long i;
                            long possibleRxDataTransferMb;
                            List<Interval> requiredRxIntervals;
                            do { // уменьшаем пока не получится
                                Instant t0 = rightMostPointCopy.minus(duration);
                                i = duration.getSeconds() / Given.tx_speed;
                                List<Interval> rxIntervals = rxIntervalsBySatellite.get(satelliteName);
                                rxIntervals = intersection(List.of(new Interval(limits.start, t0)), rxIntervals);
                                Duration timeToTakePhotosToMatch = Duration.ofSeconds(i / rx_speed);
                                requiredRxIntervals = intervalsCutBySumFromBehind(rxIntervals, timeToTakePhotosToMatch);
                                Duration possibleRxDuration = requiredRxIntervals.stream().map(Interval::duration).reduce(Duration.ZERO, Duration::plus);
                                possibleRxDataTransferMb = possibleRxDuration.getSeconds() * rx_speed;
                                if (i != possibleRxDataTransferMb) {
                                    duration = duration.dividedBy(2);
                                }
                            } while (i != possibleRxDataTransferMb && (duration.compareTo(minDuration) >= 0));
                            if (duration.compareTo(minDuration) < 0) {
                                return RxIntervals.builder().duration(Duration.ZERO).rxIntervals(List.of()).build();
                            }
                            return RxIntervals.builder().duration(duration).rxIntervals(requiredRxIntervals).build();
                        }));
                Map.Entry<String, RxIntervals> maxDurationEntry = Collections.max(rxBySatellite.entrySet(), Map.Entry.comparingByValue(Comparator.comparing(v -> v.duration)));
                if (maxDurationEntry.getValue().duration.isZero()) {
                    // todo choose a point to move to
                    rightMostPoint = satellites.stream().map(satelliteName -> intersection(List.of(interval), satelliteFree.get(satelliteName)))
                            .flatMap(List::stream)
                            .map(i -> i.end)
                            .max(Instant::compareTo)
                            .orElse(interval.start);
                    index -= 1;
                    continue;
                }
                String satelliteName = maxDurationEntry.getKey();
                Interval txInterval = new Interval(rightMostPoint.minus(maxDurationEntry.getValue().duration), rightMostPoint);

                takeInterval.accept(satelliteName, txInterval);
                takeIntervals.accept(satelliteName, maxDurationEntry.getValue().rxIntervals);
                appendToResults.accept(satelliteName, base, txInterval);
            }
        }
        return null;
    }

    @Override
    public String name() {
        return "vlad";
    }
}
