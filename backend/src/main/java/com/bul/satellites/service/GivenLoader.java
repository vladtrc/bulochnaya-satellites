package com.bul.satellites.service;

import com.bul.satellites.mapper.ParserRaw;
import com.bul.satellites.mapper.RawDataToDurationDatasets;
import com.bul.satellites.mapper.StringToInstant;
import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.Interval;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GivenLoader {
    private final Given given;
    private final RawDataToDurationDatasets rawDataToDurationDatasets;
    private final StringToInstant stringToInstant;

    public GivenLoader(RawDataToDurationDatasets rawDataToDurationDatasets, StringToInstant stringToInstant) throws IOException {
        this.rawDataToDurationDatasets = rawDataToDurationDatasets;
        this.stringToInstant = stringToInstant;

        Resource[] facilityResources = new PathMatchingResourcePatternResolver().getResources("Facility2Constellation/*.txt");
        List<DurationDataset> facilityDatasets = Arrays.stream(facilityResources)
                .map(GivenLoader::resourceToInputStream)
                .map(this::parseFromRaw)
                .flatMap(List::stream)
                .toList();

        Resource[] russiaResources = new PathMatchingResourcePatternResolver().getResources("Russia2Constellation/*.txt");
        List<DurationDataset> russiaDatasets = Arrays.stream(russiaResources)
                .map(GivenLoader::resourceToInputStream)
                .map(this::parseFromRaw)
                .flatMap(List::stream)
                .toList();

        Map<String, List<DurationDataset>> availabilityByBase = facilityDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.base));
        Map<String, List<DurationDataset>> availabilityBySatellite = facilityDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.satellite));
        Map<String, List<DurationDataset>> availabilityRussia = russiaDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.satellite));

        Instant end = stringToInstant.apply("14 Jun 2027 00:00:00.000");
        Instant start = stringToInstant.apply("1 Jun 2027 00:00:00.000");
        Interval interval = Interval.builder().start(start).end(end).build();

        this.given = Given.builder()
                .availabilityByBase(availabilityByBase)
                .availabilityBySatellite(availabilityBySatellite)
                .availabilityRussia(availabilityRussia)
                .interval(interval)
                .build();
    }

    public Given getGiven() {
        return given;
    }

    private static InputStream resourceToInputStream(Resource e) {
        try {
            return e.getInputStream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<DurationDataset> parseFromRaw(InputStream in) {
        Map<String, List<List<String>>> rawData = new ParserRaw(in).parse();
        return rawDataToDurationDatasets.apply(rawData);
    }
}
