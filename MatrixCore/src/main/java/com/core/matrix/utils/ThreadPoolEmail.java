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
import com.core.matrix.service.TemplateService;
import static com.core.matrix.utils.Constants.IMAGE_LOGO_MATRIX_HEADER;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final Map<String, Email> map = new HashMap<>();
    private final EmailService emailService;
    private final LogService logService;
    private final JavaMailSender sender;
    private final TemplateService templateService;

    private static final Map<String, String> paths = new HashMap<>();
    private static final Map<String, FileSystemResource> resources = new HashMap<>();

    static {
        paths.put("identifier1", Constants.IMAGE_LOGO_MATRIX);
        paths.put("attachment-1", IMAGE_LOGO_MATRIX_HEADER);
        paths.put("attachment-2", Constants.IMAGE_LOGO_MATRIX_FOOTER);

        loadResources();
    }

    public ThreadPoolEmail(EmailService emailService, LogService logService, JavaMailSender sender, TemplateService templateService) {

        this.emailService = emailService;
        this.logService = logService;
        this.sender = sender;
        this.templateService = templateService;
    }

    public synchronized void submit(List<Email> emails) {
        emails.forEach(email -> {
            this.submit(email);
        });
    }

    private static void loadResources() {

        paths.keySet().stream().forEach(key -> {

            try {
                File logo = Utils.loadLogo(paths.get(key));
                FileSystemResource res = new FileSystemResource(logo);
                resources.put(key, res);
            } catch (IOException ex) {
                Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "Error in load of resources", ex);
            }

        });

    }

    public synchronized void submit(Email email) {

        if (!Optional.ofNullable(email.getStatus()).isPresent()) {
            email.normalizeData();
        }

        email.setStatus(Email.EmailStatus.QUEUE);
        Map<String, File> attachments = email.getAttachment();

        if (!this.map.containsKey(email.generateKey())) {
            Long idEmail;

            try {

                idEmail = this.emailService.save(email);
                email = this.emailService.find(idEmail);
                email.setAttachment(attachments);
                this.map.put(email.generateKey(), email);
                this.pool.submit(new SenderEmail(email));
            } catch (Exception ex) {
                Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "[ submit ] -> Erro ao salvar email", ex);
            }
        } else {
            Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.INFO, "[ submit ] email já esta na fila de envio -> " + email.getData());
        }
    }

    private synchronized void send(Email email) {

        MimeMessage message = sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Map<String, String> parameters = Utils.toMap(email.getData());

            Template template = this.templateService.find(email.getTemplate());

            String content = template.getTemplate();

            helper.setSubject(template.getSubject());
            helper.setFrom("portal@matrixenergia.com");

            if (parameters != null && parameters.size() > 0) {
                for (String key : parameters.keySet()) {
                    if (email.getContent() == null) {
                        content = content.replace(key, parameters.get(key));
                    } else {
                        content = email.getContent();
                    }
                    if (key.equals(":email")) {
                        helper.setTo(parameters.get(key).split(";"));
                    }
                    if (key.equals(":subject")) {
                        helper.setSubject(parameters.get(key));
                    }
                }
            }

            content = Utils.replaceAccentToEntityHtml(content);
            helper.setSentDate(new Date());
            helper.setText(content, true);

            String[] attaStrings = template.getAttachments().split(";");

            for (String attaString : attaStrings) {
                helper.addInline(attaString, resources.get(attaString));
            }

            for (String nameFile : email.getAttachment().keySet()) {
                helper.addAttachment(nameFile, email.getAttachment().get(nameFile));
            }

            sender.send(message);

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
            deleteAttachments(email);
        }

    }

    private void deleteAttachments(Email email) {

        for (File file : email.getAttachment().values()) {
            try {
                FileUtils.forceDelete(file);
            } catch (Exception e) {
                Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "[ deleteAttachments ]", e);
            }
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

    public boolean executorIsRunning() {
        return !this.pool.isTerminated();
    }

    public static void deleteFiles() {

        resources.keySet().stream().forEach(key -> {
            File file = resources.get(key).getFile();
            try {
                FileUtils.forceDelete(file);
            } catch (IOException ex) {
                Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void shutdown() {
        pool.shutdown();
    }
}
