package com.bul.satellites.mapper;

import com.bul.satellites.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ResultToResultsWeb implements Function<Result, ResultWeb> {
    //final private Given given;

    List<BaseUsageResult> toBaseUsageResult(Map.Entry<String, List<DurationDataset>> pair) {
        String satellite = pair.getKey();
        List<DurationDataset> datasets = pair.getValue();
        return datasets.stream()
                .map(d -> d.entries)
                .flatMap(List::stream)
                .map(d -> BaseUsageResult.builder().satelliteName(satellite).start(d.start).end(d.end).build())
                .collect(Collectors.toList());
    }

    BaseResultWeb toBaseResultWeb(Map.Entry<String, List<DurationDataset>> pair) {
        String base = pair.getKey();
        Map<String, List<DurationDataset>> datasetsBySatellite = pair.getValue().stream().collect(Collectors.groupingBy(d -> d.satelliteBasePair.satellite));
        List<BaseUsageResult> usage = datasetsBySatellite.entrySet().stream().map(this::toBaseUsageResult).flatMap(List::stream).collect(Collectors.toList());
        return BaseResultWeb.builder().base(base).usage(usage).build();
    }

    @Override
    public ResultWeb apply(Result result) {
        List<BaseResultWeb> results = result.datasets.stream()
                .collect(Collectors.groupingBy(e -> e.satelliteBasePair.base))
                .entrySet()
                .stream()
                .map(this::toBaseResultWeb)
                .collect(Collectors.toList());
        return ResultWeb.builder().results(results).build();
    }
}
