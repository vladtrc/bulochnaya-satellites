package com.bul.satellites.mapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.function.Function;

public class StringToInstant implements Function<String, Instant> {
    @Override
    public Instant apply(String s) {
        // 13 Jun 2027 15:36:18.162
        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS", Locale.US);
        try {
            return format.parse(s).toInstant();
        } catch (ParseException e) {
            throw new RuntimeException("could not", e);
        }
    }
}
