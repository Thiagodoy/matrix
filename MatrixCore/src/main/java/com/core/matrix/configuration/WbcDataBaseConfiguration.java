/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import com.core.matrix.properties.WbcProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author thiag
 */

@EnableJpaRepositories(basePackages = {"com.core.matrix.wbc.repository"},
         entityManagerFactoryRef = "wbcEntityManagerFactory", 
        transactionManagerRef = "wbcTransactionManager") 

@Configuration
public class WbcDataBaseConfiguration {
    
      @Autowired
    private WbcProperties wbcProperties;
    
    private static final String NAME_POOL = "WbcPool";    
    
    @Bean
    public LocalContainerEntityManagerFactoryBean wbcEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(wbcDataSource());
        em.setPackagesToScan(
                new String[]{"com.core.matrix.wbc.model", "com.core.matrix.wbc.repository"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
       // properties.put("hibernate.tool.hbm2ddl.SchemaUpdate", "true");
        em.setJpaPropertyMap(properties);

        return em;
    }

    
    @Bean
    public DataSource wbcDataSource() {

        DataSource dataSource;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setInitializationFailTimeout(-1);
        hikariConfig.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        hikariConfig.setMinimumIdle(wbcProperties.getDatasource().getMinimumIdle());
        hikariConfig.setMaximumPoolSize((wbcProperties.getDatasource().getMaximumPoolSize()));
        hikariConfig.setValidationTimeout((wbcProperties.getDatasource().getValidationTimeout()));
        hikariConfig.setIdleTimeout((wbcProperties.getDatasource().getIdleTimeout()));
        hikariConfig.setConnectionTimeout((wbcProperties.getDatasource().getConnectionTimeout()));
        hikariConfig.setAutoCommit((wbcProperties.getDatasource().isAutoCommit()));
        hikariConfig.setJdbcUrl(wbcProperties.getDatasource().getSqlserver().getDataUrl());
        hikariConfig.setUsername(wbcProperties.getDatasource().getSqlserver().getDataSourceUser());
        hikariConfig.setPassword(wbcProperties.getDatasource().getSqlserver().getDataSourcePassword());
        hikariConfig.setPoolName(NAME_POOL);
        
        hikariConfig.setConnectionTestQuery(wbcProperties.getDatasource().getSqlserver().getConnectionTestQuery());

        dataSource = new HikariDataSource(hikariConfig);       

        return dataSource;
    }

    
    @Bean
    public PlatformTransactionManager wbcTransactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                wbcEntityManagerFactory().getObject());
        return transactionManager;
    }

    
  
    
    
    
    
}
