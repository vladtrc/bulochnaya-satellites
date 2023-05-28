//package com.bul.satellites.service;
//
//import com.bul.satellites.mapper.ParserRaw;
//import com.bul.satellites.mapper.RawDataToDurationDatasets;
//import com.bul.satellites.model.DurationDataset;
//import com.bul.satellites.model.Given;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.stereotype.Component;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Component
//public class GivenLoader2 {
//   // private final Given given;
//    private final RawDataToDurationDatasets rawDataToDurationDatasets;
//
//    public GivenLoader2(RawDataToDurationDatasets rawDataToDurationDatasets, String facility, String borders) throws IOException {
//        this.rawDataToDurationDatasets = rawDataToDurationDatasets;
//
//        List<DurationDataset> facilityDatasets = new ArrayList<>();
//        List<DurationDataset> russiaDatasets = new ArrayList<>();
//
//        try {
//            InputStream inputStream = new FileInputStream(facility);
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            //  Resource[] facilityResources2 = new PathMatchingResourcePatternResolver().getResources("Facility2Constellation/*.txt");
//            facilityDatasets = parseFromRaw(inputStreamReader);
//
//            InputStream inputStream2 = new FileInputStream(borders);
//            InputStreamReader inputStreamReader2 = new InputStreamReader(inputStream2);
//            //  Resource[] facilityResources2 = new PathMatchingResourcePatternResolver().getResources("Facility2Constellation/*.txt");
//            russiaDatasets = parseFromRaw(inputStreamReader2);
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
////        Resource[] russiaResources = new PathMatchingResourcePatternResolver().getResources("Russia2Constellation/*.txt");
////        List<DurationDataset> russiaDatasets = Arrays.stream(russiaResources)
////                .map(GivenLoader2::resourceToInputStreamReader)
////                .map(this::parseFromRaw)
////                .flatMap(List::stream)
////                .toList();
//
//        Map<String, List<DurationDataset>> availabilityByBase = facilityDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.base));
//        Map<String, List<DurationDataset>> availabilityBySatellite = facilityDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.satellite));
//        Map<String, List<DurationDataset>> availabilityRussia = russiaDatasets.stream().collect(Collectors.groupingBy(dataset -> dataset.satelliteBasePair.satellite));
//
////        this.given = Given.builder()
////                .availabilityByBase(availabilityByBase)
////                .availabilityBySatellite(availabilityBySatellite)
////                .availabilityRussia(availabilityRussia)
////                .build();
//    }
////
////    @Bean
////    public Given getGiven() {
////        return given;
////    }
//
//    private static InputStreamReader resourceToInputStreamReader(Resource e) {
//        try {
//            return new InputStreamReader(e.getInputStream());
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    private List<DurationDataset> parseFromRaw(InputStreamReader in) {
//        Map<String, List<List<String>>> rawData = new ParserRaw(in).parse();
//        return rawDataToDurationDatasets.apply(rawData);
//    }
//}
