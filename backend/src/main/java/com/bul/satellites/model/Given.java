package com.bul.satellites.model;

import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Data
@Builder
public class Given {
    public Map<String, List<DurationDataset>> availabilityByBase;
    public Map<String, List<DurationDataset>> availabilityBySatellite;
    public Map<String, List<DurationDataset>> availabilityRussia;

    // todo add static constrains like bandwidth
    Interval interval;


    public long tx_speed = 250;  // Мегабит/сек отправка на Землю
    public long rx_speed = 4000;  // Мегабит/сек фотографирование
    public long memory_limit = 8000000;  // Мегабит (1 Терабайт)

    public static SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS", Locale.US);
}
