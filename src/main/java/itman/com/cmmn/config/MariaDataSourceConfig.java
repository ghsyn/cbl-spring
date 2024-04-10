package itman.com.cmmn.config;

import com.zaxxer.hikari.HikariDataSource;
import itman.com.cmmn.util.RefreshableSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableTransactionManagement
public class MariaDataSourceConfig {

    @Bean
    @ConfigurationProperties("ds-maria")
    public DataSourceProperties mariaProperties(){
        return new DataSourceProperties();
    }

    @Bean(name = "mariaDataSource")
    @Primary
    public DataSource mariaDataSource(){
        return mariaProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
        //return new JndiDataSourceLookup().getDataSource(mariaProperties().getJndiName());
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) throws IOException {
        PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();
        //SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        RefreshableSqlSessionFactoryBean bean = new RefreshableSqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setConfigLocation(pmrpr.getResource("classpath:/mybatis-config.xml"));
        bean.setMapperLocations(pmrpr.getResources("classpath:/mappers/**/**Mapper.xml"));
        return bean;
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name="mariaTx")
    PlatformTransactionManager mariaTransactionManager(){
        DataSourceTransactionManager firstTransactionManager = new DataSourceTransactionManager();
        firstTransactionManager.setDataSource(mariaDataSource());
        return firstTransactionManager;
    }

//    @Bean(name="txManager")
//    public DataSourceTransactionManager txManager( DataSource dataSource) {
//        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
//        dataSourceTransactionManager.setDataSource(dataSource);
//        return dataSourceTransactionManager;
//    }
//
//    @Bean(name="txAdvice")
//    public TransactionInterceptor txAdvice(DataSourceTransactionManager txManager) {
//        RuleBasedTransactionAttribute txAttribute = new RuleBasedTransactionAttribute();
//        txAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        txAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
//
//        HashMap<String, TransactionAttribute> txMethods = new HashMap<String, TransactionAttribute>();
//        txMethods.put("*", txAttribute);
//
//        NameMatchTransactionAttributeSource txAttributeSource = new NameMatchTransactionAttributeSource();
//        txAttributeSource.setNameMap(txMethods);
//
//        TransactionInterceptor txAdvice = new TransactionInterceptor();
//        txAdvice.setTransactionAttributeSource(txAttributeSource);
//        txAdvice.setTransactionManager(txManager);
//
//        return txAdvice;
//    }
//
//    @Bean(name="txAdvisor")
//    public Advisor txAdvisor(@Qualifier("txManager") DataSourceTransactionManager txManager) {
//        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
//        pointcut.setExpression("execution (* itman.com.web.service.*Impl.*(..))" );
//        return new DefaultPointcutAdvisor(pointcut, txAdvice(txManager));
//    }
}
