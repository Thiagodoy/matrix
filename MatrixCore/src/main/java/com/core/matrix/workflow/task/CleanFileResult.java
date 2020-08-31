/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.ContractMtx;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.ContractMtxService;
import com.core.matrix.service.MeansurementFileResultService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.VAR_NO_PERSIST;
import com.core.matrix.utils.ThreadPoolDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class CleanFileResult extends Task {

    private static ApplicationContext context;
    private MeansurementFileResultService fileResultService;
    private MeansurementFileService meansurementFileService;
    private ContractMtxService contractMtxService;

    public CleanFileResult(ApplicationContext context) {
        CleanFileResult.context = context;
    }
    
    public static void setContext(ApplicationContext context){
        CleanFileResult.context = context;
    }

    public CleanFileResult() {
        synchronized (CleanFileResult.context) {
            this.fileResultService = CleanFileResult.context.getBean(MeansurementFileResultService.class);
            this.meansurementFileService = CleanFileResult.context.getBean(MeansurementFileService.class);
            this.contractMtxService = CleanFileResult.context.getBean(ContractMtxService.class);
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {

            this.loadVariables(execution);

            this.fileResultService.deleteByProcess(execution.getProcessInstanceId());

            this.hasPersitencePending(execution.getProcessInstanceId());

            List<MeansurementFile> files = this.meansurementFileService.findByProcessInstanceId(execution.getProcessInstanceId());

            Map<String, List<MeansurementFileDetail>> details = files
                    .parallelStream()
                    .map(MeansurementFile::getDetails)
                    .flatMap(List::stream)
                    .collect(Collectors.groupingBy(d -> d.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim()));

            List<MeansurementFile> fTemps = new ArrayList<>();
            files.forEach(ff -> {
                MeansurementFile fTemp = new MeansurementFile();
                fTemp.setMeansurementPoint(ff.getMeansurementPoint());
                fTemp.setWbcContract(ff.getWbcContract());
                fTemp.setMonth(ff.getMonth());
                fTemp.setYear(ff.getYear());
                fTemp.setCompanyName(ff.getCompanyName());
                fTemp.setFile(ff.getFile());
                fTemp.setNickname(ff.getNickname());
                fTemp.setProcessInstanceId(ff.getProcessInstanceId());
                fTemp.setStatus(ff.getStatus());
                fTemp.setId(ff.getId());
                fTemp.setType(ff.getType());
                fTemps.add(fTemp);
            });

            MeansurementFile file = files.stream().findFirst().get();

            List<ContractMtx> contractMtx = this.contractMtxService.findAll(file.getWbcContract()).getContracts();

            this.setVariable(Constants.PROCESS_INFORMATION_CONTRACTS_MATRIX, contractMtx);
            this.setVariable(Constants.VAR_LIST_FILES, fTemps);
            this.setVariable(Constants.VAR_MAP_DETAILS, details);

            this.setVariable(VAR_NO_PERSIST, true);

            this.writeVariables(execution);

        } catch (Exception e) {
            Logger.getLogger(CleanFileResult.class.getName()).log(Level.SEVERE, "[ execute ] process -> " + execution.getProcessInstanceId(), e);
        }

    }

    private void hasPersitencePending(String processInstanceId) {

        while (ThreadPoolDetail.isRunning(processInstanceId)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(CleanFileResult.class.getName()).log(Level.SEVERE, "[waitForPersitData]", ex);
            }
        }

    }

}
