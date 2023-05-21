package com.bul.satellites.model;

import lombok.Builder;

import java.time.Instant;
@Builder
public class DurationInstant {
    Instant start;
    Instant end;
}
