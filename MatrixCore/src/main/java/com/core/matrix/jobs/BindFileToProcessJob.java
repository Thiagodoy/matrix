/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.jobs;

import com.core.matrix.dto.ProcessFilesInLoteStatusDTO;
import com.core.matrix.io.BeanIO;
import static com.core.matrix.utils.Constants.LIST_ATTACHMENT_ID;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Data;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author thiag
 */
@Data
public class BindFileToProcessJob implements Runnable {

    private TaskService taskService;
    private ProcessFilesInLoteStatusDTO processFilesInLoteStatusDTO;

    @Override
    public void run() {

        File file = null;

        try {

            Map<String, Object> parameters = new HashMap<>();

            if (this.processFilesInLoteStatusDTO.isUnitConsumerOrFlat()) {
                parameters.put(LIST_ATTACHMENT_ID, Collections.EMPTY_LIST);
                taskService.complete(processFilesInLoteStatusDTO.getTaskId(), parameters);
            } else {

                BeanIO beanIO = new BeanIO();
                file = beanIO.write(processFilesInLoteStatusDTO.getFileParsedDTO(), processFilesInLoteStatusDTO.getTaskId(), processFilesInLoteStatusDTO.getProcessInstanceId(), 1);
                InputStream ip = new FileInputStream(file);

                Attachment attachment = taskService
                        .createAttachment(
                                "application/vnd.ms-excel",
                                null,
                                processFilesInLoteStatusDTO.getProcessInstanceId(),
                                file.getName(),
                                "attachmentDescription",
                                ip);

                parameters.put(LIST_ATTACHMENT_ID, Arrays.asList(attachment.getId()));
                taskService.complete(processFilesInLoteStatusDTO.getTaskId(), parameters);
                processFilesInLoteStatusDTO.setStatus(ProcessFilesInLoteStatusDTO.Status.ASSOCIATED);
            }

        } catch (Exception ex) {
            Logger.getLogger(BindFileToProcessJob.class.getName()).log(Level.SEVERE, "[run]", ex);
            processFilesInLoteStatusDTO.setStatus(ProcessFilesInLoteStatusDTO.Status.ERROR);
            processFilesInLoteStatusDTO.setError(MessageFormat.format("Erro ao associar o arquivo ao processo [ {1} ]", processFilesInLoteStatusDTO.getProcessInstanceId()));
        } finally {
            deleteFile(file);
        }
    }

    private void deleteFile(File file) {
        try {
            if (file != null) {
                FileUtils.forceDelete(file);
            }
        } catch (Exception e) {
            Logger.getLogger(BindFileToProcessJob.class.getName()).log(Level.SEVERE, "Erro ao remover o arquivo", e);
        }
    }

}
