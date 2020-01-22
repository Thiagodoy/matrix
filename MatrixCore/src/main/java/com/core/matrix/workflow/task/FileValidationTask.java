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
import com.core.matrix.io.Stream;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.MeansurementFileStatus;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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

    @Autowired
    private MeansurementFileService service;

    

    @Override
    public void execute(DelegateExecution de) throws Exception {

        final String attachmentId = de.getVariable(ATTACHMENT_ID, String.class);
        final String userId = de.getVariable(USER_ID, String.class);

        try {

            InputStream stream = taskService.getAttachmentContent(attachmentId);
            BeanIoReader reader = new BeanIoReader();
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(stream, Stream.FILE_LAYOUT_PARSER);

            if (fileParsed.isPresent()) {
                MeansurementFile meansurementFile = mountFile(fileParsed.get(), attachmentId, userId);
                service.saveFile(meansurementFile);
                de.setVariable(CONTROLE, RESPONSE_LAYOUT_VALID);
            }

        } catch (Exception e) {
            Logger.getLogger(MeansurementFileService.class.getName()).log(Level.SEVERE, "[execute]", e);
            Log log = new Log();
            log.setAttachment(attachmentId);
            log.setMessage(e.getMessage());
            de.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
        }
    }

    private MeansurementFile mountFile(FileParsedDTO fileParsedDTO, String idFile, String userId) throws Exception {

        //final String reportType = fileParsedDTO.informations.get(0).getValue();
        //final String agentType = fileParsedDTO.informations.get(1).getValue();
        final String period = fileParsedDTO.informations.get(2).getValue();

        MeansurementFile meansurementFile = new MeansurementFile();
        meansurementFile.setFile(idFile);

        List<Integer> date = extractMonthAndYear(period);

        meansurementFile.setMonth(date.get(0).longValue());
        meansurementFile.setYear(date.get(1).longValue());

        // TODO Definir como será tratado esse 
        meansurementFile.setStatus(MeansurementFileStatus.SUCCESS);
        meansurementFile.setUser(userId);

        meansurementFile.setDetails(this.mountDetail(fileParsedDTO.getDetails()));

        return meansurementFile;

    }

    private List<MeansurementFileDetail> mountDetail(List<FileDetailDTO> details) {
        return details
                .parallelStream()
                .map(d -> new MeansurementFileDetail(d))
                .collect(Collectors.toList());
    }

    private List<Integer> extractMonthAndYear(String value) throws Exception {

        Pattern p = Pattern.compile("[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}", Pattern.MULTILINE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
