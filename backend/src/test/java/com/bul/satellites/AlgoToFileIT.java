package com.bul.satellites;

import com.bul.satellites.algo.AlexeyAlgo;
import com.bul.satellites.model.Given;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ComponentScan("com.bul.satellites.model")
public class AlgoToFileIT {

    @Autowired
    private Given given;

    @Test
    void dumpToFileTest() {
        new AlexeyAlgo().apply(given);
    }
}
