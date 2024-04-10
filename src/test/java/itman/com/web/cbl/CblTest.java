package itman.com.web.cbl;

import itman.com.cmmn.util.file.reader.CsvReader;
import itman.com.web.cbl.mapper.CblMapper;
import itman.com.web.cbl.util.CblType;
import itman.com.web.cbl.util.CblUtil;
import itman.com.web.cbl.util.FileReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CblTest {

    //@Resource(name="cblMapper")
    @Autowired
    CblMapper mapper;

    @Autowired
    CblUtil cblUtil;

    @Test
    public void SelectTest(){
        String commandDate = "2020-03-22 00:00:00";

        List<Map<String,Object>> result = mapper.selectCblAsDate(commandDate);
        System.out.println("::>> Result: "+result);
    }

    @Test
    public void fileUpsertTest() {
        String filePath = "src/main/resources/static/data/ems_ent_EM_ANAL_HIST_60MIN.csv";
//        String filePath = "src/main/resources/static/data/holidays.csv";

        Set<String> keySet = new HashSet<>();

        FileReader fileReader = new FileReader();
        List<Map<String, Object>> fileList = fileReader.readFile(filePath, "UTF-8");

        int res = 0;
        if (filePath.contains("ems_ent_EM_ANAL_HIST_60MIN.csv")) {
            res = mapper.upsertHistList(fileList);

            keySet.add("HIST_TIME");
            keySet.add("DELT_VAL");

            assertThat(res).isEqualTo(32471);
            assertThat(fileList.get(1).keySet()).isEqualTo(keySet);

        } else if (filePath.contains("holidays.csv")) {
            res = mapper.upsertHolidayList(fileList);

            keySet.add("HOLIDAY_DATE");
            keySet.add("HOLIDAY_DETAIL");

            assertThat(res).isEqualTo(89);
            assertThat(fileList.get(1).keySet()).isEqualTo(keySet);
        }

        assertThat(res).isPositive();
    }

    @Test
    public void insertOneTest() throws ParseException {
        String filePath = "src/main/resources/static/data/ems_ent_EM_ANAL_HIST_60MIN.csv";

        FileReader fileReader = new FileReader();
        List<Map<String, Object>> fileList = fileReader.readFile(filePath, "UTF-8");

        int res = 0;
        for (Map<String, Object> map : fileList) {
            res += mapper.insertOne(map);
        }
        assertThat(res).isEqualTo(32471);

        Map<String, Object> expected = new HashMap<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp histTime = new Timestamp(format.parse("2022-01-01 01:00:00").getTime());
        expected.put("HIST_TIME", histTime);
        expected.put("DELT_VAL", 22.416f);

        Map<String, Object> actual = mapper.selectHist("2022-01-01 01:00:00");

        assertThat(actual).isEqualTo(expected);
    }

//    @Test
//    public void insertTest() throws Exception{
//        List<Map<String,Object>> result = CsvReader.builder()
//                .delimiter(",")
//                .fileName("src/main/resources/data/ems_ent_EM_ANAL_HIST_60MIN.csv")
//                .lock(new ReentrantLock())
//                .build()
//                .parse2ListMap();
//
//        System.out.println(result);
//        int res = mapper.insertAllList(result);
//        System.out.println("::>> Result: "+((res>0)? "SUCC": "FAIL"));
//    }

    @Test
    public void upsertTest2() throws Exception{
        List<Map<String,Object>> result = CsvReader.builder()
                .delimiter(",")
                .fileName("src/main/resources/static/data/ems_ent_EM_ANAL_HIST_60MIN.csv")
                .charset(StandardCharsets.UTF_8)
                .lock(new ReentrantLock())
                .build()
                .parse2ListMap();
        int res = mapper.upsertHistList(result);

        System.out.println("::>> RESULT: " + res);
    }

    @Test
    public void insertWeekendToHolidays() {
        List<Map<String, Object>> weekendList = mapper.selectWeekend();

        int expected = weekendList.size();
        int actual = mapper.insertWeekend(weekendList);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void test() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime commandDateTime = LocalDateTime.parse("2023-12-12" + " 00:00:00", formatter);

        // CBL 계산
        List<Map<String, Object>> cblValues = new ArrayList<>();

        for (CblType cblType : CblType.values()) {
            Map<String, Object> params = new HashMap<>();
            params.put("baseDay", cblType.getBaseDay());
            params.put("doubleBaseDay", cblType.getBaseDay() * 2);
            params.put("commandDate", "2023-12-12");
        }

        while (!(commandDateTime.isEqual(LocalDateTime.parse("2023-12-12" + " 24:00:00", formatter)))) {
            Map<String, Object> map = new HashMap<>();
            map.put("CBL_TIME", commandDateTime.format(formatter));

            for (CblType cblType : CblType.values()) {
                Map<String, Object> params = new HashMap<>();
                params.put("baseDay", cblType.getBaseDay());
                params.put("doubleBaseDay", cblType.getBaseDay() * 2);
                params.put("commandDate", "2023-12-12");

                List<Map<String, Object>> deltList = mapper.selectDeltValList(params);
//                map.put(cblType.name(), String.format("%.3f", calcCbl(deltList, cblType.getSelectDay())));
            }

            cblValues.add(map);

            commandDateTime = commandDateTime.plusHours(1);
        }
    }

    @Test
    public void getValMapAsDateTest() {
        Map<String, Object> param = new HashMap<>();
        param.put("commandDate", "2023-09-08");

        List<Map<String, Object>> selectData = mapper.selectDeltValList(param);

        List<Map<String, Object>> cblData = new ArrayList<>();

        List<Float> deltList = new ArrayList<>();
        int curTm = 0;
        for (Map<String, Object> m : selectData) {
            int tm = (int) m.get("TM");
            if (curTm == tm) {
                deltList.add((Float) m.get("DELT_VAL"));
            } else {
                Map<String, Object> cblReusltMap = cblUtil.getCblMap(deltList);
                cblReusltMap.put("DATE", param.get("commandDate"));
                cblReusltMap.put("TM", curTm);

                cblData.add(cblReusltMap);

                deltList.clear();
                curTm = tm;
            }
        }

        int res = mapper.upsertCblRes(cblData);
        System.out.println("cblData >> " + cblData);

        List<Map<String,Object>> listResultMap = mapper.selectCblAsDate(String.valueOf(param.get("commandDate")));
    }

    private List<Object> getlist(List<Map<String, Object>> selectData, String keyName) {
        List<Object> list = new ArrayList<>();
        for (Map<String, Object> data : selectData) {
            list.add(data.get(keyName));
        }
        return list;
    }


    private Map<String, Object> listToMap(List<Map<String, Object>> data) {
        Map<String, Object> map = new HashMap<>();
        map.put("grid", data);

        // keyList = ["CBL_TIME", "MID610", ... ]
        List<String> keyList = new ArrayList<>(data.get(1).keySet().stream().toList());
        Map<String, List<Object>> res = new HashMap<>();
        for (String key : keyList) {
            List<Object> valueList = getlist(data, key);
            res.put(key, valueList);
        }
        map.put("chart", res);
        return map;
    }
}