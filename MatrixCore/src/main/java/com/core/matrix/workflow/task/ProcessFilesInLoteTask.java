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
import com.core.matrix.jobs.BindFileToProcessJob;
import com.core.matrix.jobs.ParseFileJob;
import com.core.matrix.model.Email;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.Template;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.CREATED_BY;
import static com.core.matrix.utils.Constants.PROCESS_RESULT_FILES_NOK;
import static com.core.matrix.utils.Constants.PROCESS_RESULT_FILES_OK;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_NUMBER_PROCESS;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_USER_EMAIL;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_USER_NAME;
import com.core.matrix.utils.ThreadPoolEmail;
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
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
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

    private ThreadPoolExecutor pool;
    private TaskService taskService;

    private Set<ProcessFilesInLoteStatusDTO> status;
    private String processInstanceId;
    private List<FileLoteErrorDTO> fileLoteErrorDTOs;

    private EmailFactory emailFactory;
    private ThreadPoolEmail threadPoolEmail;
    private MeansurementFileService meansurementFileService;
    private List<Future> executions = new ArrayList<>();

    private static ApplicationContext context;

    public ProcessFilesInLoteTask() {

        synchronized (context) {
            this.emailFactory = this.context.getBean(EmailFactory.class);
            this.threadPoolEmail = this.context.getBean(ThreadPoolEmail.class);
            this.meansurementFileService = this.context.getBean(MeansurementFileService.class);
        }

    }

    public ProcessFilesInLoteTask(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
            processInstanceId = execution.getProcessInstanceId();
            taskService = execution.getEngineServices().getTaskService();
            fileLoteErrorDTOs = Collections.synchronizedList(new ArrayList());
            status = new CopyOnWriteArraySet(getProcessPendingForUploadFile());

            if (!status.isEmpty()) {
                this.initPoolExecutor();
                this.startPollExecutor();
                this.monitorStatusPoolExecutor();
                this.sendEmail(execution);

            } else {
                FileLoteErrorDTO fledto = new FileLoteErrorDTO();
                fledto.setFileName("Erro no processo");
                fledto.setError("Não foi encontrato nenhum processo para associar os arquivos");
                fileLoteErrorDTOs.add(fledto);
            }

        } catch (Exception e) {
            Logger.getLogger(ProcessFilesInLoteTask.class.getName()).log(Level.SEVERE, "[execute]", e);
            FileLoteErrorDTO fledto = new FileLoteErrorDTO();
            fledto.setFileName("Erro no processo");
            fledto.setError("Erro no processo, favor encaminhar para a TI.");
            fileLoteErrorDTOs.add(fledto);
        }

        this.result(execution);

    }

    private void result(DelegateExecution execution) {

        //clear some data because can be up database when it will pesist
        List<ResultInLoteStatusDTO> result = new ArrayList();

        this.status.stream().forEach(st -> {
            result.add(new ResultInLoteStatusDTO(st));
        });

        execution.setVariable(PROCESS_RESULT_FILES_OK, result);
        execution.setVariable(PROCESS_RESULT_FILES_NOK, fileLoteErrorDTOs);

    }

    private void sendEmail(DelegateExecution execution) {

        
        
        while(!this.pool.isTerminated()){
             try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProcessFilesInLoteTask.class.getName()).log(Level.SEVERE, "[sendEmail]", ex);
            }
        }    
        
        String to = execution.getVariable(CREATED_BY, String.class);
        String nameUser = execution
                .getEngineServices()
                .getIdentityService()
                .createUserQuery()
                .userId(to)
                .singleResult()
                .getFirstName();

        Email email = emailFactory.createEmailTemplate(Template.TemplateBusiness.FINISHED_UPLOAD_LOTE_FILE);

        email.setParameter(TEMPLATE_PARAM_USER_NAME, nameUser);
        email.setParameter(TEMPLATE_PARAM_NUMBER_PROCESS, processInstanceId);
        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, to);

        threadPoolEmail.submit(email);

    }

    private Set<ProcessFilesInLoteStatusDTO> getProcessPendingForUploadFile() {

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
                        + "        AND b.TASK_DEF_KEY_ = 'task-upload-file-meansurement'")
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

        return listStatus;

    }

    private void initPoolExecutor() {
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }

    private void startPollExecutor() {

        taskService.getProcessInstanceAttachments(processInstanceId).forEach(att -> {
            ParseFileJob job = new ParseFileJob();
            job.setTaskService(taskService);
            job.setLoteStatusDTOs(status);
            job.setAttachmentId(att.getId());
            job.setFileLoteErrorDTOs(fileLoteErrorDTOs);
            Future future = pool.submit(job);
            executions.add(future);
        });

    }

    private void monitorStatusPoolExecutor() {

        boolean isfinalized = false;
        do {
            isfinalized = this.executions.stream().map(Future::isDone).reduce(Boolean::logicalAnd).get();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProcessFilesInLoteTask.class.getName()).log(Level.SEVERE, "[monitorStatusPoolExecutor]", ex);
            }

        } while (!isfinalized);

        pool.shutdown();

    }

    @Override
    public synchronized void update(Observable o, Object arg) {

        Boolean insert = Optional.ofNullable(arg).isPresent() ? (Boolean) arg : Boolean.FALSE;

        ProcessFilesInLoteStatusDTO processFilesInLoteStatusDTO = (ProcessFilesInLoteStatusDTO) o;

        if (insert && processFilesInLoteStatusDTO.getStatus().equals(ProcessFilesInLoteStatusDTO.Status.PENDING)) {

            synchronized (processFilesInLoteStatusDTO) {
                processFilesInLoteStatusDTO.deleteObserver(this);
                processFilesInLoteStatusDTO.setStatus(ProcessFilesInLoteStatusDTO.Status.ASSOCIATED);
                BindFileToProcessJob job = new BindFileToProcessJob();
                job.setProcessFilesInLoteStatusDTO(processFilesInLoteStatusDTO);
                job.setTaskService(taskService);
                pool.submit(job);
            }

        }

    }

}
