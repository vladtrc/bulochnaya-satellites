package com.bul.satellites.controller;

import com.bul.satellites.service.ResultsContainerWebService;
import com.bul.satellites.model.ResultWeb;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final ResultsContainerWebService algoResultsWeb;

    @GetMapping("/results/{name}")
    public ResultWeb results(@PathVariable String name) throws ExecutionException, InterruptedException {
        Future<ResultWeb> result = algoResultsWeb.getResults().get(name);
        if (!result.isDone()) {
            return null;
        }
        return result.get();
    }
}
