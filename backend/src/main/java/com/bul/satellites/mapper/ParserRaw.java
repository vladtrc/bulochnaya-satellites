package com.bul.satellites.mapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class ParserRaw {
    InputStreamReader file;

    public ParserRaw(InputStreamReader file) {
        this.file = file;
    }

    public Map<String, List<List<String>>> parse() {
        BufferedReader scanner = new BufferedReader(file);
        String currDatasetName = null;

        Map<String, List<List<String>>> data = new HashMap<>(1000);
        for (String line: scanner.lines().toList()) {
            if (!line.contains(" ") && !line.contains("---") && !line.isEmpty()) {
                currDatasetName = line;
                data.put(currDatasetName, new ArrayList<>());
                continue;
            }
            String[] values = line.split("  +");
            if (values.length == 5 && values[1].chars().anyMatch(Character::isDigit) && values[0].isEmpty()) {
                List<List<String>> lists = data.get(currDatasetName);
                lists.add(Arrays.stream(values).skip(1).toList());
                data.put(currDatasetName, lists);
            }
        }
        return data;
    }
}
