package com.bul.satellites.validators;

import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
@Component
@RequiredArgsConstructor
public class LimitValidator implements Validator {
    //private final Given given;
    @Override
    public boolean validate(Result result) {
        long count = result.datasets.stream().flatMap(d -> d.entries.stream().flatMap(e -> Stream.of(e.start, e.end)))
                .filter(i -> i.isBefore(Given.limits.start) && i.isAfter(Given.limits.end))
                .count();
        return count > 0;
    }
*/


//    public boolean checkRussia(Result result) {
//        Map<String, List<DurationDataset>> availabilityRussia=given.getAvailabilityRussia();
//        long count = result.datasets.stream().collect(Collectors.groupingBy(e -> e.satelliteBasePair.satellite)).
//                forEach((k,v)->
//                        v.forEach(p -> p.entries.stream().flatMap(e -> Stream.of(e.start, e.end)).
//                            filter(i -> i.isBefore(Given.interval.start) && i.isAfter(Given.interval.end)).count();
//
//
//        return count > 0;
//    }
//}
