package itman.com.web.cbl.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CblMapper {
    List<Map<String, Object>> selectCblAsDate(String commandDate);
    List<Map<String, Object>> selectCblWithLimit(Map<String, Object> params);
    int upsertHistList(List<Map<String,Object>> fileList);
    int upsertHolidayList(List<Map<String,Object>> fileList);
    int insertOne(Map<String, Object> map);
    int upsertCblRes(List<Map<String, Object>> dataList);
    List<Map<String, Object>> selectDeltValList(Map<String, Object> params);

    List<Map<String, Object>> selectWeekend();
    int insertWeekend(List<Map<String, Object>> weekendList);

    Map<String, Object> selectHist(String dateTime);
}
