package com.bul.satellites;

import com.bul.satellites.algo.AlexeyAlgo;
import com.bul.satellites.algo.DumbAlgo;
import com.bul.satellites.mapper.InstantToString;
import com.bul.satellites.mapper.RawDataToDurationDatasets;
import com.bul.satellites.mapper.StringToInstant;
import com.bul.satellites.model.*;
import com.bul.satellites.service.GivenLoader;
import com.bul.satellites.validators.LimitValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
@ComponentScan("com.bul.satellites.mapper")
class RawDataToDurationDatasetsMapperRawIT {
    @Autowired
    GivenLoader loader;
    InstantToString ts = new InstantToString();

    // String path = "/home/badma/Загрузки/output/Aleksey_algo/";

    @Test
    void test() throws IOException {
        //   GivenLoader loader = new GivenLoader();

        //  loader.getGiven().getAvailabilityRussia();
    }

    public void processResult(Result result, String path) {
        Map<String, List<DurationDataset>> mp2;
        mp2 = result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base));
        toOutput(mp2, path);
    }


    public void toOutput(Map<String, List<DurationDataset>> map, String path) {
        toSortedOutputList(map).stream().collect(Collectors.groupingBy(Output::getBase)).forEach((k, v) -> {
            System.out.println(k + " в входном листе: " + v.size());
            try {
                FileWriter myWriter = new FileWriter(path + k + ".txt");
                myWriter.write("Access * Start Time (UTCG) * Stop Time (UTCG) * Duration (sec) * Satname * Data (Mbytes)");
                myWriter.write("\r\n");
                myWriter.write("\r\n");
                v.forEach(p -> {
                    try {
                        myWriter.write("Access" + "  " + ts.fromInstantToString(p.getStart()) + "  " +
                                ts.fromInstantToString(p.getEnd()) + "  " +
                                (p.getDuration().toSeconds()) + "." + (p.getDuration().
                                toMillisPart()) + "  " +
                                p.getSatellite() + "  " + String.format("%.2f", Double.valueOf((p.getDuration().toSeconds()) + "." + (p.getDuration().
                                toMillisPart())) * Given.tx_speed));
                        myWriter.write("\r\n");

                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                });
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        });
    }

    public List<Output> toSortedOutputList(Map<String, List<DurationDataset>> map) {
        List<Output> op = new ArrayList<>();
        map.forEach((k, v) -> {
                    v.forEach(p -> p.entries.forEach(t -> {
                        Output output = new Output("Access", t.start, t.end, t.duration(), p.satelliteBasePair.base, p.satelliteBasePair.satellite, "volume");
                        op.add(output);
//                        System.out.println("Access" + "  " + ts.fromInstantToString(t.start) + "  " +
//                                ts.fromInstantToString(t.end) + "  " +
//                                (Duration.between(t.start, t.end).toSeconds()) + "." + (Duration.between(t.start, t.end).
//                                toMillisPart()) + "  " +
//                                p.satelliteBasePair.satellite + "  " + "volume");
                    }));
                }

        );
        System.out.println("op.size: " + op.size());
        Comparator<Output> compareByBase = Comparator.comparing(Output::getBase);
        Comparator<Output> compareByBaseAndStart = compareByBase.thenComparing(Output::getStart);
        return op.stream().sorted(compareByBaseAndStart).toList();
    }

    public void toOneTxt(Map<String, List<DurationDataset>> map, String path) {
        InstantToString ts = new InstantToString();
        // map.forEach((k, v) -> {
        try {
            //System.out.println("Satellites.txt");
            FileWriter myWriter = new FileWriter(path + "RussiaCoverage.txt");
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
    }

    public void toConsole(Map<String, List<DurationDataset>> map, String path) {

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
    void testGivenLoaderToTxt() throws IOException {
        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
        toOneTxt(loader.getGiven().getAvailabilityRussia(), "/home/badma/Загрузки/output/Aleksey_algo/");
    }

    @Test
    void testBorders() throws IOException {
        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
        Result result = new AlexeyAlgo().apply(loader.getGiven());
        LimitValidator lm = new LimitValidator(loader.getGiven());
        lm.validate(result);
    }

    @Test
    void tesRussia() throws IOException {
        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
        Result result = new AlexeyAlgo().apply(loader.getGiven());
        LimitValidator lm = new LimitValidator(loader.getGiven());
        lm.validate(result);
    }

    @Test
    void testAlekseyOutput() throws IOException {
        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
        Result result = new AlexeyAlgo().apply(loader.getGiven());
        processResult(result, "/home/badma/Загрузки/output/Aleksey_algo/");
    }
}