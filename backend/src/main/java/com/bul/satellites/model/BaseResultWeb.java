package com.bul.satellites.model;

import lombok.Builder;

import java.util.List;

@Builder
public class BaseResultWeb {
    public String base;
    public List<BaseUsageResult> usage;
}
