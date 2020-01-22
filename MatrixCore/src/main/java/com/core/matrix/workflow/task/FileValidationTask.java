/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.FileDetailDTO;
import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.io.BeanIoReader;
import com.core.matrix.io.Stream;
import com.core.matrix.workflow.model.MeansurementFile;
import com.core.matrix.workflow.model.MeansurementFileDetail;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class FileValidationTask implements JavaDelegate {

    @Autowired
    private TaskService taskService;

    private static final String ATTACHMENT_ID = "";
    private static final String USER_ID = "";

    @Override
    public void execute(DelegateExecution de) throws Exception {

        try {

            final String attachmentId = de.getVariable(ATTACHMENT_ID, String.class);
            final String userId = de.getVariable(USER_ID, String.class);

            InputStream stream = taskService.getAttachmentContent(attachmentId);
            BeanIoReader reader = new BeanIoReader();
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(stream, Stream.FILE_LAYOUT_PARSER);

            if (fileParsed.isPresent()) {
                MeansurementFile meansurementFile = mountFile(fileParsed.get(), attachmentId, userId);
            } else {
                // TODO LAYOU INVALIDO    
            }

        } catch (Exception e) {

        }
    }

    private MeansurementFile mountFile(FileParsedDTO fileParsedDTO, String idFile, String userId) throws Exception {

        final String reportType = fileParsedDTO.informations.get(0).getValue();
        final String agentType = fileParsedDTO.informations.get(1).getValue();
        final String period = fileParsedDTO.informations.get(2).getValue();

        MeansurementFile meansurementFile = new MeansurementFile();
        meansurementFile.setFile(idFile);

        List<Integer> date = extractMonthAndYear(period);

        meansurementFile.setMonth(date.get(0).longValue());
        meansurementFile.setYear(date.get(1).longValue());

        // TODO Definir como será tratado esse 
        meansurementFile.setStatus("");
        meansurementFile.setUser(userId);

        return meansurementFile;

    }

    private List<MeansurementFileDetail> mountDetail(List<FileDetailDTO> details) {

        details.parallelStream().map(d -> new MeansurementFileDetail(d));

        return null;

    }

    private List<Integer> extractMonthAndYear(String value) throws Exception {

        Pattern p = Pattern.compile("[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}", Pattern.MULTILINE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

        Matcher m = p.matcher(value);

        if (m.find()) {
            LocalDate localDate = LocalDate.parse(m.group(0), formatter);

            List<Integer> data = new LinkedList<>();
            data.add(localDate.getMonthValue());
            data.add(localDate.getYear());
            return data;
        } else {
            //TODO LANCAR ERRO QUE NAO FOIPOSSIVEL EXTRAIR O PERIDO DO ARQUIVO
            throw new Exception("Não foi possivel extrair o periodo");
        }

    }

}
