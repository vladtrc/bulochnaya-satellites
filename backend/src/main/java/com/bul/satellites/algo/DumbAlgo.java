package com.bul.satellites.algo;

import com.bul.satellites.mapper.InstantToString;
import com.bul.satellites.mapper.StringToInstant;
import com.bul.satellites.model.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class DumbAlgo implements Algorithm {
    @Override
    public Result apply(Given given) {
//TODO - place for Result generation from some of service

        //mapping of final result to output format

        Result result = Result.builder().build();

//result.datasets.stream().collect(groupingBy(e -> e.satelliteBasePair.base)).e.entries.stream().collect(groupingBy(t->t.start))));
        //      datasets.collect(groupingBy(e -> e.satelliteBasePair.base))
        result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base,
                Collectors.groupingBy(e -> e.entries.stream().collect(groupingBy(t -> t.start)))));

        result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base));

        return null;
    }

    @Override
    public String name() {
        return "dumb";
    }


    public void toOutput() throws IOException {
        Output output1 = new Output();
        output1.setAccess("1");
        output1.setVolume("100");
        output1.setStart(Instant.parse("2072-12-12T10:12:25.224Z"));
        output1.setEnd(Instant.parse("2072-12-12T10:42:25.290Z"));
        output1.setSatelliteName("kinosat1");
        output1.setBase("Anadyr");
        Output output2 = new Output();
        output2.setAccess("2");
        output2.setStart(Instant.parse("2072-12-11T23:59:59.860Z"));
        output2.setEnd(Instant.parse("2072-12-12T00:00:00.800Z"));
        output2.setVolume("200");
        output2.setSatelliteName("kinosat2");
        output2.setBase("Magadan");
        List<Output> list = new ArrayList<>();
        list.add(output1);
        list.add(output2);

        StringToInstant st = new StringToInstant();
        InstantToString ts = new InstantToString();

        list.forEach(n -> {

            try {
                FileWriter myWriter = new FileWriter("/home/badma/Загрузки/" + n.getBase() + ".txt");
                myWriter.write("Access * Start Time (UTCG) * Stop Time (UTCG) * Duration (sec) * Satname * Data (Mbytes)");
                myWriter.write("\r\n");
                myWriter.write("\r\n");
                for (Output ot : list) {
                    myWriter.write(ot.getAccess() + "  " + ts.fromInstantToString(ot.getStart()) + "  " +
                            ts.fromInstantToString(ot.getEnd()) + "  " +
                            (Duration.between(ot.getStart(), ot.getEnd()).toSeconds()) + "." +
                            (Duration.between(ot.getStart(), ot.getEnd()).toMillisPart()) + "  " +
                            ot.getSatelliteName() + "  " + ot.getVolume());
                    myWriter.write("\r\n");
                }
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

        });

    }

    //  List<Output> output = result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base,
    //           Collectors.groupingBy(e -> e.entries.stream().collect(groupingBy(t -> t.start)))));


}