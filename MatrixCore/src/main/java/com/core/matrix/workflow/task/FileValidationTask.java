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
import com.core.matrix.utils.MeansurementFileDetailStatus;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.MessageFormat;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private List<String> meansurementPoints;
    private List<MeansurementFile> files;
    private List<String> messageErrors;

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
        final List<String> attachmentIds = (List<String>) de.getVariable(LIST_ATTACHMENT_ID, Object.class);
        final String user = de.getVariable(USER_UPLOAD, String.class);
        files = this.service.findByProcessInstanceId(delegateExecution.getProcessInstanceId());

        attachmentIds.parallelStream().forEach(attachmentId -> {

            try {

                InputStream stream = null;
                String fileName = null;

                synchronized (taskService) {
                    stream = taskService.getAttachmentContent(attachmentId);
                    stream = removeLinesEmpty(stream);
                    fileName = taskService.getAttachment(attachmentId).getName();
                }

                BeanIoReader reader = new BeanIoReader();
                Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(stream);

                if (!reader.getErrors().isEmpty()) {
                    writeFile(fileName, reader.getErrors(), de);
                } else if (fileParsed.isPresent()) {
                    mountFile(fileParsed.get(), attachmentId, user, files);
                }

            } catch (Exception e) {
                Logger.getLogger(FileValidationTask.class.getName()).log(Level.SEVERE, "[ forEach ]", e);
            }

        });

        files.stream().filter(f -> f.getFile() == null).forEach(f -> {
            String message = MessageFormat.format("Não foi encontrado nenhuma corespondência do ponto de medição, dentro dos arquivos postados.\nInformação:\nContrato: {0}\nPonto de Medição: {1}\n", f.getWbcContract(), f.getMeansurementPoint());
            this.messageErrors.add(message);
        });

        if (!this.messageErrors.isEmpty()) {
            delegateExecution.setVariable(RESPONSE_RESULT, messageErrors);
            delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
        } else {
            delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_VALID);
        }

    }

    private void writeFile(String fileName, List<String> errors, DelegateExecution de) {

        FileWriter writer = null;
        File file = null;

        try {

            file = File.createTempFile("erros", ".txt");
            writer = new FileWriter(file);

            String content = errors.stream().distinct().collect(Collectors.joining("\n"));
            writer.write(content);
            writer.flush();
            writer.close();

            taskService.createAttachment("text/plain",
                    null,
                    de.getProcessInstanceId(),
                    fileName + "erros.txt",
                    "attachmentDescription",
                    new FileInputStream(file));

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

    public void mountFile(FileParsedDTO fileParsedDTO, String attachmentId, String userId, List<MeansurementFile> files) {

        try {

            MeansurementFileType type = MeansurementFileType.valueOf(fileParsedDTO.getType());
            final List<MeansurementFileDetail> details = this.mountDetail(fileParsedDTO.getDetails(), type);

            //set a user for files and type
            files.forEach(file -> {
                file.setUser(userId);
                file.setType(type);
            });
            //List all point that are into the file    
            List<String> meansuremPoint = details.parallelStream().map(d -> d.getMeansurementPoint()).distinct().collect(Collectors.toList());

            //Verify if point match some files made. And set the attachment id on file             
            meansuremPoint.forEach(point -> {
                Optional<MeansurementFile> opt = files.stream().filter(file -> file.getMeansurementPoint().equals(point)).findFirst();
                if (opt.isPresent()) {

                    MeansurementFile file = opt.get();
                    file.setFile(attachmentId);
                    file.setUser(userId);
                    file.setType(type);

                    List<MeansurementFileDetail> fileDetaisl = details
                            .stream()
                            .filter(d -> d.getMeansurementPoint().equals(point))
                            .collect(Collectors.toList());

                    fileDetaisl.forEach(d -> {
                        d.setIdMeansurementFile(file.getId());                        
                    });

                    opt.get().setStatus(MeansurementFileStatus.SUCCESS);
                    service.saveFile(opt.get());
                    detailService.save(fileDetaisl);
                }
            });

        } catch (Exception e) {
            Logger.getLogger(FileValidationTask.class.getName()).log(Level.SEVERE, "[ mountFile ]", e);
            throw e;
        }

    }

    private List<MeansurementFileDetail> mountDetail(List<FileDetailDTO> details, MeansurementFileType type) {
        return details
                .parallelStream()
                .map(d -> new MeansurementFileDetail(d, type))
                .collect(Collectors.toList());
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
