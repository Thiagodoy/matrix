/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import com.core.matrix.properties.MatrixProperties;
import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
import com.core.matrix.utils.ThreadPoolBindFile;
import com.core.matrix.utils.ThreadPoolDetail;
import com.core.matrix.utils.ThreadPoolParseFile;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.persistence.TableGenerator;
import javax.sql.DataSource;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author thiag
 */

@EnableJpaRepositories(basePackages = {"com.core.matrix.repository"},
         entityManagerFactoryRef = "matrixEntityManagerFactory", 
        transactionManagerRef = "matrixTransactionManager") 

@Configuration
@TableGenerator(name = TABLE_SEQUENCES)
public class MatrixDataBaseConfiguration {
    
    
    @Autowired
    private MatrixProperties matrixProperties;
    
    private static final String NAME_POOL = "MatrixPool";    
    
    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean matrixEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(matrixDataSource());
        em.setPackagesToScan(
                new String[]{"com.core.matrix.model", "com.core.matrix.repository"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        //properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");           
        //properties.put("hibernate.jdbc.batch_size", "3000");   
        //properties.put("hibernate.order_inserts", "true");   
        //properties.put("hibernate.generate_statistics", "true");   
        em.setJpaPropertyMap(properties);
        
        
        
        return em;
    }

    @Primary
    @Bean
    public DataSource matrixDataSource() {

        DataSource dataSource;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setInitializationFailTimeout(-1);
        hikariConfig.setMinimumIdle(matrixProperties.getDatasource().getMinimumIdle());
        hikariConfig.setMaximumPoolSize((matrixProperties.getDatasource().getMaximumPoolSize()));
        hikariConfig.setValidationTimeout((matrixProperties.getDatasource().getValidationTimeout()));
        hikariConfig.setIdleTimeout((matrixProperties.getDatasource().getIdleTimeout()));
        hikariConfig.setConnectionTimeout((matrixProperties.getDatasource().getConnectionTimeout()));
        hikariConfig.setAutoCommit((matrixProperties.getDatasource().isAutoCommit()));
        hikariConfig.setJdbcUrl(matrixProperties.getDatasource().getSqlserver().getDataUrl());
        hikariConfig.setUsername(matrixProperties.getDatasource().getSqlserver().getDataSourceUser());
        hikariConfig.setPassword(matrixProperties.getDatasource().getSqlserver().getDataSourcePassword());
        hikariConfig.setPoolName(NAME_POOL);
        
        hikariConfig.setConnectionTestQuery(matrixProperties.getDatasource().getSqlserver().getConnectionTestQuery());

        dataSource = new HikariDataSource(hikariConfig);        
        
        return dataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager matrixTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                matrixEntityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ThreadPoolParseFile threadPoolParseFile(){
        return  new ThreadPoolParseFile();
    }
    
    @Bean 
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ThreadPoolBindFile threadPoolBindFile(TaskService service){
        return new ThreadPoolBindFile(service);
    }
    
    @Bean 
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ThreadPoolDetail detail(){
        return new ThreadPoolDetail();
    }
    
}
