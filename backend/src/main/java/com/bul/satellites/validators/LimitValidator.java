package com.bul.satellites.validators;

import com.bul.satellites.model.Given;
import com.bul.satellites.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LimitValidator implements Validator {
    private final Given given;
    @Override
    public boolean validate(Result result) {
        long count = result.datasets.stream().flatMap(d -> d.entries.stream().flatMap(e -> Stream.of(e.start, e.end)))
                .filter(i -> i.isBefore(Given.limits.start) && i.isAfter(Given.limits.end))
                .count();
        return count > 0;
    }
}
