package com.bul.satellites;

import lombok.Builder;

import java.util.List;

@Builder
public class DurationDataset {
    SatelliteBasePair satelliteBasePair;
    List<DurationEntry> entries; // todo fast search
}
