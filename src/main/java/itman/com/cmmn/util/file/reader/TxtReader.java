package itman.com.cmmn.util.file.reader;

import lombok.Builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;


@Builder
public class TxtReader {
    final String fileName;
    final Lock lock;

    public Map<String, List<?>> parseTxtFile() throws IOException{
        if(fileName == null){
            throw new IOException("읽어들일 파일이 존재하지 않습니다." + fileName);
        }
        List<String> lines = new ArrayList<>();
        Map<String, List<?>> ret= new HashMap<>();
        try{
            lock.lock();
            try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(Paths.get(fileName)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                ret.put("data", lines);
            }
        } catch (Exception e) {
            System.err.println("::ERR:: "+e.getCause());
        }finally {
            lock.unlock();
        }

        return ret;
    }
}
