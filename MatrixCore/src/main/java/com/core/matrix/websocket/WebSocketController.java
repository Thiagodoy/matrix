/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.websocket;

import com.core.matrix.model.SessionWebsocket;
import com.core.matrix.repository.SessionWebsocketRepository;
import com.core.matrix.service.NotificationService;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 *
 * @author thiag
 */
@Controller
public class WebSocketController {

    @Autowired
    private SessionWebsocketRepository repository;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping(value = {"/register"})
    @Transactional
    public void register(SessionWebsocket sessionWebsocket) {

        List<SessionWebsocket> sessions = repository.findByUserId(sessionWebsocket.getUserId());

        if (!sessions.isEmpty()) {
            sessions.forEach(s -> {
                repository.delete(s);
            });
        }

        repository.save(sessionWebsocket);

        notificationService.pushUserNotifications(sessionWebsocket.getUserId(), sessionWebsocket.getSessionId());
    }

    @MessageMapping(value = {"/unregister"})
    @Transactional
    public void unregister(SessionWebsocket sessionWebsocket) {
        repository.deleteById(sessionWebsocket.getSessionId());
    }

    @MessageMapping(value = {"/read"})
    @Transactional
    public void read(Long id) {
        notificationService.read(id);
    }
}
