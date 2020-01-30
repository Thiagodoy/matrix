/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.ErrorInformationDTO;
import com.core.matrix.dto.PointDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.service.ContractMeasurementPointService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.*;
import com.core.matrix.utils.MeansurementFileStatus;
import com.core.matrix.wbc.service.MeansurementPointService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
public class PointMeansurementValidationTask implements JavaDelegate {

    private MeansurementPointService pointService;
    private ContractMeasurementPointService contractMeasurementPointService;
    private MeansurementFileService meansurementFileService;

    private static ApplicationContext context;

    public PointMeansurementValidationTask() {

        synchronized (PointMeansurementValidationTask.context) {
            this.pointService = PointMeansurementValidationTask.context.getBean(MeansurementPointService.class);
            this.contractMeasurementPointService = PointMeansurementValidationTask.context.getBean(ContractMeasurementPointService.class);
            this.meansurementFileService = PointMeansurementValidationTask.context.getBean(MeansurementFileService.class);
        }
    }

    public PointMeansurementValidationTask(ApplicationContext context) {
        PointMeansurementValidationTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

        Long id = de.getVariable(FILE_MEANSUREMENT_ID, Long.class);

        MeansurementFile file = meansurementFileService.findById(id);

        List<PointDTO> invalidPoints = Collections.synchronizedList(new ArrayList<PointDTO>());

        try {

            this.getDetails(file)
                    .forEach((point) -> {
                        boolean existsPoint = pointService.existsPoint(point);

                        if (existsPoint) {
                            Optional result = contractMeasurementPointService.findByPoint(point);
                            if (result.isPresent()) {
                                // TODO: Nada a declarar
                            } else {

                                PointDTO pointDTO = new PointDTO(point, "PORTAL", MessageFormat.format("Ponto de medição [ {0} ] não esta associado no portal! ", point));
                                invalidPoints.add(pointDTO);
                            }
                        } else {
                            PointDTO pointDTO = new PointDTO(point, "WBC", MessageFormat.format("Ponto de medição [ {0} ] não esta cadastrado no WBC! ", point));
                            invalidPoints.add(pointDTO);
                        }

                    });

            if (!invalidPoints.isEmpty()) {
                meansurementFileService.updateStatus(MeansurementFileStatus.POINT_ERROR, id);
                ErrorInformationDTO<PointDTO> errors = new ErrorInformationDTO<>("Pontos de medição que não foram encontrados!", invalidPoints, null);
                de.setVariable(RESPONSE_RESULT, errors);
                de.setVariable(CONTROLE, RESPONSE_MEANSUREMENT_POINT_INVALID);
            } else {
                meansurementFileService.updateStatus(MeansurementFileStatus.SUCCESS, id);
                de.setVariable(CONTROLE, RESPONSE_MEANSUREMENT_POINT_VALID);
            }

        } catch (Exception e) {
            Logger.getLogger(PointMeansurementValidationTask.class.getName()).log(Level.SEVERE, "[execute]", e);
            ErrorInformationDTO<String> errors = new ErrorInformationDTO<>(e.getMessage(), null, null);
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
                        .distinct()
                        .collect(Collectors.toList());
            case LAYOUT_B:
                return file.getDetails()
                        .parallelStream()
                        .filter(d -> d.getEnergyType().equalsIgnoreCase(TYPE_ENERGY_LIQUID))
                        .map(d -> d.getMeansurementPoint())
                        .distinct()
                        .collect(Collectors.toList());
            case LAYOUT_C:
                return file.getDetails()
                        .stream()
                        .filter(detail -> detail.getMeansurementPoint().contains("(L)"))
                        .map(detail -> detail.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim())
                        .distinct()
                        .collect(Collectors.toList());
            default:
                throw new Exception("Não foi possivel selecionar os ponto de medição");
        }

    }

}
