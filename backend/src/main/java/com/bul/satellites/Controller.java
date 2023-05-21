package com.bul.satellites;

import com.bul.satellites.model.ResultWeb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class Controller {
    ResultsContainerWebService algoResultsWeb;

    @GetMapping("/results/{name}")
    public ResultWeb results(@PathVariable String name) throws ExecutionException, InterruptedException {
        Future<ResultWeb> result = algoResultsWeb.results.get(name);
        if (!result.isDone()) {
            return null;
        }
        return result.get();
    }
}
