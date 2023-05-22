package com.bul.satellites.algo;

import com.bul.satellites.mapper.InstantToString;
import com.bul.satellites.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DumbAlgo implements Algorithm {
    //todo @Value does not work
    //  @Value("${path:/home/badma/Загрузки/}")
    private String path = "/home/badma/Загрузки/";

    @Override
    public Result apply(Given given) {

//TODO - place for Result generation from some of service

        //mapping of final result to output format

        // Result result = Result.builder().build();
        // processResult(result)


        return null;
    }

    //todo volume(sec*Gb) is null yet
    public void processResult(Result result) {
        toConsole(result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base)));
    }

    @Override
    public String name() {
        return "dumb";
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
}
