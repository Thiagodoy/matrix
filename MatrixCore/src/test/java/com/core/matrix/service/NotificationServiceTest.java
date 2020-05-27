/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author thiag
 */
@SpringBootTest
public class NotificationServiceTest {
    
    
    @Autowired
    private NotificationService notificationService;
    
    
    @Test
    public void sendActionRemoveTask(){       
        
        this.notificationService.pushActionRemoveTask("1175046");        
        
    }
}
