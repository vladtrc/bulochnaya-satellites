package com.bul.satellites.service;

import com.bul.satellites.algo.AlexeyAlgo;
import com.bul.satellites.model.Interval;
import com.google.common.collect.Streams;


import java.beans.Visibility;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AlgoUtils {
    public static List<Interval> intervalsComplement(List<Interval> intervals, Interval limits) {
        Stream<Instant> starts = Stream.concat(Stream.of(limits.start), intervals.stream().map(Interval::getEnd));
        Stream<Instant> ends = Stream.concat(intervals.stream().map(Interval::getStart), Stream.of(limits.end));
        return Streams
                .zip(starts, ends, (start, end) -> Interval.builder().start(start).end(end).build())
                .collect(Collectors.toList());
    }

    public static Instant latest(Instant lhs, Instant rhs) {
        return lhs.isAfter(rhs) ? lhs : rhs;
    }

    public static Instant earliest(Instant lhs, Instant rhs) {
        return lhs.isBefore(rhs) ? lhs : rhs;
    }

    public static Duration min(Duration a, Duration b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    public static List<Interval> intersection(List<Interval> lhs, List<Interval> rhs) {
        int i = 0;
        int j = 0;
        int n = lhs.size();
        int m = rhs.size();

        List<Interval> res = new ArrayList<>(lhs.size() + rhs.size());
        while ((i < n) && (j < m)) {
            Interval lInterval = lhs.get(i);
            Interval rInterval = lhs.get(j);

            Instant latestStart = latest(lInterval.start, rInterval.start);
            Instant earliestEnd = earliest(rInterval.end, rInterval.end);

            if (latestStart.isBefore(earliestEnd)) {
                res.add(Interval.builder().start(latestStart).end(earliestEnd).build());
            }

            if (lInterval.end.isBefore(rInterval.end)) {
                i += 1;
            } else {
                j += 1;
            }
        }

        return res;
    }

    public static Duration sumOverIntervals(List<Interval> intervals) {
        return intervals.stream().map(Interval::duration).reduce(Duration.ZERO, Duration::plus);
    }

    public static List<AlexeyAlgo.Visibility> intervalsCut(List<AlexeyAlgo.Visibility> intervals, Interval limits) {
        // todo gotta connect w base
        List<AlexeyAlgo.Visibility> ls = new ArrayList<>();


        intervals.forEach(n -> {

            Instant latestStart = latest(n.interval.start, limits.start);
            Instant earliestEnd = earliest(n.interval.end, limits.end);

            if (latestStart.isBefore(earliestEnd)) {

                ls.add(new AlexeyAlgo.Visibility(Interval.builder().start(latestStart).end(earliestEnd).build(), n.base));
                System.out.println("size: "+ls.size());
            }
        });
         ls.forEach(System.out::println);
        return ls;
    }


    public static List<Interval> intervalsCutBySum(List<Interval> intervals, Duration targetSum) {
        Duration sum = Duration.ZERO;
        ArrayList<Interval> res = new ArrayList<>();
        for (Interval interval : intervals) {
            sum = sum.plus(interval.duration());
            if (sum.compareTo(targetSum) <= 0) { // sum <= targetSum
                res.add(interval);
            } else {
                Instant rightBound = interval.end.minus(sum).plus(targetSum);
                Interval lastInterval = Interval.builder().start(interval.start).end(rightBound).build();
                res.add(lastInterval);
            }
        }
        return res;
    }

    public static boolean intervalsContain(List<Interval> intervals, Instant target) {
        return intervals.stream().anyMatch(e -> (e.start.compareTo(target) <= 0) && (target.compareTo(e.end) <= 0));
    }
}
