package com.bul.satellites.parsing;

import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.model.DurationEntry;
import com.bul.satellites.model.SatelliteBasePair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {
    Map<String, List<List<String>>> data;

    public Parser(Map<String, List<List<String>>> data) {
        this.data = data;
    }

    DurationEntry parseToDurationEntry(List<String> row) {
        return DurationEntry.builder().start(row.get(1)).end(row.get(2)).build();
    }


    public List<DurationDataset> parse() {
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

    private List<String> parseDatasetLine(String line) {
        return Arrays.stream(line.split("  +")).filter(e -> !e.isBlank()).collect(Collectors.toList());
    }
}
