/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.websocket;

import com.core.matrix.model.SessionWebsocket;
import com.core.matrix.repository.SessionWebsocketRepository;
import java.util.Optional;
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
    
    
    @MessageMapping(value = {"/register"})
    @Transactional
    public void register(SessionWebsocket sessionWebsocket){  
        
        Optional<SessionWebsocket> opt = repository.findByUserId(sessionWebsocket.getSessionId());
        
        if(opt.isPresent()){
            repository.delete(opt.get());
        }
    
        
        repository.save(sessionWebsocket);        
    }
    
    @MessageMapping(value = {"/unregister"})
    @Transactional
    public void unregister(SessionWebsocket sessionWebsocket){        
        repository.deleteById(sessionWebsocket.getSessionId());        
    }
    
    
}
