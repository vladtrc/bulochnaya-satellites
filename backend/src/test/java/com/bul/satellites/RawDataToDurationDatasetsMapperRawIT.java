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

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;


@SpringBootTest
@ComponentScan("com.bul.satellites.mapper")
class RawDataToDurationDatasetsMapperRawIT {
    @Autowired
    GivenLoader loader;
    InstantToString ts = new InstantToString();

    String path = "/home/badma/Загрузки/output/Aleksey_algo/";

    @Test
    void test() throws IOException {
        //   GivenLoader loader = new GivenLoader();

        //  loader.getGiven().getAvailabilityRussia();

    }

    //todo volume(sec*Gb) is null yet
    public void processResult(Result result) {
        Map<SatelliteBasePair, List<DurationDataset>> mp = new HashMap<>();
        Map<String, List<DurationDataset>> mp2 = new HashMap<>();
//result.datasets.forEach(System.out::println);
        //    mp= result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair));
        //    mp.forEach((key, value) -> System.out.println("КЛЮЧ!!!!!!: "+key + " " + value+" ЗНАЧЕНИЕ"));
//        Map<String, List<DurationDataset>> finalMp = new HashMap<>();
//


//        result.datasets.stream()
//                .map(e -> e.satelliteBasePair)
//                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).forEach((key, value) -> System.out.println("КЛЮЧ!!!!!!: "+key + " " + value+" ЗНАЧЕНИЕ"));
//        long i= mp.keySet().stream().distinct().count();
//          System.out.println(i);
//        mp=result.datasets.stream()
//                .collect(toMap(e -> e.satelliteBasePair.base, Function.identity(), (first, second) -> {
//                    String locations = first.getLocatedIn() + " and " + second.getLocatedIn();
//
//
//                    return new DurationDataset(first.getName(), locations);
//                }));
//        mp= result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base));
//        mp.entrySet().stream().
//
//
//
//                System.out.println("КЛЮЧ!!!!!!: "+key + " " + value+" ЗНАЧЕНИЕ"));

        //  mp.forEach((k, v) -> {
        //               System.out.println("база:" + k.base+k.satellite);
        //               System.out.println("v:" + v.size());
        //System.out.println("спутник:" + l.satelliteBasePair.satellite);
        // finalMp.merge(k,v, h->h.forEach(t->t.entries.stream().map(List::add));

//                    v.stream().(t -> {
//
//                    });
        //          });
        //   toOutput(finalMp);


//List<DurationDataset> ld=result.datasets;
//
//        for (DurationDataset element : ld){
//            System.out.println("в датасете интервалов: "+element.entries.size());
//        }
//        System.out.println("всего датасетов: "+ld.size());
        mp2 = result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.base));

//
//                .forEach((k, v) -> {
//                    System.out.println("база:" + k);
//                    System.out.println("v:" + v.size());
//                    //System.out.println("спутник:" + l.satelliteBasePair.satellite);
//                    //
//
//                    v.forEach(t -> {
//                        System.out.println("в входном листе: " +t.entries.size());
//                        SatelliteBasePair sp = t.satelliteBasePair;
//
//                     //   System.out.println("в выходном листе: " + ls.size());
//
//                        List<DurationDataset>dtlist = new ArrayList<>();
//                        dtlist.add(dt);
//
//                        BiFunction<List<Interval>, List<Interval>, List<Interval>> listCollapse = (intervals1, intervals2) -> {
//                            List<Interval> list = new ArrayList<>();
//
//                            list.addAll(intervals1);
//                            list.addAll(intervals2);
//                            list = list.stream().sorted(Comparator.comparing(Interval::getStart)).toList();
//                            return list;
//
//                        };
//                        DurationDataset dt = DurationDataset.builder()
//                                .satelliteBasePair(sp)
//                                .entries(listCollapse()).build();
//                        mp2.merge(k.base, dt,listCollapse);

        //     });
        //       });
        toOutput(toSortedOutputList(mp2));
        //  toOutput(mp2);
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

    public void toOutput(List<Output> list) {
        list.stream().collect(Collectors.groupingBy(Output::getBase)).forEach((k, v) -> {
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
                                p.getSatellite() + "  " + "volume");
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

    public void toOneTxt(Map<String, List<DurationDataset>> map) {

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
        GivenLoader loader = new GivenLoader(new RawDataToDurationDatasets(new StringToInstant()));
        toOneTxt(loader.getGiven().getAvailabilityRussia());
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
        processResult(result);

    }

}