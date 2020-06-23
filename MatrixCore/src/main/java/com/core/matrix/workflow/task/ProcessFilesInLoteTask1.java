/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.FileDetailDTO;
import com.core.matrix.dto.FileLoteErrorDTO;
import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.dto.ProcessFilesInLoteStatusDTO;
import com.core.matrix.dto.ResultInLoteStatusDTO;
import com.core.matrix.factory.EmailFactory;
import com.core.matrix.io.BeanIO;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.request.FileStatusLoteRequest;
import com.core.matrix.response.FileStatusBillingResponse;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.CONTROLE;
import static com.core.matrix.utils.Constants.LIST_ATTACHMENT_ID;
import static com.core.matrix.utils.Constants.RESPONSE_FILES_PARSED;
import static com.core.matrix.utils.Constants.RESPONSE_LAYOUT_VALID;
import static com.core.matrix.utils.Constants.RESPONSE_LIST_PROCESS_ANALIZED;
import com.core.matrix.utils.ThreadPoolEmail;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

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
public class ProcessFilesInLoteTask1 implements JavaDelegate {

    private TaskService taskService;

    private Set<ProcessFilesInLoteStatusDTO> status;
    private String processInstanceId;
    private List<FileLoteErrorDTO> fileLoteErrorDTOs;

    private EmailFactory emailFactory;
    private ThreadPoolEmail threadPoolEmail;
    private MeansurementFileService meansurementFileService;

    private LogService logService;

    private static ApplicationContext context;

    public ProcessFilesInLoteTask1() {

        synchronized (context) {
            this.emailFactory = this.context.getBean(EmailFactory.class);
            this.threadPoolEmail = this.context.getBean(ThreadPoolEmail.class);
            this.meansurementFileService = this.context.getBean(MeansurementFileService.class);
            this.logService = this.context.getBean(LogService.class);

        }

    }

    public ProcessFilesInLoteTask1(ApplicationContext context, Long threadPoolSize) {
        this.context = context;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {

            processInstanceId = execution.getProcessInstanceId();
            taskService = execution.getEngineServices().getTaskService();
            fileLoteErrorDTOs = Collections.synchronizedList(new ArrayList());
            status = this.getProcessPendingForUploadFile(execution);

            final List<FileParsedDTO> files = execution.getVariable(RESPONSE_FILES_PARSED, List.class);

            if (!status.isEmpty()) {
                this.restartProcess();
                filterInformations(files);
                bindFileToProcess();
            } else {
                Log log = new Log();
                log.setActivitiName(execution.getCurrentActivityName());
                log.setMessage("Não foi encontrato nenhum processo para associar os arquivos");
                log.setProcessInstanceId(processInstanceId);
                log.setType(Log.LogType.ERROR);
                logService.save(log);
            }

        } catch (Exception e) {
            Logger.getLogger(ProcessFilesInLoteTask1.class.getName()).log(Level.SEVERE, "[execute]", e);
            Log log = new Log();
            log.setActivitiName(execution.getCurrentActivityName());
            log.setMessage("Erro no processo, favor encaminhar para a TI.");
            log.setMessageErrorApplication(e.getMessage());
            log.setProcessInstanceId(processInstanceId);
            log.setType(Log.LogType.ERROR);
            logService.save(log);
        }
        this.result(execution);
    }

    private void deleteFile(File file) {
        try {
            if (file != null) {
                FileUtils.forceDelete(file);
            }
        } catch (IOException ex) {
//            Logger.getLogger(BindFileToProcessJob.class.getName()).log(Level.WARNING, "[deleteFile]", ex);
        }
    }

    private synchronized void createAttchament(ProcessFilesInLoteStatusDTO process) {

        int count = 0;
        List<String> attachmentsIds = new ArrayList<>();

        for (FileParsedDTO f : process.prepareFiles()) {

            try {

                BeanIO beanIO = new BeanIO();
                File file = beanIO.write(f, process.getTaskId(), process.getProcessInstanceId(), ++count);
                InputStream ip = new FileInputStream(file);

                Attachment attachment = taskService
                        .createAttachment(
                                "application/vnd.ms-excel",
                                null,
                                process.getProcessInstanceId(),
                                file.getName(),
                                "attachmentDescription",
                                ip);

                attachmentsIds.add(attachment.getId());
                this.deleteFile(file);

            } catch (Exception e) {
                Logger.getLogger(ProcessFilesInLoteTask1.class.getName()).log(Level.SEVERE, "[createAttchament]", e);
            }

        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(LIST_ATTACHMENT_ID, attachmentsIds);
        parameters.put(CONTROLE, RESPONSE_LAYOUT_VALID);

        this.completeTask(process.getTaskId(), parameters);

    }

    @Transactional
    private void completeTask(String id, Map<String, Object> parameters) {

        try {
            
            synchronized(taskService){
                taskService.complete(id, parameters);            
            }           
            
        } catch (Exception e) {
            Logger.getLogger(ProcessFilesInLoteTask1.class.getName()).log(Level.SEVERE, MessageFormat.format("[completeTask] -> Erro ao realizar o completeTask taskId:{0}", id), e);
        }

    }

    private void bindFileToProcess() {
        this.status.forEach(process -> {
            if (process.isCompletedSearch()) {
                Set<FileParsedDTO> files = process.prepareFiles();
                this.createAttchament(process);
            }
        });
    }

    private void filterInformations(List<FileParsedDTO> files) {

        this.status.forEach(s -> {
            s.getPoints().forEach(point -> {

                Optional<FileParsedDTO> optFile = files.parallelStream().filter(f -> {
                    return f.getDetails().parallelStream().anyMatch(d -> d.getMeansurementPoint().contains(point));
                }).findFirst();

                if (!s.isCompletedSearch() && optFile.isPresent()) {

                    FileParsedDTO fileParsedDTO = new FileParsedDTO();
                    fileParsedDTO.setHeader(optFile.get().getHeader());
                    fileParsedDTO.setType(optFile.get().getType());
                    fileParsedDTO.setInformations(optFile.get().getInformations());

                    List<FileDetailDTO> details = optFile
                            .get()
                            .getDetails()
                            .parallelStream()
                            .filter(d -> d.getMeansurementPoint().contains(point))
                            .collect(Collectors.toList());

                    fileParsedDTO.setDetails(details);
                    s.getFilesByPoint().add(fileParsedDTO);
                    s.getPointsChecked().add(point);
                }
            });
        });

    }

    private void result(DelegateExecution execution) {

        //clear some data because can be up database when it will pesist
        List<ResultInLoteStatusDTO> result = new ArrayList();

        this.status.stream().forEach(st -> {
            result.add(new ResultInLoteStatusDTO(st));
        });

        fileLoteErrorDTOs.stream().forEach(l -> {
            l.getErrors().forEach(error -> {
                Log log = new Log();
                log.setActivitiName(execution.getCurrentActivityName());
                log.setMessage(l.getFileName() + " -> " + error);
                log.setProcessInstanceId(processInstanceId);
                log.setType(Log.LogType.ERROR);
                logService.save(log);
            });
        });

        List<String> process = execution.getVariable(RESPONSE_LIST_PROCESS_ANALIZED, List.class);

        FileStatusLoteRequest request = new FileStatusLoteRequest();
        request.setLoadSummary(true);
        request.setProcessInstances(process);

        FileStatusBillingResponse response = meansurementFileService.statusEnd(request);

        // execution.setVariable(RESPONSE_RESULT, response);
    }

    private void restartProcess() {

        LocalDate dateBilling = LocalDate.now().minusMonths(1);

        int month = dateBilling.getMonthValue();
        int year = dateBilling.getYear();

        this.taskService
                .createNativeTaskQuery()
                .sql("SELECT \n"
                        + "   distinct b.*\n"
                        + "FROM\n"
                        + "    matrix.mtx_arquivo_de_medicao a\n"
                        + "        INNER JOIN\n"
                        + "    activiti.ACT_RU_TASK b ON a.act_id_processo = b.proc_inst_id_\n"
                        + "WHERE\n"
                        + "        a.mes = " + month + "\n"
                        + "        AND a.ano = " + year + "\n"
                        + "        AND a.wbc_ponto_de_medicao IS NOT NULL\n"
                        + "        AND b.TASK_DEF_KEY_ in('task-show-error-1','task-show-error-2','task-show-error-ajustament')")
                .list().stream().forEach(task -> {

                    try {

                        Map<String, Object> parameters = new HashMap<>();

                        if (task.getTaskDefinitionKey().equals("task-show-error-ajustament")) {
                            parameters.put(CONTROLE, "REALIZAR NOVO UPLOAD");
                        }
                        taskService.complete(task.getId(), parameters);

                    } catch (Exception e) {
                        Logger.getLogger(ProcessFilesInLoteTask1.class.getName()).log(Level.SEVERE, "[restartProcess] -> Erro ao realizar o completeTask", e);
                    }

                });

    }

    private Set<ProcessFilesInLoteStatusDTO> getProcessPendingForUploadFile(DelegateExecution execution) {

        LocalDate dateBilling = LocalDate.now().minusMonths(1);

        int month = dateBilling.getMonthValue();
        int year = dateBilling.getYear();

        List<org.activiti.engine.task.Task> tasks = this.taskService
                .createNativeTaskQuery()
                .sql("SELECT \n"
                        + "   distinct b.*\n"
                        + "FROM\n"
                        + "    matrix.mtx_arquivo_de_medicao a\n"
                        + "        INNER JOIN\n"
                        + "    activiti.ACT_RU_TASK b ON a.act_id_processo = b.proc_inst_id_\n"
                        + "WHERE\n"
                        + "    a.status = 'FILE_PENDING' AND a.mes = " + month + "\n"
                        + "        AND a.ano = " + year + "\n"
                        + "        AND a.wbc_ponto_de_medicao IS NOT NULL\n"
                        + "        AND b.TASK_DEF_KEY_ in ('task-upload-file-meansurement','task-upload-file-meansurement-1','task-upload-file-meansurement-2')")
                .list();

        Set<ProcessFilesInLoteStatusDTO> listStatus = new HashSet<>();

        tasks.forEach(task -> {

            List<MeansurementFile> files = meansurementFileService.findByProcessInstanceId(task.getProcessInstanceId());
            ProcessFilesInLoteStatusDTO pfilsdto = new ProcessFilesInLoteStatusDTO();
            pfilsdto.setStatus(ProcessFilesInLoteStatusDTO.Status.PENDING);
            pfilsdto.setProcessInstanceId(task.getProcessInstanceId());
            pfilsdto.setTaskName(task.getName());
            pfilsdto.setTaskId(task.getId());
            List<String> points = files
                    .stream()
                    .map(MeansurementFile::getMeansurementPoint)
                    .collect(Collectors.toList());

            pfilsdto.setPoints(points);
            listStatus.add(pfilsdto);
        });

        List<String> process = tasks.stream().map(task -> task.getProcessInstanceId()).collect(Collectors.toList());

        execution.setVariable(RESPONSE_LIST_PROCESS_ANALIZED, process);

        return listStatus;

    }
}
