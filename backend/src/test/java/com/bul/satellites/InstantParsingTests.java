package com.bul.satellites;

import com.bul.satellites.mapper.StringToInstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class InstantParsingTests {

    @Test
    void testTimeMapper() {
        StringToInstant stringToInstant = new StringToInstant();
        Instant instant = stringToInstant.apply("14 Jun 2027 00:00:00.000");
    }
}
