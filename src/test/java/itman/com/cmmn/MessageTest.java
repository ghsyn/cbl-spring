package itman.com.cmmn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
public class MessageTest  {
    @Autowired
    private MessageSource messageSource;

    @Test
    public void testMessageSource() {
        // 기본 언어(영어) 메시지 가져오기
        String defaultMessage = messageSource.getMessage("button.create", null, Locale.getDefault());
        System.out.println("Default Message: " + defaultMessage);

        // 한국어 메시지 가져오기
        String koreanMessage = messageSource.getMessage("button.create", null, Locale.KOREAN);
        System.out.println("Korean Message: " + koreanMessage);

        // 영어 메시지 가져오기
        String englishMessage = messageSource.getMessage("button.create", null, Locale.ENGLISH);
        System.out.println("English Message: " + englishMessage);

        // 메시지가 올바르게 로드되었는지 확인
        assert defaultMessage.equals("등록");
        assert koreanMessage.equals("등록");
        assert englishMessage.equals("create");
    }

    @Test
    public void test(){
        HashMap<Object, Object> json = new HashMap<>();

        HashMap<Object, Object> mini1 = new HashMap<>();
        mini1.put("power", 2031);
        mini1.put("beautiful", 10);

        HashMap<Object, Object> mini2 = new HashMap<>();
        mini2.put("power", 1000);
        mini2.put("beautiful", 200);

        HashMap<Object, Object> child = new HashMap<>();
        child.put("power", 10);
        child.put("beautiful", 20);

        json.put("man", mini1);
        json.put("woman", mini2);
        json.put("child", child);

        Comparator<HashMap<Object, Object>> inner = Comparator.comparing( (HashMap<Object,Object> arg)-> {
            return Integer.parseInt(arg.get("power").toString());
        });
        Comparator compare = Map.Entry.comparingByValue(inner);
        json.entrySet().stream().sorted(compare).collect(Collectors.toList());  //정렬!

        System.out.println(json);
    }



}
