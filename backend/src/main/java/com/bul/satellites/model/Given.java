package com.bul.satellites.model;

import com.bul.satellites.model.DurationDataset;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Data
@Builder
public class Given {
    Map<String, List<DurationDataset>> availabilityByBase;
    Map<String, List<DurationDataset>> availabilityBySatellite;
    Map<String, List<DurationDataset>> availabilityRussia;

    // todo add static constrains like bandwidth

    public static SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS", Locale.US);
}
