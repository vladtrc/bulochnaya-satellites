package com.bul.satellites.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

@Builder
@Getter
public class Interval {
    public Instant start;
    public Instant end;

    public Duration duration() {
        return Duration.between(start, end);
    }



}
