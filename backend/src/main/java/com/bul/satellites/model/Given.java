package com.bul.satellites.model;

import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.Instant;
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
   public static Interval limits = new Interval(Instant.parse("2027-06-01T00:00:00Z"), Instant.parse("2027-06-14T00:00:00Z"));
  //  public Interval limits;

    public static long tx_speedC = 1000;  // Мегабит/сек отправка на Землю //125 мегабайт
    public static long tx_speed = 250;  // Мегабит/сек отправка на Землю//31,25 мегабайт
    public static long rx_speed = 4000;  // Мегабит/сек фотографирование//500 мегабайт
    public static long memory_limit = 8000000;  // Мегабит (1 Терабайт)
    public static long memory_limit2 = 4000000;
    //public long tx_speed;  // Мегабит/сек отправка на Землю
  //  public long rx_speed;  // Мегабит/сек фотографирование
   // public long memory_limit;  // Мегабит (1 Терабайт)


    public static SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS", Locale.US);


}
