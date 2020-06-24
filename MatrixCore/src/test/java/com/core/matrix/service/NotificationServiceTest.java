/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Notification;
import java.util.ArrayList;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author thiag
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    private static Long notificationId;

    @Test
    @Order(1)
    public void sendNotification() {
        Notification notification = new Notification();
        notification.setForm("form-teste");
        notification.setNameProcessId("Teste");
        notification.setProcessId("9999");
        notification.setRead(false);
        notification.setTaskId("9999");
        notification.setTo("admin@admin.com");

        notificationId = notificationService.push(notification);
    }

    @Test
    @Order(2)
    public void sendActionRemoveNotificationById() {

        ArrayList<Long> id = new ArrayList<>();
        id.add(notificationId);

        this.notificationService.pushActionRemoveByNotificationId(id);

    }
}
