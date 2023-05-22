package com.bul.satellites.mapper;

import com.bul.satellites.model.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


public class ResultToOutput {

    public List<Output> toOutputList(Result result) {
        List<Output> outputList = result.datasets.stream().flatMap(e -> e.entries.stream().map(t -> {
                    return new Output(e.satelliteBasePair.base, "", t.start, t.end, "", e.satelliteBasePair.satellite, "");
                })).collect(Collectors.toList());

//
//        result.datasets.stream().map(c->c {new Output()
//        }).collect(Collectors.toList());

//    collect(Collectors.groupingBy(e -> e.satelliteBasePair.base,
//                    Collectors.groupingBy(e -> e.entries.stream().collect(groupingBy(t -> t.start)))));}


        return null;
    }


}
