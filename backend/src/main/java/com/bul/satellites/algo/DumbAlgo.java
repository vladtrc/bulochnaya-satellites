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
    @Value("${path}")
    private String path;

    @Override
    public Result apply(Given given) {

//TODO - place for Result generation from some of service

        //mapping of final result to output format

        // Result result = Result.builder().build();
        // processResult(result)


        return null;
    }

    public void processResult(Result result) {
        toOutput(result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base)));
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
                FileWriter myWriter = new FileWriter("/home/badma/Загрузки/" + k + ".txt");
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
                        System.out.println("Successfully wrote to the file.");

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
}