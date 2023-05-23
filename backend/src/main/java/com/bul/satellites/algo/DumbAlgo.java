package com.bul.satellites.algo;

import com.bul.satellites.mapper.InstantToString;
import com.bul.satellites.model.*;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DumbAlgo implements Algorithm {

    @Override
    public Result apply(Given given) {
        List<Interval> listInt1 = new ArrayList<>();
        Interval int1 = Interval
                .builder()
                .start(Instant.parse("2072-12-11T21:59:59.860Z"))
                .end(Instant.parse("2072-12-11T22:59:59.860Z"))
                .build();
        Interval int2 = Interval
                .builder()
                .start(Instant.parse("2072-12-11T22:39:59.860Z"))
                .end(Instant.parse("2072-12-11T22:49:59.860Z"))
                .build();
        Interval int3 = Interval
                .builder()
                .start(Instant.parse("2072-12-10T22:59:59.860Z"))
                .end(Instant.parse("2072-12-10T23:59:59.860Z"))
                .build();
        listInt1.add(int1);
        listInt1.add(int2);
        listInt1.add(int3);
        SatelliteBasePair sb1 = SatelliteBasePair.builder()
                .base("Magadan2")
                .satellite("Kinosat1")
                .build();
        List<DurationDataset> dr = new ArrayList<>();
        DurationDataset dds1 = DurationDataset.builder()
                .entries(listInt1)
                .satelliteBasePair(sb1)
                .build();

        List<Interval> listInt2 = new ArrayList<>();
        Interval int12 = Interval
                .builder()
                .start(Instant.parse("2072-12-10T21:59:58.860Z"))
                .end(Instant.parse("2072-12-10T22:59:59.860Z"))
                .build();
        Interval int22 = Interval
                .builder()
                .start(Instant.parse("2072-12-11T21:39:58.860Z"))
                .end(Instant.parse("2072-12-11T21:49:59.860Z"))
                .build();
        Interval int32 = Interval
                .builder()
                .start(Instant.parse("2072-12-10T20:50:59.860Z"))
                .end(Instant.parse("2072-12-10T20:59:59.860Z"))
                .build();
        listInt2.add(int12);
        listInt2.add(int22);
        listInt2.add(int32);
        SatelliteBasePair sb2 = SatelliteBasePair.builder()
                .base("Magadan")
                .satellite("Kinosat2")
                .build();

        DurationDataset dds2 = DurationDataset.builder()
                .entries(listInt2)
                .satelliteBasePair(sb2)
                .build();
        dr.add(dds1);
        dr.add(dds2);
        return Result.builder()
                .datasets(dr)
                .build();
    }

    @Override
    public String name() {
        return "dumb";
    }

}
