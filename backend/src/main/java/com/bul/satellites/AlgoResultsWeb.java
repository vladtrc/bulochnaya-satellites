package com.bul.satellites;

import com.bul.satellites.algo.DumbAlgo;
import com.bul.satellites.mapper.ResultToResultsWeb;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.ResultWeb;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Component
public class AlgoResultsWeb {
    Map<String, Future<ResultWeb>> results = new HashMap<>();
    ExecutorService executorService;
    Given given;

    public AlgoResultsWeb(GivenLoader givenLoader, ResultToResultsWeb resultToResultsWeb) {
        this.given = givenLoader.getGiven();
        this.executorService = Executors.newFixedThreadPool(3);

        Stream.of(
                new DumbAlgo()
        ).forEach(algo -> results.put(
                algo.name(),
                executorService.submit(() -> resultToResultsWeb.apply(algo.apply(given))))
        );
    }
}
