/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.jobs;

import com.core.matrix.dto.FileDetailDTO;
import com.core.matrix.dto.FileLoteErrorDTO;
import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.dto.ProcessFilesInLoteStatusDTO;
import com.core.matrix.io.BeanIO;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Data;
import org.activiti.engine.TaskService;

/**
 *
 * @author thiag
 */
@Data
public class ParseFileJob implements Runnable {

    private FileLoteErrorDTO loteErrorDTO = new FileLoteErrorDTO();
    private InputStream stream = null;
    private String fileName = null;
    private String attachmentId;
    private TaskService taskService;
    private Set<ProcessFilesInLoteStatusDTO> loteStatusDTOs;
    private List<FileLoteErrorDTO> fileLoteErrorDTOs;

    public void run() {

        try {           

            synchronized (taskService) {
                stream = taskService.getAttachmentContent(attachmentId);
                stream = removeLinesEmpty(stream);
                fileName = taskService.getAttachment(attachmentId).getName();
            }

            loteErrorDTO.setFileName(fileName);

            BeanIO reader = new BeanIO();
            Optional<FileParsedDTO> opt = reader.<FileParsedDTO>parse(stream);

            if (opt.isPresent()) {
                Map<String, Long> pointsMap = opt.get().getDetails().stream().collect(Collectors.groupingBy(FileDetailDTO::getMeansurementPoint, Collectors.counting()));
                FileParsedDTO fileParsedDTO = opt.get();

                Set<String> keysSet = pointsMap.keySet();

                loteStatusDTOs.stream().forEach(l -> {

                    l.getPoints().stream().forEach(point -> {

                        boolean contains = keysSet.stream().anyMatch(key -> key.contains(point));

                        if (contains) {

                            List<FileDetailDTO> details = fileParsedDTO
                                    .getDetails()
                                    .stream()
                                    .parallel()
                                    .filter(detail -> detail
                                        .getMeansurementPoint()
                                        .contains(point))
                                    .collect(Collectors.toList());
                            
                            synchronized(l){
                                l.pointChecked(point, fileParsedDTO.getInformations(), fileParsedDTO.getHeader(), details, fileParsedDTO.getType());
                            }
                            
                        }

                    });

                });



            } else if (!reader.getErrors().isEmpty()) {
                loteErrorDTO.setErrors(reader.getErrors());
                fileLoteErrorDTOs.add(loteErrorDTO);
            } else {
                throw new Exception("Erro ao realizar o parse do arquivo.");
            }

        } catch (Exception e) {
            Logger.getLogger(ParseFileJob.class.getName()).log(Level.SEVERE, "[ run ] file name -> " + this.fileName, e);
            loteErrorDTO.setError("Erro ao ler o Layout, favor encaminhar para análise da equipe de TI.");
            fileLoteErrorDTOs.add(loteErrorDTO);
        }

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
