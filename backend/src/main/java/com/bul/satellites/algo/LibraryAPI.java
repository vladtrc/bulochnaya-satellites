package com.bul.satellites.algo;

import com.bul.satellites.mapper.InstantToString;
import com.bul.satellites.mapper.RawDataToDurationDatasets;
import com.bul.satellites.mapper.StringToInstant;
import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.Output;
import com.bul.satellites.model.Result;
import com.bul.satellites.service.GivenLoader;
import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class LibraryAPI {
    private static final Logger logger = LoggerFactory.getLogger(LibraryAPI.class);
    double totalVolume = 0;
    int totalVolumeByTime = 0;
    InstantToString ts = new InstantToString();

    public void algoOutput(String path, String pathFacility, String pathRussia) throws IOException {
        //public void algoOutput(String path) throws IOException {
        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()), pathFacility, pathRussia);
        // GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
        Result result = new AlexeyAlgo().apply(loader.getGiven());
        processResult(result, path);
    }

    public void processResult(Result result, String path) {
        Map<String, List<DurationDataset>> mp2;
        mp2 = result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base));
        toOutput(mp2, path);
    }

    public void toOutput(Map<String, List<DurationDataset>> map, String path) {
        toSortedOutputList(map).stream().collect(Collectors.groupingBy(Output::getBase)).forEach((k, v) -> {
            logger.info(k + " в выходном листе: " + v.size());
            try {
                FileWriter myWriter = new FileWriter(path + k + ".txt");
                myWriter.write("Access * Start Time (UTCG) * Stop Time (UTCG) * Duration (sec) * Satname * Data (Mbytes)");
                myWriter.write("\r\n");
                myWriter.write("\r\n");
                Stream<Integer> range = IntStream.range(1, v.size() + 1).boxed();

                Streams.zip(v.stream(), range, (p, i) -> {
                    extracted(myWriter, p, i);
                    return 1;

                }).collect(Collectors.toSet());


                myWriter.close();
            } catch (IOException e) {
                logger.error("An error occurred.");
                e.printStackTrace();
            }
        });
        logger.info("total volume: "+ String.format("%.2f", Double.valueOf(totalVolume)));
    }

    private void extracted(FileWriter myWriter, Output p, int i) {
        totalVolume = totalVolume + Double.valueOf((p.getDuration().toSeconds()) + "." + (p.getDuration().
                toMillisPart())) * ((Integer.parseInt(p.getSatellite().substring(p.getSatellite().length() - 6)) > 111510) ? Given.tx_speedC : Given.tx_speed);
        try {
            myWriter.write(i + "  " + ts.fromInstantToString(p.getStart()) + "  " +
                    ts.fromInstantToString(p.getEnd()) + "  " +
                    (p.getDuration().toSeconds()) + "." + (p.getDuration().
                    toMillisPart()) + "  " +
                    p.getSatellite() + "  " + String.format("%.2f", Double.valueOf((p.getDuration().toSeconds()) + "." + (p.getDuration().
                    toMillisPart())) * ((Integer.parseInt(p.getSatellite().substring(p.getSatellite().length() - 6)) > 111510) ? Given.tx_speedC : Given.tx_speed)));
            myWriter.write("\r\n");

        } catch (IOException e) {
            logger.error("An error occurred.");
            e.printStackTrace();
        }
    }

    public List<Output> toSortedOutputList(Map<String, List<DurationDataset>> map) {
        List<Output> op = new ArrayList<>();
        map.forEach((k, v) -> {
                    logger.info(k + " в входном листе: " + v.size());
                    v.forEach(p -> p.entries.forEach(t -> {

                        Output output = new Output("Access", t.start, t.end, t.duration(), p.satelliteBasePair.base, p.satelliteBasePair.satellite, "volume");
                        op.add(output);
//                        logger.info("Access" + "  " + ts.fromInstantToString(t.start) + "  " +
//                                ts.fromInstantToString(t.end) + "  " +
//                                (Duration.between(t.start, t.end).toSeconds()) + "." + (Duration.between(t.start, t.end).
//                                toMillisPart()) + "  " +
//                                p.satelliteBasePair.satellite + "  " + "volume");
                    }));
                }
        );
        logger.info("Кол-во записей: " + op.size());
        Comparator<Output> compareByBase = Comparator.comparing(Output::getBase);
        Comparator<Output> compareByBaseAndStart = compareByBase.thenComparing(Output::getStart);
        return op.stream().sorted(compareByBaseAndStart).toList();
    }

}
