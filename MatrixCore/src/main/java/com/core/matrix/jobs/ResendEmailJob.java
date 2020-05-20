/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.jobs;

import com.core.matrix.model.Email;
import com.core.matrix.service.EmailService;
import com.core.matrix.specifications.EmailSpecification;
import com.core.matrix.utils.ThreadPoolEmail;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class ResendEmailJob {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ThreadPoolEmail threadPoolEmail;
    
    
    @Scheduled(cron = "0 0/2 * 1/1 * ?")
    public void run() {

        
        Specification spc = EmailSpecification.find(null, null, null, null, Email.EmailStatus.READY);
        
        try {
            Page<Email> page = emailService.find(spc, Pageable.unpaged());            
            page.getContent().forEach(email-> this.threadPoolEmail.submit(email));            
            
        } catch (Exception ex) {
            Logger.getLogger(ResendEmailJob.class.getName()).log(Level.SEVERE, "[ run ]", ex);
        }
        
        
        
    }
    
    // @Scheduled(cron = "0 0/2 * 1/1 * ?")
    public void runTestWebsochekt() {
    
//        messagingTemplate.convertAndSend("/topic/stocks", "TESETE");
//        
//        Optional<SessionWebsocket> opt = repository.findByUserId("teste1matrix@mailinator.com");
//        
//        
//        if(opt.isPresent()){
//            
//            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
//            headerAccessor.setSessionId(opt.get().getSessionId());
//            
//            messagingTemplate.convertAndSendToUser(opt.get().getSessionId(),"/queue/notification", "Vc é o Thiago",headerAccessor.getMessageHeaders());
//        }   
        
        
    
    }

}
