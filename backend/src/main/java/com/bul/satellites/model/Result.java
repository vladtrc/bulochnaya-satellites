package com.bul.satellites.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
@AllArgsConstructor
public class Result {
    public List<DurationDataset> datasets; // todo fast search
}
