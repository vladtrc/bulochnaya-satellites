package com.bul.satellites;

import com.bul.satellites.algo.AlexeyAlgo;
import com.bul.satellites.algo.LibraryAPI;
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



@SpringBootTest
@ComponentScan("com.bul.satellites.mapper")
class RawDataToDurationDatasetsMapperRawIT {
   // @Autowired
  //  GivenLoader loader;
    InstantToString ts = new InstantToString();

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

//    @Test
//    void testGivenLoaderToTxt() throws IOException {
//        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
//        toOneTxt(loader.getGiven().getAvailabilityRussia(), "/home/badma/Загрузки/output/Aleksey_algo/");
//    }

//    @Test
//    void testBorders() throws IOException {
//        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
//        Result result = new AlexeyAlgo().apply(loader.getGiven());
//        LimitValidator lm = new LimitValidator(loader.getGiven());
//        lm.validate(result);
//    }
//
//    @Test
//    void tesRussia() throws IOException {
//        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
//        Result result = new AlexeyAlgo().apply(loader.getGiven());
//        LimitValidator lm = new LimitValidator(loader.getGiven());
//        lm.validate(result);
//    }

    @Test
    void testLibraryAPI() throws IOException {
        LibraryAPI la=new LibraryAPI();
       la.algoOutput("/home/badma/Загрузки/output/Aleksey_algo_mbytes/", "/home/badma/Загрузки/DATA_Files/Facility2Constellation/",
                "/home/badma/Загрузки/DATA_Files/Russia2Constellation2/");

        //la.algoOutput("/home/badma/Загрузки/output/Aleksey_algo/");
    }
}