/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.FileLoteErrorDTO;
import com.core.matrix.dto.ProcessFilesInLoteStatusDTO;
import com.core.matrix.dto.ResultInLoteStatusDTO;
import com.core.matrix.factory.EmailFactory;
import com.core.matrix.jobs.ParseFileJob;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.request.FileStatusLoteRequest;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.RESPONSE_LIST_PROCESS_ANALIZED;
import com.core.matrix.utils.ThreadPoolBindFile;
import com.core.matrix.utils.ThreadPoolEmail;
import com.core.matrix.utils.ThreadPoolParseFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class ProcessFilesInLoteTask implements JavaDelegate, Observer {

    private TaskService taskService;

    private Set<ProcessFilesInLoteStatusDTO> status;
    private String processInstanceId;
    private List<FileLoteErrorDTO> fileLoteErrorDTOs;

    private EmailFactory emailFactory;
    private ThreadPoolEmail threadPoolEmail;
    private MeansurementFileService meansurementFileService;

    private LogService logService;

    private static ApplicationContext context;

    private ThreadPoolBindFile threadPoolBindFile;
    private ThreadPoolParseFile threadPoolParseFile;

    public ProcessFilesInLoteTask() {

        synchronized (ProcessFilesInLoteTask.context) {
            this.emailFactory = ProcessFilesInLoteTask.context.getBean(EmailFactory.class);
            this.threadPoolEmail = ProcessFilesInLoteTask.context.getBean(ThreadPoolEmail.class);
            this.meansurementFileService = ProcessFilesInLoteTask.context.getBean(MeansurementFileService.class);
            this.logService = ProcessFilesInLoteTask.context.getBean(LogService.class);
            this.threadPoolBindFile = ProcessFilesInLoteTask.context.getBean(ThreadPoolBindFile.class);
            this.threadPoolParseFile = ProcessFilesInLoteTask.context.getBean(ThreadPoolParseFile.class);
        }

    }

    public ProcessFilesInLoteTask(ApplicationContext context, Long threadPoolSize) {
        this.context = context;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
            
            this.checkStatusPools();

            processInstanceId = execution.getProcessInstanceId();
            taskService = execution.getEngineServices().getTaskService();
            fileLoteErrorDTOs = Collections.synchronizedList(new ArrayList());           

            status = new CopyOnWriteArraySet(this.getProcessPendingForUploadFile(execution));

            if (!status.isEmpty()) {
                this.startPollExecutor();
                this.checkJobsParseFile();
                this.checkJobsBindFile();
            } else {

                Log log = new Log();
                log.setActivitiName(execution.getCurrentActivityName());
                log.setMessage("Não foi encontrato nenhum processo para associar os arquivos");
                log.setProcessInstanceId(processInstanceId);
                log.setType(Log.LogType.ERROR);
                logService.save(log);
            }

        } catch (Exception e) {
            Logger.getLogger(ProcessFilesInLoteTask.class.getName()).log(Level.SEVERE, "[execute]", e);
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

    private void checkStatusPools() {
        this.threadPoolBindFile = this.threadPoolBindFile.isTerminated() ? this.context.getBean(ThreadPoolBindFile.class) : this.threadPoolBindFile;
        this.threadPoolParseFile = this.threadPoolParseFile.isTerminated() ? this.context.getBean(ThreadPoolParseFile.class) : this.threadPoolParseFile;
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
        request.setProcessInstances(process);

        meansurementFileService.generateStatus(request, execution.getProcessInstanceId());

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
            pfilsdto.addObserver(this);

            listStatus.add(pfilsdto);
        });

        List<String> process = tasks.stream().map(task -> task.getProcessInstanceId()).collect(Collectors.toList());

        execution.setVariable(RESPONSE_LIST_PROCESS_ANALIZED, process);

        return listStatus;

    }

    private void startPollExecutor() {

        taskService.getProcessInstanceAttachments(processInstanceId).forEach(att -> {
            ParseFileJob job = new ParseFileJob();
            job.setTaskService(taskService);
            job.setLoteStatusDTOs(status);
            job.setAttachmentId(att.getId());
            job.setFileLoteErrorDTOs(fileLoteErrorDTOs);
            threadPoolParseFile.submit(job);
        });

    }

    private void checkJobsParseFile() {

        if (!this.threadPoolParseFile.isDone()) {
            this.threadPoolParseFile.shutdown();
            this.threadPoolParseFile.monitor();
        }
    }

    private void checkJobsBindFile() {

        if (!this.threadPoolBindFile.isDone()) {
            this.threadPoolBindFile.shutdown();
            this.threadPoolBindFile.monitor();
        }
    }

    @Override
    public synchronized void update(Observable o, Object arg) {

        Boolean insert = Optional.ofNullable(arg).isPresent() ? (Boolean) arg : Boolean.FALSE;

        ProcessFilesInLoteStatusDTO processFilesInLoteStatusDTO = (ProcessFilesInLoteStatusDTO) o;

        if (insert && processFilesInLoteStatusDTO.getStatus().equals(ProcessFilesInLoteStatusDTO.Status.PENDING)) {

            synchronized (processFilesInLoteStatusDTO) {
                processFilesInLoteStatusDTO.deleteObserver(this);
                processFilesInLoteStatusDTO.setStatus(ProcessFilesInLoteStatusDTO.Status.ASSOCIATED);
                threadPoolBindFile.submit(processFilesInLoteStatusDTO);
            }

        }

    }

}
