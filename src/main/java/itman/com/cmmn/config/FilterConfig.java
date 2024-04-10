package itman.com.cmmn.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FilterConfig {

/*
    @Bean
    public FilterRegistrationBean<Filter> htmlTagFilter(){
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new HTMLTagFilter());
        bean.setOrder(1);
        bean.addUrlPatterns("/*");
        log.info("html filter");
        return bean;
    }
*/

}
