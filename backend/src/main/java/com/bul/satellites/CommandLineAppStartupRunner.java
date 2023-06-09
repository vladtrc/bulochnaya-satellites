//package com.bul.satellites;
//
//import com.bul.satellites.algo.AlexeyAlgo;
//import com.bul.satellites.algo.VladAlgo;
//import com.bul.satellites.mapper.ResultToResultsWeb;
//import com.bul.satellites.model.Given;
//import com.bul.satellites.model.Result;
//import com.bul.satellites.model.ResultWeb;
//import com.bul.satellites.validators.LimitValidator;
//import com.bul.satellites.validators.Validator;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.util.List;
//import java.util.stream.Stream;
//
//@Component
//@RequiredArgsConstructor
//public class CommandLineAppStartupRunner implements CommandLineRunner {
//
//    private final ResultToResultsWeb resultToResultsWeb;
//    private final LimitValidator limitValidator;
//
//
//    @Override
//    public void run(String... args) throws Exception {
//        Result result = new AlexeyAlgo().apply(given);
//        limitValidator.validate(result);
//        ResultWeb resultWeb = resultToResultsWeb.apply(result);
//        ObjectMapper objectMapper = new ObjectMapper();
//        JavaTimeModule module = new JavaTimeModule();
//        objectMapper.registerModule(module);
//        objectMapper.writeValue(new File("car.json"), resultWeb); // слишком много данных там
//        System.out.println();
//    }
//}