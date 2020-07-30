/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.ContractMtx;
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
import static com.core.matrix.utils.Constants.LIST_ATTACHMENT_ID;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CONTRACTS_MATRIX;
import static com.core.matrix.utils.Constants.RESPONSE_CALENDAR_INVALID;
import static com.core.matrix.utils.Constants.RESPONSE_RESULT_MESSAGE;
import static com.core.matrix.utils.Constants.TYPE_ENERGY_LIQUID;
import static com.core.matrix.utils.Constants.VAR_LIST_FILES;
import static com.core.matrix.utils.Constants.VAR_MAP_DETAILS;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import com.core.matrix.utils.MeansurementFileType;
import static com.core.matrix.utils.MeansurementFileType.LAYOUT_A;
import static com.core.matrix.utils.MeansurementFileType.LAYOUT_B;
import static com.core.matrix.utils.MeansurementFileType.LAYOUT_C;
import com.core.matrix.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.delegate.VariableScope;

/**
 *
 * @author thiag
 */
public abstract class Task implements JavaDelegate {

    Map<String, Object> variables = new HashMap<>();

    public void writeVariables(VariableScope delegateExecution) {

        if (!variables.isEmpty()) {
            variables.entrySet().forEach((keyValue) -> {
                delegateExecution.setVariable(keyValue.getKey(), keyValue.getValue(), true);
            });

            variables.clear();
        }
    }

    public synchronized void setVariable(String key, Object value) {
        this.variables.put(key, value);
    }

    public synchronized void loadVariables(VariableScope delegateExecution) {
        variables.putAll(delegateExecution.getVariables());
    }    

    public synchronized List<MeansurementFileDetail> getDetails(MeansurementFile file, VariableScope delegateExecution) throws Exception {

        List<MeansurementFileDetail> result = new ArrayList<>();
        
        MeansurementFileType type = file.getType();
        
        if(this.isOnlyContractFlat() && !Optional.ofNullable(this.getMapDetails()).isPresent() ){
            return result;
        }        
        
        if(this.isUnitConsumer(file.getWbcContract())){            
           type = this.getFiles(false).stream().map(MeansurementFile::getType).distinct().findFirst().orElse(null);            
        }

        switch (type) {
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

    public Map<String, List<MeansurementFileDetail>> getMapDetails() {
        return (Map) this.variables.get(VAR_MAP_DETAILS);
    }
    
    public List<ContractMtx>getContractsMtx(){
        return (List<ContractMtx>) this.variables.get(PROCESS_INFORMATION_CONTRACTS_MATRIX);
    }
    
    public List<String> getAllFilesUploaded(){
        return (List<String>) this.variables.get(LIST_ATTACHMENT_ID);
    } 
    
    
    public boolean isOnlyContractFlat() {

        List<ContractMtx> contractMtxs = this.getContractsMtx();

        boolean allFlat = contractMtxs.stream().allMatch(ContractMtx::isFlat);
        boolean allUnitConsumer = contractMtxs.stream().allMatch(ContractMtx::isConsumerUnit);
        boolean noFile = this.getAllFilesUploaded().isEmpty();
        return ((allFlat || allUnitConsumer) && noFile);
    }
    
    
    public boolean isFlat(Long contract){
        List<ContractMtx> contractMtxs = this.getContractsMtx();
        return contractMtxs.stream().filter(c-> c.getWbcContract().equals(contract)).allMatch(ContractMtx::isFlat);
    }
    
    public boolean isUnitConsumer(Long contract){
        List<ContractMtx> contractMtxs = this.getContractsMtx();        
        return contractMtxs.stream().filter(c-> c.getWbcContract().equals(contract)).allMatch(ContractMtx::isConsumerUnit);
    }
    
    

    public List<MeansurementFile> getFiles(boolean loadDetail) {

        List<MeansurementFile> files = (List) this.variables.get(VAR_LIST_FILES);

        Map<String, List<MeansurementFileDetail>> details = this.getMapDetails();

        files.forEach(f -> {
            if(loadDetail && Optional.ofNullable(details).isPresent()){
               f.setDetails(details.get(f.getMeansurementPoint())); 
            }            
        });

        return files;
    }

    public void addDetails(String point, List<MeansurementFileDetail> merge) {

        synchronized (variables) {
            Map<String, List<MeansurementFileDetail>> details = variables.containsKey(VAR_MAP_DETAILS)
                    ? (Map) variables.get(VAR_MAP_DETAILS)
                    : new HashMap<>();

            if (details.containsKey(point)) {
                details.get(point).addAll(merge);
            } else {
                details.put(point, merge);
            }

            variables.put(VAR_MAP_DETAILS, details);
        }

    }

    public void setFiles(VariableScope delegateExecution, List<MeansurementFile> files) {
        this.setVariable(VAR_LIST_FILES, files);
    }

    public void removeFileTemp(VariableScope delegateExecution) {
        delegateExecution.removeVariable(VAR_MAP_DETAILS);
        delegateExecution.removeVariable(VAR_LIST_FILES);
    }

    public boolean hasHourError(VariableScope delegateExecution, String point) {

        synchronized (delegateExecution) {

            boolean result = this.getMapDetails().get(point)
                    .stream().anyMatch(d -> d.getStatus().equals(MeansurementFileDetailStatus.HOUR_ERROR));

            return result;
        }

    }

    public boolean hasDayError(VariableScope delegateExecution, String point) {

        synchronized (delegateExecution) {

            boolean result = this.getMapDetails().get(point)
                    .stream().anyMatch(d -> d.getStatus().equals(MeansurementFileDetailStatus.DAY_ERROR));

            return result;
        }
    }

    public void loggerPerformance(long start, String fase) {
//        Logger.getLogger(this.getClass().getName()).log(Level.INFO, MessageFormat.format("[loggerPerformance] -> etapa: {0} tempo : {1} min", fase, (System.currentTimeMillis() - start) / 60000D));
    }

}
