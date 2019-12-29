/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import com.core.matrix.properties.ActivitiProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author thiag
 */
@Configuration
public class ActivitiDataBaseConfiguration implements EnvironmentAware {
    
     @Autowired
    private ActivitiProperties activitiProperties;

    private Environment environment;
    
    
    @org.springframework.beans.factory.annotation.Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void setEnvironment(Environment e) {
        this.environment = e;
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(
                new String[]{"com.core.matrix.workflow.model","com.core.matrix.workflow.repository"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean
    public DataSource dataSource() {

        DataSource dataSource;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setInitializationFailTimeout(-1);
        hikariConfig.setMinimumIdle(activitiProperties.getDatasource().getMinimumIdle());
        hikariConfig.setMaximumPoolSize((activitiProperties.getDatasource().getMaximumPoolSize()));
        hikariConfig.setValidationTimeout((activitiProperties.getDatasource().getValidationTimeout()));
        hikariConfig.setIdleTimeout((activitiProperties.getDatasource().getIdleTimeout()));
        hikariConfig.setConnectionTimeout((activitiProperties.getDatasource().getConnectionTimeout()));
        hikariConfig.setAutoCommit((activitiProperties.getDatasource().isAutoCommit()));
        hikariConfig.setJdbcUrl(activitiProperties.getDatasource().getSqlserver().getDataUrl());
        hikariConfig.setUsername(activitiProperties.getDatasource().getSqlserver().getDataSourceUser());
        hikariConfig.setPassword(activitiProperties.getDatasource().getSqlserver().getDataSourcePassword());
        hikariConfig.setPoolName("ActivitiPool");
        hikariConfig.setConnectionTestQuery(activitiProperties.getDatasource().getSqlserver().getConnectionTestQuery());

        dataSource = new HikariDataSource(hikariConfig);
        
        Logger.getLogger(ActivitiDataBaseConfiguration.class.getName()).log(Level.INFO, "Profile -> " + activeProfile );

        return dataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactory().getObject());
        return transactionManager;
    }
    
    
}
