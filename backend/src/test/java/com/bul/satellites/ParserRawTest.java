package com.bul.satellites;

import com.bul.satellites.model.DurationDataset;
import com.bul.satellites.parsing.Parser;
import com.bul.satellites.parsing.ParserRaw;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ParserRawTest {

    private static InputStream resourceToInputStream(Resource e) {
        try {
            return e.getInputStream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    List<DurationDataset> parseFromRaw(InputStream in) {
        Map<String, List<List<String>>> rawData = new ParserRaw(in).parse();
        return new Parser(rawData).parse();
    }

    @Test
    void test() throws IOException {
        Resource[] facilityResources = new PathMatchingResourcePatternResolver().getResources("Facility2Constellation/*.txt");
        List<DurationDataset> facilityDatasets = Arrays.stream(facilityResources)
                .map(ParserRawTest::resourceToInputStream)
                .map(this::parseFromRaw)
                .flatMap(List::stream)
                .toList();


        Resource[] russiaResources = new PathMatchingResourcePatternResolver().getResources("Russia2Constellation/*.txt");
        List<DurationDataset> russiaDatasets = Arrays.stream(russiaResources)
                .map(ParserRawTest::resourceToInputStream)
                .map(this::parseFromRaw)
                .flatMap(List::stream)
                .toList();

        Map<String, List<DurationDataset>> availabilityByBase = facilityDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.base));
        Map<String, List<DurationDataset>> availabilityBySatellite = facilityDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.satellite));
        Map<String, List<DurationDataset>> availabilityRussia = russiaDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.satellite));
        System.out.println(russiaDatasets);
    }
}