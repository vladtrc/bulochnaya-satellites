package com.bul.satellites.mapper;

import com.bul.satellites.model.Given;

import java.time.Instant;
import java.util.function.Function;

public class InstantToString implements Function<Instant, String> {
    @Override
    public String apply(Instant instant) {
        return Given.format.format(instant);
    }
}
