package itman.com.cmmn.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import java.util.Locale;

@Configuration
@Slf4j
public class MessageConfig {

    @Bean
    public LocaleResolver defaultLocaleResolver(){
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.KOREAN);
        return localeResolver;
    }


    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/message/message-common", "classpath:/message/message-common_ko", "classpath:/message/messages-common_en");
        messageSource.setDefaultLocale(Locale.getDefault());// 기본 언어 설정 (한국어 설정)
        messageSource.setDefaultEncoding("UTF-8"); // 인코딩 설정
        messageSource.setCacheSeconds(0); // 캐시 사용 여부 설정 (true로 설정하면 변경된 메시지 파일이 자동으로 적용되지 않음)
        return messageSource;
    }
}
