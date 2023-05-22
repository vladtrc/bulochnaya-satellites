package com.bul.satellites;

import com.bul.satellites.service.GivenLoader;
import com.bul.satellites.algo.DumbAlgo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;


@SpringBootTest
@ComponentScan("com.bul.satellites.mapper")
class RawDataToDurationDatasetsMapperRawTest {
    @Autowired
    GivenLoader loader;

    @Test
    void test() throws IOException {
        GivenLoader loader = new GivenLoader();

        //System.out.println(loader.getGiven().getAvailabilityRussia());

        DumbAlgo da= new DumbAlgo();
        da.toOutput();
    }
}