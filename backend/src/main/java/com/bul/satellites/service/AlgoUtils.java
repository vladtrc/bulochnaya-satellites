package com.bul.satellites.service;

import com.bul.satellites.algo.AlexeyAlgo;
import com.bul.satellites.model.Interval;
import com.google.common.collect.Streams;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private static int binarySearch(List<Interval> intervals, Instant target) {
        int low = 0;
        int high = intervals.size() - 1;

        while (low <= high) {
            int middleIndex = (low + high) / 2;
            Interval interval = intervals.get(middleIndex);

            if (interval.contains(target)) {
                return middleIndex;
            }
            if (target.isBefore(interval.start)) {
                high = middleIndex - 1;
            }
            if (interval.end.isBefore(target)) {
                low = middleIndex + 1;
            }
        }
        return -1;
    }

    public static boolean intervalsContain(List<Interval> intervals, Interval target) {
        int startIndex = binarySearch(intervals, target.start);
        if (startIndex == -1) {
            return false;
        }
        Instant intervalEnd = intervals.get(startIndex).end;
        return target.end.compareTo(intervalEnd) <= 0;
    }

    public static Duration clamp(Duration target, Duration limit) {
        return target.compareTo(limit) > 0 ? limit : target;
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
            Interval rInterval = rhs.get(j);

            Instant latestStart = latest(lInterval.start, rInterval.start);
            Instant earliestEnd = earliest(lInterval.end, rInterval.end);

            if (latestStart.isBefore(earliestEnd)) {
                Interval interval = new Interval(latestStart, earliestEnd);
                if (interval.notEmpty()) {
                    res.add(interval);

                }
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
        List<AlexeyAlgo.Visibility> ls = new ArrayList<>();
        intervals.forEach(n -> {
            Instant latestStart = latest(n.interval.start, limits.start);
            Instant earliestEnd = earliest(n.interval.end, limits.end);
            if (latestStart.isBefore(earliestEnd)) {
                AlexeyAlgo.Visibility visibility = new AlexeyAlgo.Visibility(new Interval(latestStart, earliestEnd), n.base);
                ls.add(visibility);
            }
        });
        return ls;
    }


    public static List<Interval> intervalsCutBySum(List<Interval> intervals, Duration targetSum) {
        Duration sum = Duration.ZERO;
        ArrayList<Interval> res = new ArrayList<>();
        for (Interval interval : intervals) {
            sum = sum.plus(interval.duration());
            if (sum.compareTo(targetSum) <= 0 && interval.notEmpty()) { // sum <= targetSum  todo BUG?
                res.add(interval);
            } else {
                Instant rightBound = interval.end.minus(sum).plus(targetSum);
                Interval lastInterval = Interval.builder().start(interval.start).end(rightBound).build();
                if (lastInterval.notEmpty())
                    res.add(lastInterval);
                break;
            }
        }
        return res;
    }

    public static List<Interval> intervalsCutBySumFromBehind(List<Interval> intervals, Duration targetSum) {
        Duration sum = Duration.ZERO;
        ArrayList<Interval> res = new ArrayList<>();
        List<Interval> intervalsReversed = new ArrayList<>(intervals.stream().toList());
        Collections.reverse(intervalsReversed);
        for (Interval interval : intervalsReversed) {
            sum = sum.plus(interval.duration());
            if (sum.compareTo(targetSum) <= 0) {
                if (interval.notEmpty()) {
                    res.add(interval);
                }
            } else {
                Instant leftBound = interval.start.plus(sum).minus(targetSum);
                Interval lastInterval = new Interval(leftBound, interval.end);
                if (lastInterval.notEmpty()) {
                    res.add(lastInterval);
                }
                break;
            }
        }
        return res.stream().sorted(Comparator.comparing(i -> i.start)).collect(Collectors.toList());
    }

    public static boolean intervalsContain(List<Interval> intervals, Instant target) {
        return intervals.stream().anyMatch(e -> e.contains(target));
    }
}
