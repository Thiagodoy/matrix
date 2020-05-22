/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Notification;
import com.core.matrix.model.SessionWebsocket;
import com.core.matrix.repository.NotificationRepository;
import com.core.matrix.repository.SessionWebsocketRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private SessionWebsocketRepository sessionWebsocketRepository;

    @Autowired
    private NotificationRepository repository;

    private final String URL_SUBSCRIBER_QUEUE = "/queue/notification";

    @Transactional(readOnly = true)
    public void push(String userId, String sessionId) {

        List<Notification> notifications = repository.findByToAndIsRead(userId, false);

        notifications.forEach(n -> {
            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(sessionId);
            messagingTemplate.convertAndSendToUser(sessionId, URL_SUBSCRIBER_QUEUE, n, headerAccessor.getMessageHeaders());
        });

    }

    public void push(List<Notification> notification) {
        notification.forEach(n -> {
            this.push(n);
        });
    }

    @Transactional
    public void push(Notification notification) {

        notification = repository.save(notification);

        Optional<SessionWebsocket> opt = sessionWebsocketRepository.findByUserId(notification.getTo());

        if (opt.isPresent()) {

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
            headerAccessor.setSessionId(opt.get().getSessionId());

            messagingTemplate.convertAndSendToUser(opt.get().getSessionId(), URL_SUBSCRIBER_QUEUE, notification, headerAccessor.getMessageHeaders());
        }
    }

}
