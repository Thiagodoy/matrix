/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.service.MeansurementFileResultService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.VAR_NO_PERSIST;
import com.core.matrix.utils.MeansurementFileStatus;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class CleanFiles implements JavaDelegate {

    private static ApplicationContext context;
    private MeansurementFileService fileService;
    private MeansurementFileDetailService fileDetailService;
    private MeansurementFileResultService fileResultService;
    private LogService logService;

    public CleanFiles(ApplicationContext context) {
        CleanFiles.context = context;
    }

    public CleanFiles() {
        synchronized (CleanFiles.context) {
            this.fileService = CleanFiles.context.getBean(MeansurementFileService.class);
            this.fileDetailService = CleanFiles.context.getBean(MeansurementFileDetailService.class);
            this.logService = CleanFiles.context.getBean(LogService.class);
            this.fileResultService = CleanFiles.context.getBean(MeansurementFileResultService.class);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {

            List<Attachment> attachments = execution.getEngineServices().getTaskService().getProcessInstanceAttachments(execution.getProcessInstanceId());

            List<Comment> comments = execution.getEngineServices().getTaskService().getProcessInstanceComments(execution.getProcessInstanceId());

            for (Attachment attachment : attachments) {
                execution.getEngineServices().getTaskService().deleteAttachment(attachment.getId());
            }

            
            for (Comment comment : comments) {
                execution.getEngineServices().getTaskService().deleteComment(comment.getId());
            }           

            
//            this.fileService.findByProcessInstanceId(execution.getProcessInstanceId()).forEach(file -> {
//
//                if (!file.getDetails().isEmpty()) {
//                    try {
//                        this.fileDetailService.deleteByMeansurementFileId(file.getId());
//                    } catch (Exception e) {
//                        Logger.getLogger(CleanFiles.class.getName()).log(Level.SEVERE, "[ deleteByMeansurementFileId ]", e);
//                    }
//                }
//
//                this.fileService.updateStatus(MeansurementFileStatus.FILE_PENDING, file.getId());
//                this.fileService.updateFile(null, file.getId());
//            });

            this.fileService.updateStatusByProcessInstanceId(MeansurementFileStatus.FILE_PENDING, execution.getProcessInstanceId());
            List<Long> filesIds = this.fileService.listIdsByProcessInstanceId(execution.getProcessInstanceId());

            filesIds.stream().forEach(l -> {
                this.fileDetailService.deleteByMeansurementFileId(l);
            });
            
            logService.deleteLogsByProcessInstance(execution.getProcessInstanceId());
            
            fileResultService.deleteByProcess(execution.getProcessInstanceId());           

            execution.removeVariable(VAR_NO_PERSIST);

        } catch (Exception e) {
            Logger.getLogger(CleanFiles.class.getName()).log(Level.SEVERE, "[ execute ]", e);
        }
    }

    private void loggerPerformance(long start, String fase) {
        Logger.getLogger(CleanFiles.class.getName()).log(Level.INFO, MessageFormat.format("[loggerPerformance] -> etapa: {0} tempo : {1} min", fase, (System.currentTimeMillis() - start) / 60000D));
    }

}
