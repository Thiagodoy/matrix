/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import com.core.matrix.properties.ActivitiProperties;
import com.core.matrix.workflow.task.BillingContractsTask;
import com.core.matrix.workflow.task.CalculateTask;
import com.core.matrix.workflow.task.ChangeStatusFileTask;
import com.core.matrix.workflow.task.CheckLevelOfApproval;
import com.core.matrix.workflow.task.CheckStatusFileResultTask;
import com.core.matrix.workflow.task.CheckTake;
import com.core.matrix.workflow.task.CleanFileResult;
import com.core.matrix.workflow.task.CleanFiles;
import com.core.matrix.workflow.task.DataValidationTask;
import com.core.matrix.workflow.task.DeleteProcessInstanceTask;
import com.core.matrix.workflow.task.FileValidationTask;
import com.core.matrix.workflow.task.PersistInformationTask;
import com.core.matrix.workflow.task.ProcessFilesInLoteTask;
import com.core.matrix.workflow.task.ResetResultTask;
import com.core.matrix.workflow.task.ValidationFileLoteTask;
import com.core.matrix.workflow.task.listener.ResultFileLoteListener;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;

import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.jobexecutor.JobExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author thiag
 */
@EnableJpaRepositories(basePackages = {"com.core.matrix.workflow.repository"},
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")

@Configuration
public class ActivitiCoreConfiguration implements EnvironmentAware {

    @Autowired
    private ActivitiProperties activitiProperties;    

    @Autowired
    private ApplicationContext context;

    private Environment environment;

    @org.springframework.beans.factory.annotation.Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public void setEnvironment(Environment e) {
        this.environment = e;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(
                new String[]{"com.core.matrix.workflow.model", "com.core.matrix.workflow.repository"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        //properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        // properties.put("hibernate.tool.hbm2ddl.SchemaUpdate", "true");
        if (activeProfile.equals("test")) {
            properties.put("hibernate.physical_naming_strategy", "com.core.matrix.utils.PhysicalNamingStrategyImpl");
        }

        em.setJpaPropertyMap(properties);

        return em;
    }

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
        hikariConfig.setMaxLifetime(activitiProperties.getDatasource().getMaxLifetime());
        hikariConfig.setPoolName("ActivitiPool");

        //hikariConfig.setConnectionTestQuery(activitiProperties.getDatasource().getSqlserver().getConnectionTestQuery());
        dataSource = new HikariDataSource(hikariConfig);

        Logger.getLogger(ActivitiCoreConfiguration.class.getName()).log(Level.INFO, "Profile -> " + activeProfile);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    @Scope(value = "singleton")
    public ProcessEngine processEngine() {

        ProcessEngineConfiguration s = new StandaloneProcessEngineConfiguration()
                .setCustomSessionFactories(null)
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setDataSource(this.dataSource())
                //.setTransactionsExternallyManaged(true)
                //.setJpaHandleTransaction(true)
                .setAsyncFailedJobWaitTime(2147483647)
                .setDefaultFailedJobWaitTime(2147483647)
                .setAsyncExecutorActivate(true)
                .setJobExecutorActivate(true);

        ProcessEngine processEngine = s.buildProcessEngine();

        JobExecutor jobExecutor = processEngine.getProcessEngineConfiguration().getJobExecutor();
        jobExecutor.setLockTimeInMillis(15 * 60000);

        return processEngine;

    }

    @Bean
    public IdentityService identityService() {
        return this.processEngine().getIdentityService();
    }

    @Bean
    public RuntimeService runtimeService() {
        return this.processEngine().getRuntimeService();
    }

    @Bean
    public RepositoryService repositoryService() {
        return this.processEngine().getRepositoryService();
    }

    @Bean
    public TaskService taskService() {
        return this.processEngine().getTaskService();
    }

    @Bean
    public ManagementService managementService() {
        return this.processEngine().getManagementService();
    }

    @Bean
    public HistoryService historyService() {
        return this.processEngine().getHistoryService();
    }

    
    
    @PostConstruct
    private void configureContext() {
        FileValidationTask.setContext(context);
        DataValidationTask.setContext(context);
        CalculateTask.setContext(context);
        CleanFiles.setContext(context);
        BillingContractsTask.setContext(context);
        DeleteProcessInstanceTask.setContext(context);
        ChangeStatusFileTask.setContext(context);
        CheckStatusFileResultTask.setContext(context);
        CleanFileResult.setContext(context);
        CheckLevelOfApproval.setContext(context);
        CheckTake.setContext(context);
        ProcessFilesInLoteTask.setContext(context);
        ValidationFileLoteTask.setContext(context);
        ResultFileLoteListener.setContext(context);
        ResetResultTask.setContext(context);
        PersistInformationTask.setContext(context);
    }

}
