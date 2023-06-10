package com.bul.satellites.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Params {
    public Interval limits;
    public long tx_speedC;  // Мегабит/сек отправка на Землю //125 мегабайт
    public long tx_speed;  // Мегабит/сек отправка на Землю//31,25 мегабайт
    public long rx_speed;  // Мегабит/сек фотографирование//500 мегабайт
    public long memory_limit;  // Мегабит (1 Терабайт)
    public long memory_limit2;

}
