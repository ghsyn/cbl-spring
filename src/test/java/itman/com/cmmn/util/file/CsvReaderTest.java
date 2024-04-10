package itman.com.cmmn.util.file;

import itman.com.cmmn.util.file.reader.CsvReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class CsvReaderTest {

    final String path ="C:\\data.csv";
    final String pdPath = "C:\\Users\\itman\\Desktop\\PD 시험데이터\\고리2호기 PD\\U PD\\2022_03_09_15_09_15\\2022_03_09_15_09_15.CSV";
    @Test
    public void readerTest() throws Exception {

        List<Map<String,Object>> result = CsvReader.builder()
                .delimiter(",")
                .fileName(path)
                .lock(new ReentrantLock())
                .build()
                .parse2ListMap();

        System.out.println(result);

    }

    @Test
    public void readerCSV() throws IOException {
        List<Map<String,Object>> result = CsvReader.builder()
                .charset(StandardCharsets.UTF_16LE)
                .delimiter("\t")
                .fileName(pdPath)
                .skipLine(14)
                .lock(new ReentrantLock())
                .build()
                .parse2ListMap();
        System.out.println(result);
    }
}
