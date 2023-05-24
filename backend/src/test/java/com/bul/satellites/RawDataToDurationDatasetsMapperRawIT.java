package com.bul.satellites;

import com.bul.satellites.algo.DumbAlgo;
import com.bul.satellites.mapper.InstantToString;
import com.bul.satellites.mapper.RawDataToDurationDatasets;
import com.bul.satellites.mapper.StringToInstant;
import com.bul.satellites.model.*;
import com.bul.satellites.service.GivenLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SpringBootTest
@ComponentScan("com.bul.satellites.mapper")
class RawDataToDurationDatasetsMapperRawIT {
    @Autowired
    GivenLoader loader;

    String path = "/home/badma/Загрузки/";

    @Test
    void test() throws IOException {
       //   GivenLoader loader = new GivenLoader();

      //  loader.getGiven().getAvailabilityRussia();

    }

    //todo volume(sec*Gb) is null yet
    public void processResult(Result result) {
        toConsole(result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base)));
    }


    public void toOutput(Map<String, List<DurationDataset>> map) {

        InstantToString ts = new InstantToString();
        map.forEach((k, v) -> {
            try {
                System.out.println(path + k + ".txt");
                FileWriter myWriter = new FileWriter(path + k + ".txt");
                myWriter.write("Access * Start Time (UTCG) * Stop Time (UTCG) * Duration (sec) * Satname * Data (Mbytes)");
                myWriter.write("\r\n");
                myWriter.write("\r\n");

                v.forEach(p -> p.entries.forEach(t -> {
                    try {
                        myWriter.write("Access" + "  " + ts.fromInstantToString(t.start) + "  " +
                                ts.fromInstantToString(t.end) + "  " +
                                (Duration.between(t.start, t.end).toSeconds()) + "." + (Duration.between(t.start, t.end).
                                toMillisPart()) + "  " +
                                p.satelliteBasePair.satellite + "  " + "volume");
                        myWriter.write("\r\n");
                        System.out.println("Access" + "  " + ts.fromInstantToString(t.start) + "  " +
                                ts.fromInstantToString(t.end) + "  " +
                                (Duration.between(t.start, t.end).toSeconds()) + "." + (Duration.between(t.start, t.end).
                                toMillisPart()) + "  " +
                                p.satelliteBasePair.satellite + "  " + "volume");

                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                }));
                myWriter.close();

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        });
    }
    public void toOneTxt(Map<String, List<DurationDataset>> map) {

        InstantToString ts = new InstantToString();
       // map.forEach((k, v) -> {
            try {
                //System.out.println("Satellites.txt");
                FileWriter myWriter = new FileWriter(path+"RussiaCoverage.txt");
                myWriter.write("Base * Start Time (UTCG) * Stop Time (UTCG) * Duration (sec) * Satname * Data (Mbytes)");
                myWriter.write("\r\n");
                myWriter.write("\r\n");

                map.forEach((k, v) -> v.forEach(p -> p.entries.forEach(t -> {
                    try {
                        myWriter.write(k + "  " + ts.fromInstantToString(t.start) + "  " +
                                ts.fromInstantToString(t.end) + "  " +
                                (Duration.between(t.start, t.end).toSeconds()) + "." + (Duration.between(t.start, t.end).
                                toMillisPart()) + "  " +
                                p.satelliteBasePair.satellite + "  " + "volume");
                        myWriter.write("\r\n");

                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                })));
                myWriter.close();

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        };
   // }

    public void toConsole(Map<String, List<DurationDataset>> map) {

        InstantToString ts = new InstantToString();
        map.forEach((k, v) -> {
            System.out.println(path + k + ".txt");
            System.out.println("Access * Start Time (UTCG) * Stop Time (UTCG) * Duration (sec) * Satname * Data (Mbytes)");

            v.forEach(p -> p.entries.forEach(t -> {

                System.out.println("Access" + "  " + ts.fromInstantToString(t.start) + "  " +
                        ts.fromInstantToString(t.end) + "  " +
                        (Duration.between(t.start, t.end).toSeconds()) + "." + (Duration.between(t.start, t.end).
                        toMillisPart()) + "  " +
                        p.satelliteBasePair.satellite + "  " + "volume");
            }));
        });
    }

    @Test
    public void testProcessResult() throws IOException {
        Result result = Result.builder().build();
        DumbAlgo da = new DumbAlgo();
        processResult(result);
    }

    @Test
    void testGivenLoaderToTxt() throws IOException {
        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()),new StringToInstant());

        toOneTxt(loader.getGiven().getAvailabilityRussia());


    }

    @Test
    void testBorders(){
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
        Result.builder()
                .datasets(dr)
                .build();

        Interval intGiven = Interval
                .builder()
                .start(Instant.parse("2072-12-10T20:50:59.860Z"))
                .end(Instant.parse("2072-12-10T20:59:59.860Z"))
                .build();



    }

}