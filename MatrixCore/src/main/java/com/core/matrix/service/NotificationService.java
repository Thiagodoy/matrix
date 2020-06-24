/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.dto.Action;
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
    private final String URL_SUBSCRIBER_TOPIC_ACTION = "/topic/action/notification";

    @Transactional
    public void read(Long id) {

        Notification notification = repository.findById(id).get();
        notification.setRead(true);

        repository.save(notification);
    }

    @Transactional(readOnly = true)
    public void pushUserNotifications(String userId, String sessionId) {

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
    public Long push(Notification notification) {

        boolean canWrite = repository.findByToAndTaskIdAndProcessId(notification.getTo(), notification.getTaskId(), notification.getProcessId()).isEmpty();
        Long idNotification = null;

        if (canWrite) {
            notification = repository.save(notification);
            Optional<SessionWebsocket> opt = sessionWebsocketRepository.findByUserId(notification.getTo()).stream().findFirst();
            
            if (opt.isPresent()) {
                SessionWebsocket session = opt.get();
                SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
                headerAccessor.setSessionId(session.getSessionId());
                messagingTemplate.convertAndSendToUser(session.getSessionId(), URL_SUBSCRIBER_QUEUE, notification, headerAccessor.getMessageHeaders());
            }
            
            return notification.getId();
        }
        
        return idNotification;

    }

    @Transactional
    public void deleteNotificationByTaskId(String taskId) {
        this.repository.deleteByTaskId(taskId);
    }

    @Transactional
    public void pushActionRemoveByTaskId(String taskId) {
        messagingTemplate.convertAndSend(URL_SUBSCRIBER_TOPIC_ACTION, new Action(taskId, null, Action.ActionType.REMOVE));
    }

    @Transactional
    public void pushActionRemoveByNotificationId(List<Long> ids) {

        ids.forEach(id -> {
            messagingTemplate.convertAndSend(URL_SUBSCRIBER_TOPIC_ACTION, new Action(null, id, Action.ActionType.REMOVE));
        });

    }

}
