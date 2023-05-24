package com.bul.satellites;

import com.bul.satellites.mapper.RawDataToDurationDatasets;
import com.bul.satellites.mapper.StringToInstant;
import com.bul.satellites.model.Given;
import com.bul.satellites.model.Result;
import com.bul.satellites.service.GivenLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

public class ValidateLimitsTest {
    @Test
    void test() throws IOException {
        StringToInstant stringToInstant = new StringToInstant();
        RawDataToDurationDatasets rawDataToDurationDatasets = new RawDataToDurationDatasets(stringToInstant);
        GivenLoader givenLoader = new GivenLoader(rawDataToDurationDatasets);
        Given given = givenLoader.getGiven();
        Result result = new Result();

        long count = result.datasets.stream().flatMap(d -> d.entries.stream().flatMap(e -> Stream.of(e.start, e.end)))
                .filter(i -> i.isBefore(Given.interval.start) && i.isAfter(Given.interval.end))
                .count();
        Assertions.assertEquals(0, count);
    }
}
