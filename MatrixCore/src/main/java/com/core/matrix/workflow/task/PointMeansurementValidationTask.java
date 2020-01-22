/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.Log;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.*;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.wbc.service.MeansurementPointService;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class PointMeansurementValidationTask implements JavaDelegate {

    @Autowired
    private MeansurementFileService fileService;

    @Autowired
    private MeansurementPointService pointService;

    @Autowired
    private LogService logService;

    @Autowired
    private MeansurementFileService meansurementFileService;

    private Boolean hasError = Boolean.FALSE;

    @Override
    public void execute(DelegateExecution de) throws Exception {

        
        Long id = de.getVariable(FILE_MEANSUREMENT_ID, Long.class);

        try {

            //Limpa os logs gerados
            logService.deleteLogsByFile(id);

            fileService.findById(id)
                    .getDetails()
                    .parallelStream()
                    .map((d) -> d.getMeansurementPoint())
                    .parallel()
                    .forEach((point) -> {
                        boolean existsPoint = pointService.existsPoint(point);
                        if (!existsPoint) {
                            synchronized (hasError) {
                                hasError = Boolean.TRUE;
                                de.setVariable(CONTROLE, MEANSUREMENT_POINT_INVALID);
                                Log log = new Log();
                                log.setFileId(id);
                                log.setMessage(MessageFormat.format("Ponto de medição [ {0} ] não foi localizado ! ", point));
                                logService.save(log);
                            }
                        }
                    });

            if (hasError) {
                meansurementFileService.updateStatus(MeansurementFileStatus.POINT_ERROR, id);
                de.setVariable(CONTROLE, MEANSUREMENT_POINT_INVALID);
            } else {
                meansurementFileService.updateStatus(MeansurementFileStatus.SUCCESS, id);
                de.setVariable(CONTROLE, MEANSUREMENT_POINT_VALID);
            }

        } catch (Exception e) {            
            Logger.getLogger(PointMeansurementValidationTask.class.getName()).log(Level.SEVERE, "[execute]", e);
            meansurementFileService.updateStatus(MeansurementFileStatus.POINT_ERROR, id);
            de.setVariable(CONTROLE, MEANSUREMENT_POINT_INVALID);
        }

    }

}
