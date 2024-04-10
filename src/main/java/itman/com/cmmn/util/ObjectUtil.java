package itman.com.cmmn.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* @author: ybwhyb
* @since: 20240125
* */
@Slf4j
public class ObjectUtil {
    static final ObjectMapper mapper = new ObjectMapper();
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    /**
    * @comment: json형태의 문자열이나 Object를 map 타입으로 변환
    * @param: Object json 문자열
    * @return: Map
    * */
    public static Map<String, Object> json2Map(final Object json) throws IOException {
        JsonNode jsonNode = mapper.readTree(json instanceof String ? (String) json : mapper.writeValueAsString(json));
        return jsonToMapHelper(jsonNode);
    }

    /**
     * @comment: json Object 에서 Map 형태일 경우 map으로, List형태일 경우 List로 반환
     * @param: JSONObject
     * @return: Map
     * */
    private static Map<String, Object> toMap(final ObjectNode obj) {
        Map<String, Object> map = new HashMap<>();
        obj.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            if (value.isObject()) {
                map.put(key, toMap((ObjectNode) value));
            } else if (value.isArray()) {
                map.put(key, toList(value));
            } else {
                map.put(key, convertNodeValue(value));
            }
        });
        return map;
    }

    /**
     * @comment: json Array에서 map형태일 경우 map으로 리스트 형태일 경우 list로 반환
     * @param: JSONArray
     * @return: List
     * */
    private static List<Object> toList(final JsonNode array) {
        List<Object> list = new ArrayList<>();
        array.elements().forEachRemaining(element -> {
            if (element.isObject()) {
                list.add(toMap((ObjectNode) element));
            } else if (element.isArray()) {
                list.add(toList(element));
            } else {
                list.add(convertNodeValue(element));
            }
        });
        return list;
    }

    /**
     * @comment: jsonToMap함수에서 내부적으로 사용하는 함수
     * @param: JsonObject
     * @return: Map
     * */
    private static Map<String, Object> jsonToMapHelper(final JsonNode jsonNode) {
        return jsonNode.isObject() ? toMap((ObjectNode) jsonNode) : new HashMap<>();
    }

    /**
    * @comment: JsonNode 읙 값이 date format일 경우 dateformat 형식으로 변환하고 아닌경우 문자열로 반환
    * @param: JsonNode
    * @return: Object
    * */
    private static Object convertNodeValue(final JsonNode node) {
        if (node.isTextual() && isDateFormat(node.asText())) {
            try {
                return dateFormat.parse(node.asText());
            } catch (Exception e) {
               //log.info(":: ERR :: "+e.getMessage());
                return node.asText();
            }
        }
        return node.asText();
    }

    /**
    * @comment: json 문자열에 DateTime 형식의 문자열인지 판단
    * @param: String value
    * @return: boolean
    * */
    private static boolean isDateFormat(String value) {
        try {
            dateFormat.parse(value);
            return true;
        } catch (Exception e) {
            //log.error(":: ERR :: "+e.getMessage());
            return false;
        }
    }

    /**
     * @commnet: 데이터를 형식에 맞게 parsing
     * @param: Object 데이터
     * @return: Object
     * */
    public static Object dynamicCast(Object val){
        if(val instanceof Integer)
            return (Integer)val;
        if(val instanceof  Double)
            return (Double)val;
        if (val instanceof String)
            return (String) val;
        throw  new IllegalArgumentException("지원되지 않는 데이터 타입");
    }

}
