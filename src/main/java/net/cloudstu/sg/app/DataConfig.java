package net.cloudstu.sg.app;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * 数据源相关配置
 *
 * @author lizhiming
 */
@Configuration
@MapperScan(value = "net.cloudstu.sg.dao",annotationClass = Repository.class)
public class DataConfig {
    @Bean
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setUrl("jdbc:mysql://localhost:3306/prediction?useUnicode=true&amp;characterEncoding=UTF-8");

        return dataSource;
    }

//    @Bean
//    public DataSource dataSource() {
//        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        dataSource.setUsername("pdf");
//        dataSource.setPassword("pdfpassword");
//        dataSource.setUrl("jdbc:mysql://172.16.3.251:3306/pdf?useUnicode=true&amp;characterEncoding=UTF-8");
//
//        return dataSource;
//    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        sessionFactory.setTypeAliasesPackage("net.cloudstu.sg.entity");
        return sessionFactory;
    }
}
