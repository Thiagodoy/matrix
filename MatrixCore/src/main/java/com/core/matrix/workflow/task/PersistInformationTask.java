/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.jobs.PersistDetailsJob;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.VAR_NO_PERSIST;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.utils.ThreadPoolDetail;
import java.util.List;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.VariableScope;
import org.jboss.logging.Logger;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class PersistInformationTask extends Task implements ExecutionListener {

    private MeansurementFileService fileService;    
    private ThreadPoolDetail threadPoolDetail;
    private static ApplicationContext context;

    public PersistInformationTask(ApplicationContext context) {
        PersistInformationTask.context = context;
    }

    public PersistInformationTask() {
        synchronized (PersistInformationTask.context) {
            this.fileService = PersistInformationTask.context.getBean(MeansurementFileService.class);            
            this.threadPoolDetail = PersistInformationTask.context.getBean(ThreadPoolDetail.class);
        }
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        this.run(execution);
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        this.run(execution);
    }

    public void run(VariableScope execution) throws Exception {

        this.loadVariables(execution);

        if (execution.hasVariable(VAR_NO_PERSIST)) {
            execution.removeVariable(VAR_NO_PERSIST);
            return;
        } else {

            try {
                List<MeansurementFile> files = this.getFiles(execution);

                List<MeansurementFileDetail> details = files
                        .stream()
                        .map(MeansurementFile::getDetails)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());

                files.forEach(file -> {
                    this.fileService.updateStatus(MeansurementFileStatus.SUCCESS, file.getId());
                });

                PersistDetailsJob job = new PersistDetailsJob(context);
                job.setDetais(details);
                job.setProcessInstanceId(((DelegateExecution) execution).getProcessInstanceId());

                threadPoolDetail.submit(job);
            } catch (Exception e) {
                Logger.getLogger(PersistInformationTask.class.getName()).log(Logger.Level.FATAL, "[execute]", e);
            }

        }

        this.writeVariables(execution);

    }

}
