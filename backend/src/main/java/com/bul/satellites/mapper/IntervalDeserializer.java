package com.bul.satellites.mapper;

import com.bul.satellites.model.Interval;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.time.Instant;

public class IntervalDeserializer extends KeyDeserializer {
    @Override
    public Interval deserializeKey(String s, DeserializationContext deserializationContext) {
        String[] split = s.split(",");
        return new Interval(Instant.parse(split[0]), Instant.parse(split[1]));
    }
}
