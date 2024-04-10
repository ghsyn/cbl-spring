package itman.com.web.cbl.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class FileReader {
    public List<Map<String, Object>> readFile(String filePath, String charsetName) {
        Path path = Paths.get(filePath);
        Charset charset = Charset.forName(charsetName);

        if (!Files.exists(path)) {
            log.info("File does not exist: {}", filePath);
            System.exit(0);
        }

        List<Map<String, Object>> dataList = new ArrayList<>();

        try (BufferedReader fileReader = Files.newBufferedReader(path, charset)) {
            String[] header = fileReader.readLine().trim().split(",");

            String line;

            while ((line = fileReader.readLine()) != null) {
                String[] data = line.trim().split(",");
                if (data.length < 2) {
                    log.info("line skip: {}", line);
                    continue;
                }

                Map<String, Object> map = new HashMap<>();
                map.put(header[0], data[0]);
                map.put(header[1], data[1]);

                dataList.add(map);
            }

        } catch (IOException e) {
            log.error("Read file Error: {}, {}", filePath, e.getMessage());
        }

        if (dataList.isEmpty()) {
            log.info("File read fail: {}", filePath);
            System.exit(0);
        }

        return dataList;
    }
}
