package com.bul.satellites.model;

import lombok.Builder;

import java.time.Duration;
import java.time.Instant;

@Builder
public class Interval {
    public Instant start;
    public Instant end;

    public Duration duration() {
        return Duration.between(start, end);
    }
}
