package com.bul.satellites.mapper;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ParserRaw {
    InputStream file;

    public ParserRaw(InputStream file) {
        this.file = file;
    }

    public Map<String, List<List<String>>> parse() {
        Scanner scanner = new Scanner(file);
        String currDatasetName = null;
        Map<String, List<List<String>>> data = new HashMap<>(1000);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
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
