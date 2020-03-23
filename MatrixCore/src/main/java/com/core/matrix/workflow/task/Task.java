/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import static com.core.matrix.utils.Constants.CONST_QUALITY_COMPLETE;
import static com.core.matrix.utils.Constants.CONST_SITUATION_1;
import static com.core.matrix.utils.Constants.CONST_SITUATION_2;
import static com.core.matrix.utils.Constants.CONST_SITUATION_3;
import static com.core.matrix.utils.Constants.CONST_SITUATION_4;
import static com.core.matrix.utils.Constants.CONST_SOURCE_COLLECTION_1;
import static com.core.matrix.utils.Constants.CONST_SOURCE_COLLECTION_2;
import static com.core.matrix.utils.Constants.CONST_SOURCE_COLLECTION_3;
import static com.core.matrix.utils.Constants.CONST_SOURCE_COLLECTION_4;
import static com.core.matrix.utils.Constants.CONTROLE;
import static com.core.matrix.utils.Constants.RESPONSE_CALENDAR_INVALID;
import static com.core.matrix.utils.Constants.RESPONSE_RESULT_MESSAGE;
import static com.core.matrix.utils.Constants.TYPE_ENERGY_LIQUID;
import static com.core.matrix.utils.MeansurementFileType.LAYOUT_A;
import static com.core.matrix.utils.MeansurementFileType.LAYOUT_B;
import static com.core.matrix.utils.MeansurementFileType.LAYOUT_C;
import com.core.matrix.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 *
 * @author thiag
 */
public interface Task extends JavaDelegate {

    default List<MeansurementFileDetail> getDetails(MeansurementFile file, DelegateExecution delegateExecution) throws Exception {

        List<MeansurementFileDetail> result = new ArrayList<>();        

        switch (file.getType()) {
            case LAYOUT_A:
                result = file.getDetails()
                        .parallelStream()
                        .filter(d -> d.getEnergyType().equalsIgnoreCase(TYPE_ENERGY_LIQUID))
                        .filter(d -> {
                            if ((d.getReasonOfSituation() != null && d.getReasonOfSituation().length() > 0)
                                    && (Utils.checkDistance(CONST_SITUATION_1, d.getReasonOfSituation()) > 0.90
                                    || (Utils.checkDistance(CONST_SITUATION_2, d.getReasonOfSituation()) > 0.90)
                                    || (Utils.checkDistance(CONST_SITUATION_3, d.getReasonOfSituation()) > 0.90)
                                    || (Utils.checkDistance(CONST_SITUATION_4, d.getReasonOfSituation()) > 0.90))) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
                break;
            case LAYOUT_B:

                result = file.getDetails()
                        .parallelStream()
                        .filter(d -> d.getEnergyType().equalsIgnoreCase(TYPE_ENERGY_LIQUID))
                        .filter(d -> {

                            if ((d.getSourceCollection() != null && d.getSourceCollection().length() > 0)
                                    && (Utils.checkDistance(CONST_SOURCE_COLLECTION_1, d.getSourceCollection()) > 0.90
                                    || (Utils.checkDistance(CONST_SOURCE_COLLECTION_2, d.getSourceCollection()) > 0.90)
                                    || (Utils.checkDistance(CONST_SOURCE_COLLECTION_3, d.getSourceCollection()) > 0.90)
                                    || (Utils.checkDistance(CONST_SOURCE_COLLECTION_4, d.getSourceCollection()) > 0.90))) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());
                break;
            case LAYOUT_C:
            case LAYOUT_C_1:
                result = file.getDetails()
                        .parallelStream()
                        .filter(detail -> detail.getMeansurementPoint().contains("(L)"))
                        .filter(d -> {
                            if ((d.getQuality() != null && d.getQuality().length() > 0)
                                    && Utils.checkDistance(CONST_QUALITY_COMPLETE, d.getQuality()) > 0.90) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .filter(d -> {
                            if ((d.getOrigem() != null && d.getOrigem().length() > 0)
                                    && ((Utils.checkDistance(CONST_SITUATION_2, d.getOrigem()) > 0.90)
                                    || (Utils.checkDistance(CONST_SITUATION_3, d.getOrigem()) > 0.90)
                                    || (Utils.checkDistance(CONST_SITUATION_4, d.getOrigem()) > 0.90))) {
                                return true;
                            } else {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());

                break;
        }

        if (result.isEmpty()) {
            delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Não existe nenhum registro para realizar as validações!");
            delegateExecution.setVariable(CONTROLE, RESPONSE_CALENDAR_INVALID, true);
            throw new Exception("Não existe dados Suficiente");
        } else {
            return result;
        }

    }

}
