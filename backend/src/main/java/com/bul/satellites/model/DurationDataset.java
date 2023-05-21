package com.bul.satellites.model;

import lombok.Builder;

import java.util.List;

@Builder
public class DurationDataset {
    public SatelliteBasePair satelliteBasePair;
    public List<DurationEntry> entries; // todo fast search
}
