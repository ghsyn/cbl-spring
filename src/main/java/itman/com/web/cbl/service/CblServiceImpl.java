package itman.com.web.cbl.service;

import itman.com.web.cbl.util.CblUtil;
import itman.com.web.cbl.util.FileReader;
import itman.com.web.cbl.mapper.CblMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;


@Slf4j
@Service("cblService")
@Transactional
public class CblServiceImpl implements CblService {

    @Resource(name = "cblMapper")
    CblMapper mapper;

    FileReader fileReader = new FileReader();
    @Resource(name = "cblUtil")
    CblUtil cblUtil;

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

    /**
     * 계산한 cbl values를 INSERT 및 UPDATE 하기 위해 List<Map<String, Object>> 형태로 만듦
     * @param param commandDate를 포함
     * @param selectData
     * @return upsertParam
     */
    private List<Map<String, Object>> getUpsertParam(Map<String, Object> param, List<Map<String, Object>> selectData) {
        List<Map<String, Object>> upsertParam = new ArrayList<>();
        List<Float> deltList = new ArrayList<>();

        int temp = 0;
        for (Map<String, Object> m : selectData) {

            if ((int) m.get("TM") == temp) {
                deltList.add((Float) m.get("DELT_VAL"));
            } else {
                Map<String, Object> cblMap = cblUtil.getCblMap(deltList);
                cblMap.put("DATE", param.get("commandDate"));
                cblMap.put("TM", m.get("TM"));

                upsertParam.add(cblMap);

                deltList.clear();
                deltList.add((Float) m.get("DELT_VAL"));

                temp++;
            }
        }

        return upsertParam;
    }

    /**
     * 받은 날짜에 대한 모든 시간 selectDeltVal, cbl계산, upsertCblRes 수행
     *
     * @param param commandDate
     * @return (map) {chart=[CBL_TIME="", MID610= , MID46= , MID=810], [], [], ...}, {grid=[CBL_TIME="", MID610= , MID46= , MID=810], [], [] ...}
     */
    @Override
    public Map<String, Object> getValMapAsDate(Map<String, Object> param) {
        List<Map<String, Object>> selectData = mapper.selectDeltValList(param);
        if (selectData.isEmpty()) {
            log.info("Select Data failed: {}", selectData);
            return Collections.emptyMap();
        }

        List<Map<String, Object>> upsertParam = getUpsertParam(param, selectData);

        int res = mapper.upsertCblRes(upsertParam);
        if (res != upsertParam.size()) {
            log.info("Insert Cbl FAIL : {}", res);
        }

        List<Map<String, Object>> ret = mapper.selectCblAsDate(param.get("commandDate") + "");
        return listToMap(ret);
    }

    @Override
    public Map<String, Object> getCblWithLimit(Map<String, Object> param) {
        List<Map<String, Object>> selectData = mapper.selectDeltValList(param);
        if (selectData.isEmpty()) {
            log.info("Select Data failed: {}", selectData);
            return Collections.emptyMap();
        }

        List<Map<String, Object>> upsertParam = getUpsertParam(param, selectData);

        int res = mapper.upsertCblRes(upsertParam);
        if (res != upsertParam.size()) {
            log.info("Insert Cbl FAIL : {}", res);
        }

        List<Map<String, Object>> ret = mapper.selectCblWithLimit(param);

        return listToMap(ret);
    }

    @Override
    public int inputHistData(Map<String, Object> params) {
        List<Map<String, Object>> fileList = fileReader.readFile(params.get("filePath") + "", params.get("charset") + "");
        return mapper.upsertHistList(fileList);
    }

    @Override
    public int inputHistDataForJava(Map<String, Object> params) {
        List<Map<String, Object>> fileList = fileReader.readFile(params.get("filePath") + "", params.get("charset") + "");

        int cnt = 0;
        for (Map<String, Object> map : fileList) {
            int res = mapper.insertOne(map);
            if (res != 1) {
                log.info("Insert One Failed: {}", map);
                return 0;
            }
            cnt += res;
        }
        return cnt;
    }

    @Override
    public int inputHolidayData(Map<String, Object> params) {
        List<Map<String, Object>> fileList = fileReader.readFile(params.get("filePath") + "", params.get("charset") + "");
        return mapper.upsertHolidayList(fileList);
    }
}
