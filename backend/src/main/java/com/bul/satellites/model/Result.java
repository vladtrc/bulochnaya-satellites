package com.bul.satellites.model;

import lombok.Builder;

import java.util.List;

@Builder
public class Result {
    public List<DurationDataset> datasets; // todo fast search
}
