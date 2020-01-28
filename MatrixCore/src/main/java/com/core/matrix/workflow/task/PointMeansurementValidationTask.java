/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.ErrorInformation;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.*;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.wbc.service.MeansurementPointService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
    private MeansurementPointService pointService;

    @Autowired
    private MeansurementFileService meansurementFileService;

    @Override
    public void execute(DelegateExecution de) throws Exception {

        Long id = de.getVariable(FILE_MEANSUREMENT_ID, Long.class);

        MeansurementFile file = meansurementFileService.findById(id);

        List<String> invalidPoints = Collections.synchronizedList(new ArrayList<String>());

        try {

            this.getDetails(file)
                    .forEach((point) -> {
                        boolean existsPoint = pointService.existsPoint(point);
                        if (!existsPoint) {
                            invalidPoints.add(MessageFormat.format("Ponto de medição [ {0} ] não foi localizado ! ", point));
                        }
                    });

            if (!invalidPoints.isEmpty()) {
                meansurementFileService.updateStatus(MeansurementFileStatus.POINT_ERROR, id);
                ErrorInformation<String> errors = new ErrorInformation<>("Pontos de medição que não foram encontrados!", invalidPoints);
                de.setVariable(RESPONSE_RESULT, errors);
                de.setVariable(CONTROLE, RESPONSE_MEANSUREMENT_POINT_INVALID);
            } else {
                meansurementFileService.updateStatus(MeansurementFileStatus.SUCCESS, id);
                de.setVariable(CONTROLE, RESPONSE_MEANSUREMENT_POINT_VALID);
            }

        } catch (Exception e) {
            Logger.getLogger(PointMeansurementValidationTask.class.getName()).log(Level.SEVERE, "[execute]", e);
            ErrorInformation<String> errors = new ErrorInformation<>(e.getMessage(), null);
            de.setVariable(RESPONSE_RESULT, errors);
           // meansurementFileService.updateStatus(MeansurementFileStatus.POINT_ERROR, id);
            de.setVariable(CONTROLE, RESPONSE_MEANSUREMENT_POINT_INVALID);
        }

    }

    private List<String> getDetails(MeansurementFile file) throws Exception {

        switch (file.getType()) {
            case LAYOUT_A:
                return file.getDetails()
                        .parallelStream()
                        .map(detail -> detail.getMeansurementPoint())
                        .collect(Collectors.toList());
            case LAYOUT_B:
                return file.getDetails()
                        .parallelStream()
                        .filter(d -> d.getEnergyType().equalsIgnoreCase(TYPE_ENERGY_LIQUID))
                        .map(d -> d.getMeansurementPoint())
                        .collect(Collectors.toList());
            case LAYOUT_C:
                return file.getDetails()
                        .stream()
                        .filter(detail -> detail.getMeansurementPoint().contains("(L)"))
                        .map(detail -> detail.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim())
                        .collect(Collectors.toList());
            default:
                throw new Exception("Não foi possivel selecionar os ponto de medição");
        }

    }

}
