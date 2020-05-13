/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import com.core.matrix.model.Email;
import com.core.matrix.service.EmailService;
import com.core.matrix.service.LogService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class ThreadPoolEmail {

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private Map<String, Email> map = new HashMap<String, Email>();

    @Autowired
    private static EmailService emailService;

    @Autowired
    private static LogService logService;

    public synchronized void submit(Email email) {
        
        email.setStatus(Email.EmailStatus.QUEUE);
        
        
        if(!this.map.containsKey(map)){
            emailService.save(email);
            this.executor.submit(new SenderEmail(email));
        }else{
            Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.INFO, "[ submit ] -> " + email.getData());    
        }
    }

    private synchronized static void send(Email email) {

    }

    private synchronized static void finalize(Email email) {

        email.setStatus(Email.EmailStatus.SENT);
        try {
            emailService.update(email);
        } catch (Exception ex) {
            Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "[ finalize ]", ex);            
        }

    }

    private synchronized static void error(Email email, Exception exception) {

    }

    @AllArgsConstructor
    private class SenderEmail implements Runnable {

        private Email email;

        @Override
        public void run() {
            try {
                ThreadPoolEmail.send(email);
            } catch (Exception e) {
                Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "[ run ]", e);   
                ThreadPoolEmail.error(email, e);                
            }
        }

    }

}
