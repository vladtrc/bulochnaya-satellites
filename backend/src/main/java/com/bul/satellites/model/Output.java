package com.bul.satellites.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

@Builder
@Getter
@AllArgsConstructor
public class Output {
    private String access;
    private Instant start;
    private Instant end;
    private Duration duration;
    private String base;
    private String satellite;
    private String volume;
}

