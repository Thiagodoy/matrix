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
import com.core.matrix.model.ContractMtxStatus;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.model.MeansurementPointMtx;
import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.service.ContractMtxStatusService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.service.MeansurementPointMtxService;
import com.core.matrix.service.MeansurementPointStatusService;
import com.core.matrix.utils.ContractStatus;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.MeansurementFileType;
import com.core.matrix.utils.PointStatus;
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
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.Data;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
@Data
public class FileValidationTask extends Task {

    private static ApplicationContext context;

    private TaskService taskService;
    private MeansurementFileService service;    
    private DelegateExecution delegateExecution;
    private LogService logService;
    private MeansurementPointMtxService meansurementPointMtxService;
    private List<MeansurementFile> files;
    private MeansurementPointStatusService pointStatusService;
    private ContractMtxStatusService contractMtxStatusService;

    private List<Log> logs;

    public FileValidationTask() {

        synchronized (FileValidationTask.context) {
            this.taskService = FileValidationTask.context.getBean(TaskService.class);
            this.service = FileValidationTask.context.getBean(MeansurementFileService.class);            
            this.logService = FileValidationTask.context.getBean(LogService.class);
            this.meansurementPointMtxService = FileValidationTask.context.getBean(MeansurementPointMtxService.class);
            this.pointStatusService = FileValidationTask.context.getBean(MeansurementPointStatusService.class);
            this.contractMtxStatusService = FileValidationTask.context.getBean(ContractMtxStatusService.class);
        }

    }

    public FileValidationTask(ApplicationContext context) {
        FileValidationTask.context = context;
    }

    public static void setContext(ApplicationContext context) {
        FileValidationTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

        try {
            delegateExecution = de;

            this.loadVariables(delegateExecution);

            logs = new ArrayList<>();
            final List<String> attachmentIds = (List<String>) de.getVariable(LIST_ATTACHMENT_ID, Object.class);

            final String user = de.getVariable(USER_UPLOAD, String.class);
            files = new CopyOnWriteArrayList<>(this.service.findByProcessInstanceId(delegateExecution.getProcessInstanceId()));

            if (this.isOnlyContractFlatOrUnitConsumer()) {
                this.alterStatusFiles(files);
                this.setVariable(CONTROLE, RESPONSE_LAYOUT_VALID);
                this.writeVariables(delegateExecution);
                return;
            }

            this.checkProinfaOfPoints();

            attachmentIds.stream().forEach(attachmentId -> {

                InputStream stream = null;
                String fileName = null;
                try {

                    synchronized (taskService) {
                        stream = taskService.getAttachmentContent(attachmentId);
                        stream = removeLinesEmpty(stream);
                        fileName = taskService.getAttachment(attachmentId).getName();
                    }

                    BeanIO reader = new BeanIO();
                    Optional<FileParsedDTO> opt = reader.<FileParsedDTO>parse(stream);

                    if (opt.isPresent()) {

                        FileParsedDTO fileDto = opt.get();
                        MeansurementFileType type = MeansurementFileType.valueOf(fileDto.getType());

                        List<FileDetailDTO> result = this.filter(fileDto.getDetails(), fileName);
                        fileDto.setDetails(result);

                        this.checkHoursMissing(result);

                        this.checkDuplicity(result, type, fileName);

                        this.validate(result, type, fileName);

                        if (this.logs.isEmpty()) {
                            persistFile(fileDto, attachmentId, user, files);
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
                        .filter(f -> !this.isUnitConsumer(f.getWbcContract()))
                        .filter(f -> !this.isFlat(f.getWbcContract()))
                        .filter(f -> f.getFile() == null)
                        .filter(f -> !f.getStatus().equals(MeansurementFileStatus.FILE_MISSING_ALL_HOURS))
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
                this.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
            } else {
                this.alterStatusFiles(files);
                this.alterStatusPoint();
                this.setVariable(CONTROLE, RESPONSE_LAYOUT_VALID);
            }

        } catch (Exception e) {
            this.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Erro ao finalizar o processo", e);
        } finally {
            this.writeVariables(delegateExecution);
        }

    }

    private void alterStatusPoint() {

        try {
            this.getPointsRead().forEach(point -> {

                try {
                    Optional<MeansurementPointStatus> opt = this.pointStatusService.getPoint(point);

                    if (opt.isPresent()) {
                        MeansurementPointStatus pointStatus = opt.get();
                        pointStatus.setStatus(PointStatus.PENDING);
                        MeansurementFile file = this.getFileByPoint(point);
                        pointStatus.setCompany(file.getNickname());
                        pointStatus.forceUpdate();
                    }

                } catch (Exception e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Não foi possivel atualizar o ponto -> " + point, e);
                }

            });
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Não foi possivel alterar status dos pontos referente ao processo -> " + delegateExecution.getProcessInstanceId() , e);
        }

    }

    private void alterStatusFiles(List<MeansurementFile> files) {

        MeansurementFileType laFileType = files
                .stream()
                .map(MeansurementFile::getType).filter(l -> Objects.nonNull(l))
                .findFirst()
                .orElse(MeansurementFileType.LAYOUT_C);

        files.stream().filter(f -> f.getStatus().equals(MeansurementFileStatus.FILE_MISSING_ALL_HOURS)).forEach(c -> {
            c.setType(laFileType);
        });

        files.stream()
                .forEach(f -> {

                    if ((this.isFlat(f.getWbcContract()) || this.isUnitConsumer(f.getWbcContract())) && !f.getStatus().equals(MeansurementFileStatus.FILE_MISSING_ALL_HOURS)) {
                        f.setStatus(MeansurementFileStatus.SUCCESS);
                    }
                    service.saveFile(f);
                });

        List<MeansurementFile> fTemps = new ArrayList<>();
        files.forEach(ff -> {
            MeansurementFile fTemp = new MeansurementFile();
            fTemp.setMeansurementPoint(ff.getMeansurementPoint());
            fTemp.setWbcContract(ff.getWbcContract());
            fTemp.setMonth(ff.getMonth());
            fTemp.setYear(ff.getYear());
            fTemp.setCompanyName(ff.getCompanyName());
            fTemp.setFile(ff.getFile());
            fTemp.setNickname(ff.getNickname());
            fTemp.setProcessInstanceId(ff.getProcessInstanceId());
            fTemp.setStatus(ff.getStatus());
            fTemp.setId(ff.getId());
            fTemp.setType(ff.getType());
            fTemps.add(fTemp);
        });

        this.setFiles(delegateExecution, fTemps);

    }

    private void checkDuplicity(List<FileDetailDTO> detail, MeansurementFileType type, String fileName) {

        Map<String, Long> checkDuplicity = detail
                .parallelStream()
                .filter(mpd -> !mpd.getMeansurementPoint().contains("(B)"))
                .collect(Collectors.groupingBy(FileDetailDTO::generateKey, Collectors.counting()));

        List<String> errors = Collections.synchronizedList(new ArrayList<String>());

        checkDuplicity
                .entrySet()
                .parallelStream()
                .forEach(keyValue -> {
                    if (keyValue.getValue().compareTo(1L) > 0) {
                        String[] values = keyValue.getKey().split("-");
                        String message = MessageFormat.format("Registro duplicado [ ponto -> {0} data -> {1} hora -> {2} ]", values[2], values[0], values[1]);
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

    private void checkProinfaOfPoints() throws Exception {

        List<Exception> execExceptions = new ArrayList<>();

        files
                .stream()
                .filter(f -> Optional.ofNullable(f.getMeansurementPoint()).isPresent())
                .forEach(file -> {

                    String point = null;
                    try {

                        MeansurementPointMtx pointMtx = this.meansurementPointMtxService.getByPoint(file.getMeansurementPoint());
                        point = pointMtx.getPoint();
                        pointMtx.checkProInfa();
                    } catch (Exception ex) {
                        Exception e = new Exception("Não foi encontrado nenhum proinfa cadastrada para o ponto [" + point + "]!\n Mês/Ano referência: " + file.getMonth() + "/" + file.getYear());
                        execExceptions.add(e);
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

    private void checkHoursMissing(List<FileDetailDTO> detail) {

        // long daysOnMonth = YearMonth.of(file.getYear().intValue(), Month.of(file.getMonth().intValue())).lengthOfMonth();
        files.stream()
                .filter(f -> !f.getStatus().equals(MeansurementFileStatus.FILE_MISSING_ALL_HOURS))
                .forEach(f -> {

                    long size = detail.parallelStream()
                            .filter(d -> Optional.ofNullable(d.getOrigem()).isPresent() && d.getOrigem().equals("DADOS FALTANTES"))
                            .parallel()
                            .filter(d -> d.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(f.getMeansurementPoint()))
                            .count();

                    if (size >= 1) {
                        detail.removeIf(d -> d.getMeansurementPointFormated().equals(f.getMeansurementPoint())
                                && Optional.ofNullable(d.getOrigem()).isPresent()
                                && d.getOrigem().equals("DADOS FALTANTES"));
                        f.setStatus(MeansurementFileStatus.FILE_MISSING_ALL_HOURS);
                    }
                });
    }

    private void validate(List<FileDetailDTO> detail, MeansurementFileType type, String fileName) {


        List<String> errors = Collections.synchronizedList(new ArrayList<String>());


        if (type.equals(MeansurementFileType.LAYOUT_C) || type.equals(MeansurementFileType.LAYOUT_C_1)) {

            boolean has_L = Validator.hasL(detail);
            if (!has_L && !detail.isEmpty()) {
                errors.add(MessageFormat.format("Os layouts C ou C.1, é obrigatorio que os pontos no arquivo tenham essa composição: XXXXXXXXXXXXX (L) . Arquivo [ {0} ]", fileName));
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

        this.files.stream()
                .filter(f -> !f.getStatus().equals(MeansurementFileStatus.FILE_CHECKED))
                .forEach(f -> {

                    List<FileDetailDTO> r = detail
                            .parallelStream()
                            .filter(d -> d.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(f.getMeansurementPoint()))
                            .collect(Collectors.toList());

                    if (!r.isEmpty()) {
                        f.setStatus(MeansurementFileStatus.FILE_CHECKED);
                        result.addAll(r);
                    }
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
            final List<MeansurementFileDetail> details = new CopyOnWriteArrayList<>(this.mountDetail(fileParsedDTO.getDetails(), type));

            final Map<String, List<MeansurementFileDetail>> variable = new HashMap<>();
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
            meansuremPoint.parallelStream().forEach(point -> {
                Optional<MeansurementFile> opt = files.stream()
                        //.filter(f -> f.getStatus().equals(MeansurementFileStatus.FILE_CHECKED))
                        .filter(file -> point.equals(file.getMeansurementPoint())).findFirst();

                if (opt.isPresent()) {
                    MeansurementFile file = opt.get();

                    Optional<ContractMtxStatus> optContract = contractMtxStatusService.getContract(file.getWbcContract());

                    if (optContract.isPresent()) {
                        ContractMtxStatus contractMtxStatus = optContract.get();
                        contractMtxStatus.setStatus(ContractStatus.PENDING);
                        contractMtxStatus.forceUpdate();
                    }

                    file.setFile(attachmentId);
                    file.setUser(userId);
                    file.setType(type);

                    List<MeansurementFileDetail> fileDetaisl = details
                            .stream()
                            .filter(d -> d.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(point))
                            .collect(Collectors.toList());

                    fileDetaisl.parallelStream().forEach(d -> {
                        d.setIdMeansurementFile(file.getId());
                    });

                    if (!file.getStatus().equals(MeansurementFileStatus.FILE_MISSING_ALL_HOURS)) {
                        file.setStatus(MeansurementFileStatus.SUCCESS);
                    }

                    this.addDetails(point, fileDetaisl);

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

}
