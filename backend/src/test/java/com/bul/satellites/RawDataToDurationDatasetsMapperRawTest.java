package com.bul.satellites;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class RawDataToDurationDatasetsMapperRawTest {
    @Test
    void test() throws IOException {
        GivenLoader loader = new GivenLoader();

        System.out.println(loader.getGiven().getAvailabilityRussia());
    }
}