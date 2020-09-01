/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.jobs;

import com.core.matrix.factory.EmailFactory;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.utils.Constants;
import com.core.matrix.utils.ThreadPoolDetail;
import com.core.matrix.utils.ThreadPoolEmail;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.RuntimeService;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class PersistDetailsJob implements Runnable {

    private MeansurementFileDetailService detailService;
    private RuntimeService runtimeService;
    private List<MeansurementFileDetail> details;
    private String processInstanceId;
    private ThreadPoolEmail poolEmail;
    private EmailFactory emailFactory;

    public PersistDetailsJob(ApplicationContext context) {
        detailService = context.getBean(MeansurementFileDetailService.class);
        runtimeService = context.getBean(RuntimeService.class);
        poolEmail = context.getBean(ThreadPoolEmail.class);
        emailFactory = context.getBean(EmailFactory.class);
    }

    public void setDetais(List<MeansurementFileDetail> details) {
        this.details = details;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessInstanceId() {
        return this.processInstanceId;
    }

    @Override
    public void run() {

        try {

            long start = System.currentTimeMillis();
            detailService.saveAllBatch(details, processInstanceId);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, MessageFormat.format("[Salvando registros] -> tempo : {0} min", (System.currentTimeMillis() - start) / 60000D));

            Optional opt = Optional.ofNullable(this.runtimeService.createProcessInstanceQuery().processDefinitionId(processInstanceId).singleResult());

            if (opt.isPresent()) {
                this.runtimeService.removeVariable(processInstanceId, Constants.VAR_MAP_DETAILS);
                this.runtimeService.removeVariable(processInstanceId, Constants.VAR_LIST_FILES);
            }

        } catch (Exception e) {
            Logger.getLogger(PersistDetailsJob.class.getName()).log(Level.SEVERE, "Não foi possivel salvar os dados no banco de dados processInstanceId -> " + processInstanceId, e);
        } finally {
            ThreadPoolDetail.finalize(processInstanceId);
        }

    }

}
