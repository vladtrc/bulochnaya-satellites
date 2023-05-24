package com.bul.satellites;

import com.bul.satellites.model.Given;
import com.bul.satellites.model.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidateLimitsTest {
    @Test
    void test() {
        Given given = new Given();
        Result result = new Result();

        long count = result.datasets.stream().flatMap(d -> d.entries.stream().flatMap(e -> Stream.of(e.start, e.end)))
                .filter(i -> i.isBefore(Given.interval.start) && i.isAfter(Given.interval.end))
                .count();
        Assertions.assertEquals(0, count);
    }
}
