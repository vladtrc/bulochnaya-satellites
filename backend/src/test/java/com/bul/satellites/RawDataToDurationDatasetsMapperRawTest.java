package com.bul.satellites;

import com.bul.satellites.algo.DumbAlgo;
import com.bul.satellites.mapper.InstantToString;
import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.model.Result;
import com.bul.satellites.service.GivenLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SpringBootTest
@ComponentScan("com.bul.satellites.mapper")
class RawDataToDurationDatasetsMapperRawTest {
    @Autowired
    GivenLoader loader;

    String path = "";

    @Test
    void test() throws IOException {
        // GivenLoader loader = new GivenLoader();

        //System.out.println(loader.getGiven().getAvailabilityRussia());

        // da.toOutput();
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
}