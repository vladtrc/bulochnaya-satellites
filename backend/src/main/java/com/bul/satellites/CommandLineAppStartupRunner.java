package com.bul.satellites;

import com.bul.satellites.algo.AlexeyAlgo;
import com.bul.satellites.algo.VladAlgo;
import com.bul.satellites.mapper.ResultToResultsWeb;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.Result;
import com.bul.satellites.model.ResultWeb;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private final Given given;
    private final ResultToResultsWeb resultToResultsWeb;

    @Override
    public void run(String... args) throws Exception {
        Result result = new AlexeyAlgo().apply(given);
        ResultWeb resultWeb = resultToResultsWeb.apply(result);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File("car.json"), resultWeb); // слишком много данных там
        System.out.println();
    }
}