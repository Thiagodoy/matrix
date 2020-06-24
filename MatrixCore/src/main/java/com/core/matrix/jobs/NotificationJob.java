/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.jobs;

import com.core.matrix.model.Notification;
import com.core.matrix.repository.NotificationRepository;
import com.core.matrix.repository.SessionWebsocketRepository;
import com.core.matrix.specifications.NotificationSpecification;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Component
public class NotificationJob {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private SessionWebsocketRepository sessionRepository;

    @Scheduled(cron = "0 0 1 ? * MON-FRI")
    @Transactional
    public void clean() {

        Specification<Notification> spc = NotificationSpecification.isRead();

        repository.findAll(spc).stream().forEach(n -> {
            this.repository.delete(n);
        });

        removeSessionInactive();
    }

    private void removeSessionInactive() {
        LocalDateTime dateTime = java.time.LocalDate.now().atStartOfDay();
        sessionRepository.deleteByCreatedAtBefore(dateTime);
    }

}
