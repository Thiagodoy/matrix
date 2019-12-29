/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import javax.sql.DataSource;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author thiag
 */
@Configuration
public class ActivitiCoreConfiguration {

//    @Autowired
//    private ActivitiProperties activitiProperties;
//    
//    @org.springframework.beans.factory.annotation.Value("${spring.profiles.active}")
//    private String activeProfile;
    
    @Autowired
    private DataSource dataSource; 

    @Bean
    @Scope(value = "singleton")
    public ProcessEngine processEngine() {

        
//        Logger.getLogger(ActivitiCoreConfiguration.class.getName()).log(Logger.Level.INFO, "Environment -> " + activeProfile);
//        
//        DataSource dataSource;
//
//        HikariConfig hikariConfig = new HikariConfig();
//        hikariConfig.setInitializationFailTimeout(-1);
//        
//        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        
//
//        hikariConfig.setMinimumIdle(activitiProperties.getDatasource().getMinimumIdle());
//        hikariConfig.setMaximumPoolSize((activitiProperties.getDatasource().getMaximumPoolSize()));
//        hikariConfig.setValidationTimeout((activitiProperties.getDatasource().getValidationTimeout()));
//        hikariConfig.setIdleTimeout((activitiProperties.getDatasource().getIdleTimeout()));
//        hikariConfig.setConnectionTimeout((activitiProperties.getDatasource().getConnectionTimeout()));
//        hikariConfig.setAutoCommit((activitiProperties.getDatasource().isAutoCommit()));
//        hikariConfig.setJdbcUrl(activitiProperties.getDatasource().getSqlserver().getDataUrl());
//        hikariConfig.setUsername(activitiProperties.getDatasource().getSqlserver().getDataSourceUser());
//        hikariConfig.setPassword(activitiProperties.getDatasource().getSqlserver().getDataSourcePassword());
//        hikariConfig.setPoolName("ActivitiPool");
//        hikariConfig.setConnectionTestQuery(activitiProperties.getDatasource().getSqlserver().getConnectionTestQuery());
//
//        dataSource = new HikariDataSource(hikariConfig);
//
//        List<SessionFactory> sessions = new ArrayList<>();
      //  sessions.add(new MyGroupManagerFactory());
      ///  sessions.add(new MyUserManagerFactory());

        ProcessEngineConfiguration s = new StandaloneProcessEngineConfiguration()
                .setCustomSessionFactories(null)                
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
               // .setAsyncExecutorEnabled(true)
                .setAsyncExecutorActivate(true)
                .setDataSource(dataSource)
                .setAsyncFailedJobWaitTime(2147483647)
                .setDefaultFailedJobWaitTime(2147483647);
        
        //Logger.getLogger(ActivitiCoreConfiguration.class.getName()).log(Logger.Level.INFO, " ProcessEngine created -> " + activeProfile);

        return s.buildProcessEngine();

    }

}
