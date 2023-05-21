package com.bul.satellites.mapper;

import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.model.DurationEntry;
import com.bul.satellites.model.SatelliteBasePair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RawDataToDurationDatasets implements Function<Map<String, List<List<String>>>, List<DurationDataset>> {
    private final StringToInstant stringToInstant;


    DurationEntry parseToDurationEntry(List<String> row) {
        Instant start = stringToInstant.apply(row.get(1));
        Instant end = stringToInstant.apply(row.get(2));
        return DurationEntry.builder().start(start).end(end).build();
    }

    @Override
    public List<DurationDataset> apply(Map<String, List<List<String>>> data) {
        return data.entrySet().stream().map(nameToDataset -> {
                    String[] splitName = nameToDataset.getKey().split("-");
                    String base = splitName[0];
                    String satellite = splitName[2];
                    SatelliteBasePair satelliteBasePair = SatelliteBasePair.builder().base(base).satellite(satellite).build();
                    List<DurationEntry> entries = nameToDataset.getValue().stream().map(this::parseToDurationEntry).collect(Collectors.toList());
                    return DurationDataset.builder().satelliteBasePair(satelliteBasePair).entries(entries).build();
                }
        ).collect(Collectors.toList());
    }
}
