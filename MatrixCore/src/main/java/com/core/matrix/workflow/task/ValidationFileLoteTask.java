/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import static com.core.matrix.utils.Constants.*;

import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.io.BeanIO;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.service.ContractCompInformationService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.service.MeansurementFileService;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
@Data

public class ValidationFileLoteTask implements JavaDelegate {

    private static ApplicationContext context;

    private TaskService taskService;
    private MeansurementFileService service;
    private MeansurementFileDetailService detailService;
    private DelegateExecution delegateExecution;
    private LogService logService;
    private ContractCompInformationService contractInformationService;

    private List<MeansurementFile> files;

    private List<Log> logs;

    public ValidationFileLoteTask() {

        synchronized (ValidationFileLoteTask.context) {
            this.taskService = ValidationFileLoteTask.context.getBean(TaskService.class);
            this.service = ValidationFileLoteTask.context.getBean(MeansurementFileService.class);
            this.detailService = ValidationFileLoteTask.context.getBean(MeansurementFileDetailService.class);
            this.logService = ValidationFileLoteTask.context.getBean(LogService.class);
            this.contractInformationService = ValidationFileLoteTask.context.getBean(ContractCompInformationService.class);
        }

    }

    public ValidationFileLoteTask(ApplicationContext context) {
        ValidationFileLoteTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

        try {
            delegateExecution = de;
            logs = Collections.synchronizedList(new ArrayList<>());

            taskService
                    .getProcessInstanceAttachments(delegateExecution.getProcessInstanceId())
                    .parallelStream()
                    .forEach(attachment -> {

                        InputStream stream = null;
                        String fileName = null;
                        try {

                            synchronized (taskService) {
                                stream = taskService.getAttachmentContent(attachment.getId());
                                stream = removeLinesEmpty(stream);
                                fileName = taskService.getAttachment(attachment.getId()).getName();
                            }

                            BeanIO reader = new BeanIO();
                            Optional<FileParsedDTO> opt = reader.<FileParsedDTO>parse(stream);

                            if (!opt.isPresent()) {

                                if (reader.getErrors().isEmpty()) {
                                    this.generateLog(de, null, "Não foi possivel aplicar o parse no arquivo -> " + fileName);
                                } else {
                                    final String name = fileName;
                                    reader.getErrors().stream().forEach(error -> {
                                        this.generateLog(de, null, error + "arquivo ->[" + name + "]");
                                    });
                                }
                            }

                        } catch (Exception e) {
                            this.generateLog(de, e, "Layout inválido do arquivo -> [" + fileName + "]");
                        }
                    });

            String response = this.logs.isEmpty() ? RESPONSE_LAYOUT_VALID : RESPONSE_LAYOUT_INVALID;
            delegateExecution.setVariable(CONTROLE, response);

        } catch (Exception e) {
            Logger.getLogger(ValidationFileLoteTask.class.getName()).log(Level.SEVERE, "[execute]", e);
            delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
            this.generateLog(de, e, "Erro ao processar os arquivos");
        }

        if (!this.logs.isEmpty()) {
            this.logService.save(logs);
        }

    }

    private void generateLog(DelegateExecution de, Exception e, String message) {
        Log log = new Log();
        log.setMessage(message);
        String messageError = Optional.ofNullable(e).isPresent() ? e.getMessage() : "";

        log.setMessageErrorApplication(messageError);
        log.setProcessInstanceId(de.getProcessInstanceId());
        log.setProcessName(de.getProcessBusinessKey());
        log.setActivitiName(de.getCurrentActivityName());
        this.logs.add(log);
    }

    private synchronized InputStream removeLinesEmpty(InputStream stream) throws IOException {

        StringBuilder sb = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        String line;
        while ((line = br.readLine()) != null) {

            if (line.split(";").length > 0) {
                sb.append(line + System.lineSeparator());
            }
        }

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes(Charset.forName("ISO-8859-1")));
        return inputStream;

    }

}
