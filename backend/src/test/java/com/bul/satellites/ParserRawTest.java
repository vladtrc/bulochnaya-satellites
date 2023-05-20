package com.bul.satellites;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

class ParserRawTest {

    private static InputStream resourceToInputStream(Resource e) {
        try {
            return e.getInputStream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    void test() throws IOException {
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("Facility2Constellation/*.txt");
        List<List<DurationDataset>> res = Arrays.stream(resources)
                .map(ParserRawTest::resourceToInputStream)
                .map(e -> new ParserRaw(e).parse())
                .map(Parser::new)
                .map(Parser::parse)
                .toList();
        System.out.println(res);
    }
}