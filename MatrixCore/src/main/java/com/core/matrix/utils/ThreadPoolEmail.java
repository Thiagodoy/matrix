/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import com.core.matrix.model.Email;
import com.core.matrix.model.Log;
import com.core.matrix.model.Template;
import com.core.matrix.service.EmailService;
import com.core.matrix.service.LogService;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 *
 * @author thiag
 */
@Component
public class ThreadPoolEmail {

    private final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    private final Map<String,Email> map = new HashMap<>();
    private final EmailService emailService;
    private final LogService logService;
    private final JavaMailSender sender;

    public ThreadPoolEmail(EmailService emailService, LogService logService, JavaMailSender sender) {

        this.emailService = emailService;
        this.logService = logService;
        this.sender = sender;
    }

    
    public synchronized void submit(List<Email> emails){
        emails.forEach(email->{        
            this.submit(email);
        });
    }
    
    public synchronized void submit(Email email){

        email.setStatus(Email.EmailStatus.QUEUE);
        email.normalizeData();

        if (!this.map.containsKey(email.generateKey())) {
            Long idEmail;
            
            try {
                idEmail = this.emailService.save(email);
                email = this.emailService.find(idEmail);
            } catch (Exception ex) {
                Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "[ submit ] -> Erro ao salvar email", ex);
            }
            
            
            this.map.put(email.generateKey(),email);
            
            this.pool.submit(new SenderEmail(email));
        } else {
            Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.INFO, "[ submit ] email já esta na fila -> " + email.getData());
        }
    }

    private synchronized void send(Email email) {

        MimeMessage message = sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Map<String, String> parameters = Utils.toMap(email.getData());

            Template template = email.getTemplate();
            String content = template.getTemplate();

            helper.setSubject(template.getSubject());
            helper.setFrom("portal@matrixenergia.com");
            

            if (parameters != null && parameters.size() > 0) {
                for (String key : parameters.keySet()) {
                    content = content.replace(key, parameters.get(key));
                    if (key.equals(":email")) {
                        helper.setTo(parameters.get(key).split(";"));
                    }                    
                }
            }
            
          
            
            content = Utils.replaceAccentToEntityHtml(content);
            helper.setSentDate(new Date());
            helper.setText(content, true); 
            
            File logo = Utils.loadLogo(Constants.IMAGE_LOGO_MATRIX);
            FileSystemResource res = new FileSystemResource(logo);                    
            helper.addInline("identifier1", res);
            
            sender.send(message);
            
            FileUtils.forceDelete(logo);
            
            this.finalize(email);

        } catch (Exception ex) {
            Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "[send]", ex);
            this.error(email, ex);

        }

    }

    private synchronized void finalize(Email email) {

        email.setStatus(Email.EmailStatus.SENT);
        try {
            this.emailService.update(email);
        } catch (Exception ex) {
            Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "[ finalize ]", ex);
        } finally {
            this.map.remove(email.generateKey());
        }

    }

    private synchronized void error(Email email, Exception exception) {

        try {
            if (email.getRetry() <= 3) {
                email.setRetry(email.getRetry() + 1);
                this.emailService.update(email);
                this.map.remove(email.generateKey());
                this.submit(email);

            } else {
                Log log = new Log();
                log.setMessageErrorApplication(exception.getMessage());
                Long idLog = this.logService.save(log);

                email.setStatus(Email.EmailStatus.ERROR);
                email.setError(idLog);
                this.emailService.update(email);
                this.map.remove(email.generateKey());
            }
        } catch (Exception e) {
            Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "[ error ]", e);
        }

    }

    @AllArgsConstructor
    private class SenderEmail implements Runnable {

        private Email email;

        @Override
        public void run() {
            try {
                ThreadPoolEmail.this.send(email);
            } catch (Exception e) {
                ThreadPoolEmail.this.error(email, e);
            }
        }

    }
    
    public boolean  executorIsRunning(){        
        return !this.pool.isTerminated();
    }

}
