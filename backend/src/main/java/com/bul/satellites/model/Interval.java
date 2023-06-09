package com.bul.satellites.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

@Builder
@Getter
@AllArgsConstructor
public class Interval {
    public Instant start;
    public Instant end;

    public Duration duration() {
        return Duration.between(start, end);
    }

    public boolean notEmpty() {
        return !duration().isZero();
    }
    public boolean contains(Instant target) {
        return (start.compareTo(target) <= 0) && (target.compareTo(end) <= 0);
    }

    @Override
    public String toString() {
        return start.toString() + "," + end.toString();
    }
}
