package itman.com.web.cbl.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("cblUtil")
public class CblUtil {

    public Map<String, Object> getCblMap(List<Float> deltList) {
        Map<String, Object> cblMap = new HashMap<>();
        for (CblType cblType : CblType.values()) {
            double val = calcCbl(deltList, cblType);
            cblMap.put(cblType.name(), val);
        }
        return cblMap;
    }

    private double calcCbl(List<Float> deltVals, CblType cblType) {
        int selectDay = cblType.getSelectDay();

        if (cblType.name().equals("MID46")) {
            deltVals = deltVals.subList(0, 6);
        }

        Collections.sort(deltVals);

        if (deltVals.size() >= selectDay) {
            int sttIdx = (int) Math.ceil((deltVals.size() - selectDay) / 2.0);
            int endIdx = sttIdx + selectDay;

            deltVals = deltVals.subList(sttIdx, endIdx);

            return deltVals.stream()
                    .mapToDouble(Float::doubleValue)
                    .average().orElse(0);
        }

            return 0;
    }

}
