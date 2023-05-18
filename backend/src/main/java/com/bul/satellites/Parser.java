
package com.bul.satellites;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    InputStream file;

    public Parser(InputStream file) {
        this.file = file;
    }

    Map<String, List<List<String>>> parse() {
        Scanner scanner = new Scanner(file);
        String currDatasetName = null;
        Map<String, List<List<String>>> data = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.contains(" ") && !line.contains("---") && !line.isEmpty()) {
                currDatasetName = line;
                data.put(currDatasetName, new ArrayList<>());
                continue;
            }
            String[] values = line.split("  +");
            if (values.length == 5 && values[1].chars().allMatch(Character::isDigit) && values[0].isEmpty()) {
                List<List<String>> lists = data.get(currDatasetName);
                lists.add(Arrays.stream(values).skip(1).toList());
                data.put(currDatasetName, lists);
            }
        }
        return data;
    }

    private List<String> parseDatasetLine(String line) {
        return Arrays.stream(line.split("  +")).filter(e -> !e.isBlank()).collect(Collectors.toList());
    }
}
