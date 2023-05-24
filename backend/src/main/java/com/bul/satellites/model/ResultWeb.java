package com.bul.satellites.model;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public class ResultWeb {
    public List<BaseResultWeb> results;
    public Instant start;
    public Instant end;
}
