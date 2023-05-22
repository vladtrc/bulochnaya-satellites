package com.bul.satellites;

import com.bul.satellites.mapper.StringToInstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class InstantParsingTests {

    @Test
    void testTimeMapper() {
        StringToInstant stringToInstant = new StringToInstant();
        Instant instant = stringToInstant.apply("1 Jun 2027 09:19:35.605");
        Assertions.assertEquals("asd", instant);
    }
}
