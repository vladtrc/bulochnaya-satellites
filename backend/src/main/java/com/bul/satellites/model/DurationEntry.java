package com.bul.satellites.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public class DurationEntry {
    public Instant start;
    public Instant end;
}