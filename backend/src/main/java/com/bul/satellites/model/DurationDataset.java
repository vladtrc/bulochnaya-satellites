package com.bul.satellites.model;

import lombok.Builder;

import java.util.List;



@Builder
public class DurationDataset {
    public SatelliteBasePair satelliteBasePair;
    public List<Interval> entries; // todo fast search

    @Override
    public String toString() {
        return "DurationDataset{" +
                "satelliteBasePair=" + satelliteBasePair +
                ", entries=" + entries +
                '}';
    }
}
