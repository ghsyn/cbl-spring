package itman.com.web.cbl.service;

import java.util.Map;

public interface CblService {
    Map<String, Object> getValMapAsDate(Map<String, Object> commandDate);
    Map<String, Object> getCblWithLimit(Map<String, Object> params);
    int inputHistData(Map<String, Object> params);
    int inputHistDataForJava(Map<String, Object> params);
    int inputHolidayData(Map<String, Object> params);
}
