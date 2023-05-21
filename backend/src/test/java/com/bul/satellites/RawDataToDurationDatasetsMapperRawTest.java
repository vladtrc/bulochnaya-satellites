package com.bul.satellites;

import com.bul.satellites.mapper.RawDataToDurationDatasets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;


@SpringBootTest
@ComponentScan("com.bul.satellites.mapper")
class RawDataToDurationDatasetsMapperRawTest {
    @Autowired
    GivenLoader loader;

    @Test
    void test() throws IOException {

        System.out.println(loader.getGiven().getAvailabilityRussia());
    }
}