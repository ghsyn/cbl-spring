package itman.com.cmmn.util.file.reader;

import itman.com.cmmn.util.ObjectUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * @author: ybwhyb
 * @since: 2024-01-26
 * */
@Slf4j
@Builder
public class CsvReader {
    final String fileName;
    @Builder.Default
    final String delimiter = "\t";
    @Builder.Default
    final Charset charset = StandardCharsets.UTF_8;
    @Builder.Default
    final Integer skipLine = 0;
    final Lock lock;

    /**
     * @comment: csv 파일을 읽고 데이터를 저장하는 함수
     * @param: void
     * @return : List<Map<String,Object>> [{},{}....] 형태의 데이터
     * */
    public List<Map<String, Object>> parse2ListMap() throws IOException {
        String line;
        List<Map<String, Object>> dataList = new ArrayList<>();
        try {

            lock.lock();
            // try resource 로 close 없이 자원해제를 위해서 사용
            try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName), charset)) {
                skipLines(br, skipLine); // 건너뛸 라인수
                String[] headers = br.readLine().split(delimiter); // 첫 번째 행을 키 값으로 사용
                int rowCount = skipLine;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(delimiter);
                    LinkedHashMap<String, Object> row = new LinkedHashMap<>(); // key value의 순서를 유지하기 위해서 Linked HashMap으로
                    for (int i = 0; i < headers.length; i++) {
                        if (!headers[i].isEmpty()) { // 빈 키 값 제외
                            row.put(headers[i], data.length > i ? data[i] : ""); // data 가 없는 경우 공백값으로
                        }
                    }
                    dataList.add(row);
                    rowCount++;
                }
                log.info("Read Count: {}", rowCount - skipLine);
            }
        } finally {
            lock.unlock();
        }
        return dataList;
    }


    /**
     * @comment: csv 파일을 읽고 {"key1":[], "key2":[]...} 형태의 데이터로 만들기 위한 함수
     * @param: void
     * @return: Map<String, List<Object>> {"key1":[], "key2":[]...} 형태의 데이터
     * */
    public Map<String,List<Object>> parse2Map(){
        Map<String, List<Object>> result = new LinkedHashMap<>();
        try{
            lock.lock();
            try(BufferedReader br = Files.newBufferedReader(Paths.get(fileName), charset)){
                skipLines(br, skipLine);
                String headerLine = br.readLine();
                String[] headers = headerLine.split(delimiter);
                // 각 컬럼별 데이터를 저장하기 위한 리스트 초기화
                for (String header : headers) {
                    if(!header.isEmpty())
                        result.put(header, new ArrayList<>());
                }
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(delimiter);
                    for (int i = 0; i < headers.length; i++) {
                        if(!headers[i].isEmpty())
                            result.get(headers[i]).add(ObjectUtil.dynamicCast(values[i]));
                    }
                }
            }
        }catch (Exception e){
            System.err.println("ERR: "+e.getMessage());
        }finally {
            lock.unlock();
        }
        return result;
    }


    private void skipLines(BufferedReader br, int lines) throws IOException {
        for (int i = 0; i < lines; i++) {
            br.readLine();
        }
    }
}
