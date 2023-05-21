package com.bul.satellites.parsing;

import com.bul.satellites.model.*;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {
    Map<String, List<List<String>>> data;

    public Parser(Map<String, List<List<String>>> data) {
        this.data = data;
    }

    DurationEntry parseToDurationEntry(List<String> row) {
        return DurationEntry.builder().start(row.get(1)).end(row.get(2)).build();
    }


    public List<DurationDataset> parse() {
        return data.entrySet().stream().map(nameToDataset -> {
                    String[] splitName = nameToDataset.getKey().split("-");
                    String base = splitName[0];
                    String satellite = splitName[2];
                    SatelliteBasePair satelliteBasePair = SatelliteBasePair.builder().base(base).satellite(satellite).build();
                    List<DurationEntry> entries = nameToDataset.getValue().stream().map(this::parseToDurationEntry).collect(Collectors.toList());
                    return DurationDataset.builder().satelliteBasePair(satelliteBasePair).entries(entries).build();
                }
        ).collect(Collectors.toList());
    }

    private List<String> parseDatasetLine(String line) {
        return Arrays.stream(line.split("  +")).filter(e -> !e.isBlank()).collect(Collectors.toList());
    }

    public Instant toInstant(String data) {
        String[] arr = data.split(" ");
        return Instant.parse(arr[2] + "-" + returnMonthNumber(arr[1]) + "-" + String.format("%02d", Integer.parseInt(arr[0])) +
                "T" + arr[3] + "Z");

    }

    public String returnMonthNumber(String str) {
        switch (str) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
        }
        return null;
    }

    public String fromInstantToString(Instant instant) {
        //"1 Jun 2027 01:32:59.157"
        //  "2018-11-30T18:35:24.000Z"
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        System.out.println("ldt day: "+ldt.getDayOfMonth());
        System.out.println("ldt month: "+ldt.getMonth().name().substring(0, 1).toUpperCase() + ldt.getMonth().name().toLowerCase().substring(1,3));
        System.out.println("ldt year: "+ldt.getYear());
        System.out.println("ldt hour: "+ldt.getHour());
        System.out.println("ldt minute: "+ldt.getMinute());
        System.out.println("ldt second: "+ldt.getSecond());
        System.out.println("ldt epoch : "+ldt.toEpochSecond(ZoneOffset.UTC));
        System.out.println("ldt nano : "+ldt.getNano());
        System.out.println("instant to milliseconds timestamp: "+ instant.toEpochMilli());
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


    DurationInstant parseToDurationInstant(List<String> row) {
        return DurationInstant.builder().start(toInstant(row.get(1)))
                .end(toInstant(row.get(2))).build();
    }
    public List<DurationInstantDataset> parseToDurationInstantDataset() {
        return data.entrySet().stream().map(nameToDataset -> {
                    String[] splitName = nameToDataset.getKey().split("-");
                    String base = splitName[0];
                    String satellite = splitName[2];
                    SatelliteBasePair satelliteBasePair = SatelliteBasePair.builder().base(base).satellite(satellite).build();
                    List<DurationInstant> entries = nameToDataset.getValue().stream().map(this::parseToDurationInstant).collect(Collectors.toList());
                    return DurationInstantDataset.builder().satelliteBasePair(satelliteBasePair).entries(entries).build();
                }
        ).collect(Collectors.toList());
    }



    }

