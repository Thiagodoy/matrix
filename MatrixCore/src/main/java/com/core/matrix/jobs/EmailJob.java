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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class EmailJob {

    private static EmailService emailService;

    private static ThreadPoolEmail threadPoolEmail;

    private EmailJob(EmailService emailService, ThreadPoolEmail threadPoolEmail) {
        EmailJob.emailService = emailService;
        EmailJob.threadPoolEmail = threadPoolEmail;
    }

    @Scheduled(cron = "0 0/2 * 1/1 * ?")
    public void resend() {
        Specification spc = EmailSpecification.find(null, null, null, null, Email.EmailStatus.READY);
        try {
            Page<Email> page = emailService.find(spc, Pageable.unpaged());
            page.getContent().forEach(email -> this.threadPoolEmail.submit(email));

        } catch (Exception ex) {
            Logger.getLogger(EmailJob.class.getName()).log(Level.SEVERE, "[ Can't run process for resend all emails ]", ex);
        }
    }

    @Scheduled(cron = "0 0 1 ? * MON-FRI")
    public void clearEmails() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        Specification spc = EmailSpecification.createAt(start);
        try {
            Page<Email> response = emailService.find(spc, Pageable.unpaged());
            response.getContent().parallelStream().forEach(EmailJob::deleteEmail);
        } catch (Exception e) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, "The application can't run [clearEmail]", e);
        }

    }

    private static synchronized void deleteEmail(Email email) {
        try {
            emailService.delete(email.getId());
        } catch (Exception e) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, "The application can't delete this email -> " + email.getId(), e);
        }
    }
}
