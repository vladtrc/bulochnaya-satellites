package com.bul.satellites;

import com.bul.satellites.algo.DumbAlgo;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class RawDataToDurationDatasetsMapperRawTest {
    @Test
    void test() throws IOException {
        //GivenLoader loader = new GivenLoader();

        //System.out.println(loader.getGiven().getAvailabilityRussia());

        DumbAlgo da= new DumbAlgo();
        da.toOutput();
    }
}