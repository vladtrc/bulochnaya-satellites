package com.bul.satellites.mapper;

import com.bul.satellites.model.Given;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

public class InstantToString implements Function<Instant, String> {
    @Override
    public String apply(Instant instant) {
        return Given.format.format(instant);
    }

    public String fromInstantToString(Instant instant) {
        //"1 Jun 2027 01:32:59.157"
        //  "2018-11-30T18:35:24.000Z"
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        String day= String.valueOf(ldt.getDayOfMonth());
        String month=ldt.getMonth().name().substring(0, 1).toUpperCase() + ldt.getMonth().name().toLowerCase().substring(1,3);
        String year= String.valueOf(ldt.getYear());
        String hour= String.format("%02d",ldt.getHour());
        String minute= String.format("%02d",ldt.getMinute());
        String second= String.format("%02d",ldt.getSecond());
        //String milli=  String.format("%03d",ldt.toEpochSecond(ZoneOffset.UTC)).substring(0,3);
        String milliseconds= String.valueOf(instant.toEpochMilli());
        String milli= milliseconds.substring(milliseconds.length()-3);
        return day+" "+month+" "+year+" "+hour+":"+minute+":"+second+"."+milli;
    }

}
