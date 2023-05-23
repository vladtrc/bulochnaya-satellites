package com.bul.satellites;

import com.bul.satellites.algo.AlexeyAlgo;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private final Given given;

    @Override
    public void run(String... args) throws Exception {
        Result result = new AlexeyAlgo().apply(given);
        System.out.println();
    }
}