/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import static com.core.matrix.utils.Constants.*;

import com.core.matrix.dto.FileDetailDTO;
import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.io.BeanIoReader;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Data;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Attachment;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
@Data

public class FileValidationTask implements JavaDelegate {

    private static ApplicationContext context;

    private TaskService taskService;
    private MeansurementFileService service;
    private MeansurementFileDetailService detailService;
    private DelegateExecution delegateExecution;

    public FileValidationTask() {

        synchronized (FileValidationTask.context) {
            this.taskService = FileValidationTask.context.getBean(TaskService.class);
            this.service = FileValidationTask.context.getBean(MeansurementFileService.class);
            this.detailService = FileValidationTask.context.getBean(MeansurementFileDetailService.class);
        }

    }

    public FileValidationTask(ApplicationContext context) {
        FileValidationTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

        delegateExecution = de;

        final String attachmentId = de.getVariable(ATTACHMENT_ID, String.class);
        final String userId = de.getVariable(CREATED_BY, String.class);

        try {

            InputStream stream = taskService.getAttachmentContent(attachmentId);
            String fileName = taskService.getAttachment(attachmentId).getName();

            delegateExecution.setVariable(VAR_FILE_NAME, fileName);

            BeanIoReader reader = new BeanIoReader();
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(stream);

            if (!reader.getErrors().isEmpty()) {
                writeFile(reader.getErrors(), de);
                delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Layout/registros estão inválidos!");
                delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
            } else if (fileParsed.isPresent()) {
                mountFile(fileParsed.get(), attachmentId, userId);
            }

        } catch (Exception e) {
            Logger.getLogger(MeansurementFileService.class.getName()).log(Level.SEVERE, "[execute]", e);
            delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, new String[]{e.getMessage()});
            de.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
        }
    }

    private void writeFile(List<String> errors, DelegateExecution de) {

        FileWriter writer = null;
        File file = null;

        try {

            file = File.createTempFile("erros", ".txt");
            writer = new FileWriter(file);

            String content = errors.stream().collect(Collectors.joining("\n"));
            writer.write(content);
            writer.flush();
            writer.close();

            Attachment attachment = taskService.createAttachment("text/plain", null, de.getProcessInstanceId(), "Erros.txt", "attachmentDescription", new FileInputStream(file));
            delegateExecution.setVariable(ATTACHMENT_ERROR_ID, attachment.getId());
        } catch (IOException ex) {
            Logger.getLogger(FileValidationTask.class.getName()).log(Level.SEVERE, "[ writeFile ]", ex);
        } finally {

            if (file != null) {
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException ex) {
                }
            }

        }

    }

    private void mountFile(FileParsedDTO fileParsedDTO, String idFile, String userId) throws Exception {

        final String period = fileParsedDTO.informations.get(2).getValue();

        MeansurementFile meansurementFile = new MeansurementFile();
        meansurementFile.setFile(idFile);
        LocalDate date = extractMonthAndYear(period);
        meansurementFile.setMonth((long) date.getMonthValue());
        meansurementFile.setYear((long) date.getYear());

        meansurementFile.setStatus(MeansurementFileStatus.SUCCESS);
        meansurementFile.setUser(userId);

        meansurementFile.setType(MeansurementFileType.valueOf(fileParsedDTO.getType()));

        meansurementFile = service.saveFile(meansurementFile);
        final Long id = meansurementFile.getId();

        List<MeansurementFileDetail> details =  null;
        try {
            
            details = this.mountDetail(fileParsedDTO.getDetails(), meansurementFile.getType());
            details.parallelStream().forEach(d -> {
                d.setIdMeansurementFile(id);
            });
            
        } catch (Exception e) {
            Logger.getLogger(FileValidationTask.class.getName()).log(Level.SEVERE, "[ mountDetail ]", e);
            meansurementFile.setStatus(MeansurementFileStatus.FILE_ERROR);
            service.saveFile(meansurementFile);
            throw  e;
        }

        detailService.save(details);
        delegateExecution.setVariable(TYPE_LAYOUT_FILE, meansurementFile.getType().toString());
        delegateExecution.setVariable(FILE_MEANSUREMENT_ID, meansurementFile.getId());
        delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_VALID);

    }

    private List<MeansurementFileDetail> mountDetail(List<FileDetailDTO> details, MeansurementFileType type) {
        return details
                .parallelStream()
                .map(d -> new MeansurementFileDetail(d, type))
                .collect(Collectors.toList());
    }

    private LocalDate extractMonthAndYear(String value) throws Exception {

        Pattern p = Pattern.compile("([0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2})|([0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2})", Pattern.MULTILINE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Matcher m = p.matcher(value);

        if (m.find()) {

            String stringDate = m.group(0);

            if (stringDate.length() == 7) {
                stringDate = "01/" + stringDate;
            }
            LocalDate localDate = LocalDate.parse(stringDate, formatter);
            return localDate;
        } else {
            throw new Exception("Não foi possivel extrair o periodo");
        }

    }

}
