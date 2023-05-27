package com.bul.satellites.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class SatelliteBasePair {
    public String satellite;
    public String base;

    @Override
    public String toString() {
        return "SatelliteBasePair{" +
                "satellite='" + satellite + '\'' +
                ", base='" + base + '\'' +
                '}';
    }
}
