package com.bul.satellites.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public class BaseUsageResult {
    public String satelliteName;
    public Instant start;
    public Instant end;
}
