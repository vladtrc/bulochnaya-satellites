package com.bul.satellites.service;

import com.bul.satellites.model.Interval;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AlgoUtils {
    static List<Interval> intervalsComplement(List<Interval> intervals, Interval limits) {
        if (intervals.isEmpty()) {
            return List.of(limits);
        }
        return null; // todo
    }

    static List<Interval> intersection(List<Interval> lhs, List<Interval> rhs) {
        return null;
    }

    static Duration sumOverIntervals(List<Interval> intervals) {
        return intervals.stream().map(Interval::duration).reduce(Duration.ZERO, Duration::plus);
    }

    static List<Interval> intervalsCut(List<Interval> intervals, Interval limits) {
        // todo gotta connect w base
        return null;
    }

    static List<Interval> intervalsCutBySum(List<Interval> intervals, Duration targetSum) {
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

    static boolean intervalsContain(List<Interval> intervals, Instant target) {
        return intervals.stream().anyMatch(e -> (e.start.compareTo(target) <= 0) && (target.compareTo(e.end) <= 0));
    }
}
