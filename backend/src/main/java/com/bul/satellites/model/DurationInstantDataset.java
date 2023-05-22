package com.bul.satellites.model;

import lombok.Builder;

import java.util.List;
@Builder
public class DurationInstantDataset {

        public SatelliteBasePair satelliteBasePair;
        public List<DurationInstant> entries; // todo fast search
    }

