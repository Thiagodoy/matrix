/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import com.core.matrix.properties.ActivitiProperties;
import com.core.matrix.properties.MatrixProperties;
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
import com.core.matrix.workflow.task.ProcessFilesInLoteTask;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    private MatrixProperties matrixProperties;

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
                new String[]{"com.core.matrix.workflow.model", "com.core.matrix.workflow.repository"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        //properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        // properties.put("hibernate.tool.hbm2ddl.SchemaUpdate", "true");
        if(activeProfile.equals("test")){
            properties.put("hibernate.physical_naming_strategy","com.core.matrix.utils.PhysicalNamingStrategyImpl");
        }
        
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

        Logger.getLogger(ActivitiCoreConfiguration.class.getName()).log(Level.INFO, "Profile -> " + activeProfile);

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

    @Bean
    @Scope(value = "singleton")
    public ProcessEngine processEngine() {

        ProcessEngineConfiguration s = new StandaloneProcessEngineConfiguration()
                .setCustomSessionFactories(null)
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                //.setPro
                // .setAsyncExecutorEnabled(true)

                .setDataSource(this.dataSource())
                .setAsyncFailedJobWaitTime(2147483647)
                .setDefaultFailedJobWaitTime(2147483647)
                .setJobExecutorActivate(true)
                .setMailServerDefaultFrom("portal@matrixenergia.com")
                .setMailServerHost("smtp.office365.com")
                .setMailServerPort(587)
                .setMailServerUsername("portal@matrixenergia.com")
                .setMailServerPassword("P@ortal2020")
                .setMailServerUseSSL(false)
                .setMailServerUseTLS(true);

        ProcessEngine processEngine = s.buildProcessEngine();

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

    @Bean
    public FileValidationTask fileValidationTask(ApplicationContext context) {
        return new FileValidationTask(context);
    }

    @Bean
    public DataValidationTask dataValidationTask(ApplicationContext context) {
        return new DataValidationTask(context);
    }

    @Bean
    public CalculateTask calculateTask(ApplicationContext context) {
        return new CalculateTask(context);
    }

    @Bean
    public CleanFiles cleanFiles(ApplicationContext context) {
        return new CleanFiles(context);
    }

    @Bean
    public BillingContractsTask billingContractsTask(ApplicationContext context) {
        return new BillingContractsTask(context);
    }
    
    @Bean
    public DeleteProcessInstanceTask deleteProcessInstanceTask(ApplicationContext context){
        return new DeleteProcessInstanceTask(context);
    }

    @Bean
    public ChangeStatusFileTask changeStatusFileTask(ApplicationContext context) {
        return new ChangeStatusFileTask(context);
    }

    @Bean
    public CheckStatusFileResultTask checkStatusFileResultTask(ApplicationContext context) {
        return new CheckStatusFileResultTask(context);
    }

    @Bean
    public CleanFileResult cleanFileResult(ApplicationContext context) {
        return new CleanFileResult(context);
    }

    @Bean
    public CheckLevelOfApproval checkLevelOfApproval(ApplicationContext context) {
        return new CheckLevelOfApproval(context);
    }

    @Bean
    public CheckTake checkTake(ApplicationContext context) {
        return new CheckTake(context);
    }
    
    @Bean
    public ProcessFilesInLoteTask processFilesInLoteTask(ApplicationContext context){
        return new ProcessFilesInLoteTask(context,matrixProperties.getThreadPoolSize());
    }

}
