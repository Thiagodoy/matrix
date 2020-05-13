/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import com.core.matrix.properties.EmailServiceProperties;
import com.core.matrix.service.EmailService;
import com.core.matrix.service.LogService;
import com.core.matrix.utils.ThreadPoolEmail;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
public class EmailServiceConfiguration {

    @Autowired
    private EmailServiceProperties properties;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(properties.getHost());
        mailSender.setPort(properties.getPort());

        mailSender.setUsername(properties.getUsername());
        mailSender.setPassword(properties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", properties.getProtocol());
        props.put("mail.smtp.auth", properties.isAuth());
        props.put("mail.smtp.starttls.enable", properties.getStarttls().isEnable());
        props.put("mail.debug", properties.getStarttls().isRequired());

        return mailSender;
    }

    @Bean
    @Scope("singleton")
    public ThreadPoolEmail threadPoolEmail (EmailService emailService, LogService logService) {
        return new ThreadPoolEmail(emailService, logService, getJavaMailSender());

    }

}
