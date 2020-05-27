/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import com.core.matrix.workflow.listener.RuntimeListener;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author thiag
 */
@EnableJpaRepositories(basePackages = {"com.core.matrix.workflow.repository"},
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")

@Configuration
public class RuntimeListenerConfiguration implements EnvironmentAware {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Override
    public void setEnvironment(Environment e) {

    }

    @Bean
    public RuntimeListener runtimeListener(ApplicationContext applicationContext) {

        RuntimeListener listener = new RuntimeListener(applicationContext, identityService);

        runtimeService.addEventListener(listener,
                ActivitiEventType.TASK_ASSIGNED,
                ActivitiEventType.TASK_CREATED,
                ActivitiEventType.TASK_COMPLETED);

        return listener;

    }

}
