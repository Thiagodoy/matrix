/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import static com.core.matrix.utils.Constants.*;

import com.core.matrix.dto.FileDetailDTO;
import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.io.BeanIO;
import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.ContractCompInformationService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import com.core.matrix.validator.Validator;
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
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Data;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
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
    private LogService logService;
    private ContractCompInformationService contractInformationService;

    private List<MeansurementFile> files;

    private List<Log> logs;

    public FileValidationTask() {

        synchronized (FileValidationTask.context) {
            this.taskService = FileValidationTask.context.getBean(TaskService.class);
            this.service = FileValidationTask.context.getBean(MeansurementFileService.class);
            this.detailService = FileValidationTask.context.getBean(MeansurementFileDetailService.class);
            this.logService = FileValidationTask.context.getBean(LogService.class);
            this.contractInformationService = FileValidationTask.context.getBean(ContractCompInformationService.class);
        }

    }

    public FileValidationTask(ApplicationContext context) {
        FileValidationTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

        try {
            delegateExecution = de;
            logs = new ArrayList<>();
            final List<String> attachmentIds = (List<String>) de.getVariable(LIST_ATTACHMENT_ID, Object.class);

            final String user = de.getVariable(USER_UPLOAD, String.class);
            files = this.service.findByProcessInstanceId(delegateExecution.getProcessInstanceId());

            this.checkProinfaOfContracts();

            attachmentIds.stream().forEach(attachmentId -> {

                InputStream stream = null;
                String fileName = null;
                try {

                    long start = System.currentTimeMillis();
                    synchronized (taskService) {
                        stream = taskService.getAttachmentContent(attachmentId);
                        stream = removeLinesEmpty(stream);
                        fileName = taskService.getAttachment(attachmentId).getName();
                    }
                    
                    loggerPerformance(start, "Carregando arquivo e removendo as linhas em branco");

                    start = System.currentTimeMillis();
                    BeanIO reader = new BeanIO();
                    Optional<FileParsedDTO> opt = reader.<FileParsedDTO>parse(stream);
                    
                    loggerPerformance(start, "Parseando o arquivo para a entidade");

                    if (opt.isPresent()) {

                        FileParsedDTO fileDto = opt.get();

                        start = System.currentTimeMillis();
                        //Filter only points thas is into process
                        List<FileDetailDTO> result = this.filter(fileDto.getDetails(), fileName);
                        fileDto.setDetails(result);
                        
                        loggerPerformance(start, "Filtrando os registros pertecente ao processo");

                        MeansurementFileType type = MeansurementFileType.valueOf(fileDto.getType());
                        
                        start = System.currentTimeMillis();    
                        this.checkDuplicity(result, type, fileName);
                        loggerPerformance(start, "Checando a duplicidade");
                        
                        start = System.currentTimeMillis();    
                        this.validate(result, type, fileName);
                        loggerPerformance(start, "Validação");
                        
                        if (this.logs.isEmpty()) {
                            start = System.currentTimeMillis();    
                            persistFile(fileDto, attachmentId, user, files);
                            loggerPerformance(start, "Persistência");
                        }

                    } else {
                        throw new Exception("Não foi possivel aplicar o parse no arquivo!");
                    }

                } catch (Exception e) {
                    this.generateLog(de, e, "Erro ao processar o arquivo : " + fileName);
                }

            });

            //Verify if one file not associate with a attachment and not is a consumer unit , so write a log.
            if (this.logs.isEmpty()) {
                files.stream()
                        .filter(f -> !this.contractInformationService.isConsumerUnit(f.getWbcContract()))
                        .filter(f -> f.getFile() == null)
                        .forEach(f -> {
                            String message = MessageFormat.format("Não foi encontrado nenhuma correspondência do ponto de medição, dentro dos arquivos postados.\nInformação:\nContrato: {0}\nPonto de Medição: {1}\n", f.getWbcContract().toString().replace(".", ""), f.getMeansurementPoint());
                            this.generateLog(de, null, message);
                        });
            }

            if (!this.logs.isEmpty()) {

                Log log = new Log();
                log.setType(Log.LogType.LAYOUT_INVALID);
                log.setProcessInstanceId(delegateExecution.getProcessInstanceId());
                logs.add(log);

                this.logService.save(logs);
                delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
            } else {

                //Change status of file that is consumer unit to success      
                files.stream()
                        .filter(f -> this.contractInformationService.isConsumerUnit(f.getWbcContract()))
                        .forEach(f -> {
                            f.setStatus(MeansurementFileStatus.SUCCESS);
                            service.saveFile(f);
                        });

                delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_VALID);
            }
        } catch (Exception e) {
            delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
            this.generateLog(de, e, "Erro ao processar o arquivo");
        }

    }

    private void checkDuplicity(List<FileDetailDTO> detail, MeansurementFileType type, String fileName) {

        Map<String, Long> checkDuplicity = detail
                .parallelStream()
                .collect(Collectors.groupingBy(FileDetailDTO::generateKey, Collectors.counting()));

        List<String> errors = Collections.synchronizedList(new ArrayList<String>());

        checkDuplicity
                .entrySet()
                .parallelStream()
                .forEach(keyValue -> {
                    if (keyValue.getValue().compareTo(1L) > 0) {
                        String[] values = keyValue.getKey().split("-");
                        String message = MessageFormat.format("Registro duplicado [ ponto -> {0} data -> {1} hora -> {2} ]", values[2],values[0],values[1]);
                        errors.add(message);
                    }
                });

        if (!errors.isEmpty()) {
            this.generateLog(delegateExecution, null, MessageFormat.format("Arquivo [ {0} ] possui registros em duplicidade", fileName));
            errors.forEach(m -> {
                this.generateLog(delegateExecution, null, m);
            });
        }

    }

    private void checkProinfaOfContracts() throws Exception {

        List<Exception> execExceptions = new ArrayList<>();

        files.forEach(file -> {

            try {
                ContractCompInformation information = contractInformationService
                        .findByWbcContractAndMeansurementPoint(file.getWbcContract(), file.getMeansurementPoint())
                        .orElseThrow(() -> new Exception("[Matrix] -> Não foi possivel encontrar as informações complementares do contrato!"));

                if (!Optional.ofNullable(information.getProinfas()).isPresent() || information.getProinfas().isEmpty()) {
                    throw new Exception("O contrato [" + file.getWbcContract() + "] não possui nenhum cadastro de proinfa!");
                }

                information.getProinfas()
                        .stream()
                        .filter(infa -> file.getMonth().equals(infa.getMonth()) && file.getYear().equals(infa.getYear()))
                        .findFirst()
                        .orElseThrow(() -> new Exception("Não foi encontrado nenhum proinfa cadastrada para esse contrato [" + file.getWbcContract() + "]!\n Mês/Ano referência: " + file.getMonth() + "/" + file.getYear()));

            } catch (Exception ex) {
                execExceptions.add(ex);
            }
        });

        if (!execExceptions.isEmpty()) {
            execExceptions.forEach(ex -> {
                this.generateLog(delegateExecution, ex, ex.getMessage());
            });

            this.logService.save(logs);
            throw new Exception("Processo encerrado devido a ausência de informações!");
        }

    }

    private void validate(List<FileDetailDTO> detail, MeansurementFileType type, String fileName) {

        List<String> errors = Collections.synchronizedList(new ArrayList<String>());

        detail.parallelStream().forEach(d -> {

            List<String> result = new Validator().validate(d, type);

            if (!result.isEmpty()) {
                errors.addAll(result);
            }
        });

        if (type.equals(MeansurementFileType.LAYOUT_C) || type.equals(MeansurementFileType.LAYOUT_C_1)) {

            boolean has_L = Validator.validateContentIfContains(detail);
            if (!has_L) {
                errors.add(MessageFormat.format("Os registros do layout C ou C.1, não apresenta em sua composição a palavra [ (L) ] nos pontos de medições. Arquivo [ {0} ]", fileName));
            }

            detail.removeIf(d -> Optional.ofNullable(d.getOrigem()).isPresent() && d.getOrigem().equals("DADOS FALTANTES"));

            if (detail.isEmpty()) {
                errors.add(MessageFormat.format("O arquivo [ {0} ] não apresenta registros que possam ser processados de acordo com as regras estabelecidas para o layout [ {1} ].\n Favor analisar o arquivo.", fileName, type.toString()));
            }

        }

        if (!errors.isEmpty()) {
            this.writeFileError(fileName, errors, delegateExecution);
        }

    }

    private List<FileDetailDTO> filter(List<FileDetailDTO> detail, String fileName) {

        List<FileDetailDTO> result = new ArrayList();

        long count = 5;

        for (FileDetailDTO fileDetailDTO : detail) {
            fileDetailDTO.setLine(count);
            fileDetailDTO.setFileName(fileName);
            ++count;
        }

        this.files.stream().forEach(f -> {

            List<FileDetailDTO> r = detail
                    .parallelStream()
                    .filter(d -> d.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(f.getMeansurementPoint()))
                    .collect(Collectors.toList());

            result.addAll(r);

        });

        return result;

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

    private void writeFileError(String fileName, List<String> errors, DelegateExecution de) {

        FileWriter writer = null;
        File file = null;

        try {

            file = File.createTempFile("erros", ".txt");
            writer = new FileWriter(file);

            errors.stream().distinct().forEach(msg -> {
                Log log = new Log();
                log.setMessage(msg);
                log.setActivitiName(de.getCurrentActivityName());
                log.setProcessInstanceId(de.getProcessInstanceId());
                logs.add(log);
            });

            String content = errors.stream().distinct().collect(Collectors.joining("\n"));
            writer.write(content);
            writer.flush();
            writer.close();

            taskService.createAttachment("text/plain",
                    null,
                    de.getProcessInstanceId(),
                    fileName + "_erros.txt",
                    "attachmentDescription",
                    new FileInputStream(file));

        } catch (IOException ex) {
            Logger.getLogger(FileValidationTask.class.getName()).log(Level.SEVERE, "[ writeFile ]", ex);
            this.generateLog(de, ex, "Não foi possivel gravar o arquivo de erro.");

        } finally {

            if (file != null) {
                try {
                    FileUtils.forceDelete(file);
                } catch (IOException ex) {
                }
            }

        }

    }

    public void persistFile(FileParsedDTO fileParsedDTO, String attachmentId, String userId, List<MeansurementFile> files) {

        try {

            MeansurementFileType type = MeansurementFileType.valueOf(fileParsedDTO.getType());

            final List<MeansurementFileDetail> details = this.mountDetail(fileParsedDTO.getDetails(), type);

            //set a user for files and type
            files.forEach(file -> {
                file.setUser(userId);
            });

            //List all point that are into the file    
            List<String> meansuremPoint = details
                    .parallelStream()
                    .map(d -> d.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim())
                    .distinct()
                    .collect(Collectors.toList());

            //Verify if point match some files uploaded. And set the attachment id on file             
            meansuremPoint.forEach(point -> {
                Optional<MeansurementFile> opt = files.stream().filter(file -> file.getMeansurementPoint().equals(point)).findFirst();

                if (opt.isPresent()) {

                    MeansurementFile file = opt.get();
                    file.setFile(attachmentId);
                    file.setUser(userId);
                    file.setType(type);

                    List<MeansurementFileDetail> fileDetaisl = details
                            .stream()
                            .filter(d -> d.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(point))
                            .collect(Collectors.toList());

                    fileDetaisl.forEach(d -> {
                        d.setIdMeansurementFile(file.getId());
                    });

                    opt.get().setStatus(MeansurementFileStatus.SUCCESS);
                    service.saveFile(opt.get());
                    detailService.saveAllBatch(fileDetaisl);
                }

            });

        } catch (Exception e) {
            Logger.getLogger(FileValidationTask.class.getName()).log(Level.SEVERE, "[ mountFile ]", e);
            String fileName = taskService.getAttachment(attachmentId).getName();
            this.generateLog(delegateExecution, e, "Erro ao montar os detalhes do arquivo : " + fileName);
        }

    }

    private List<MeansurementFileDetail> mountDetail(List<FileDetailDTO> details, MeansurementFileType type) {
        return details
                .parallelStream()
                .map(d -> new MeansurementFileDetail(d, type))
                .filter(mpd -> !mpd.getMeansurementPoint().contains("(B)"))
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

    
    private void loggerPerformance(long start, String fase){
        Logger.getLogger(FileValidationTask.class.getName()).log(Level.INFO,MessageFormat.format("[loggerPerformance] -> etapa: {0} tempo : {1} min", fase, (System.currentTimeMillis() - start)/60000D ));
    }
    
    
}
