/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@ConfigurationProperties(prefix = "email", ignoreUnknownFields = true)
public class EmailServiceProperties {

    @Getter
    @Setter
    private String host;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private int port;
    
    @Getter
    @Setter
    private String protocol;

    @Getter
    @Setter
    private boolean auth;

    @Getter
    @Setter
    private StartTls starttls;

    public static class StartTls {

        @Getter
        @Setter
        private boolean enable;

        @Getter
        @Setter
        private boolean required;
    }

}
