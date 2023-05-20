package com.bul.satellites;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AlgorithmGiven {
    Map<String, List<DurationDataset>> availabilityByBase;
    Map<String, List<DurationDataset>> availabilityBySatellite;
    Map<String, List<DurationDataset>> availabilityRussia;

    // todo add static constrains like bandwidth
}
