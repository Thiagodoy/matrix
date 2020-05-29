/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.service.ContractCompInformationService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.PROCESS_CONTRACTS_RELOAD_BILLING;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CLIENT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_MEANSUREMENT_POINT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_MONITOR_CLIENT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_PROCESSO_ID;
import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.service.ContractService;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.runtime.ProcessInstance;
import org.joda.time.LocalDate;
import org.springframework.context.ApplicationContext;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_NICKNAME;
import java.util.Objects;

/**
 *
 * @author thiag
 */
public class BillingContractsTask implements JavaDelegate {

    private ContractService contractService;
    private MeansurementFileService meansurementFileService;
    private ContractCompInformationService contractCompInformationService;
    private LogService logService;

    private static ApplicationContext context;

    public BillingContractsTask() {
        synchronized (BillingContractsTask.context) {
            this.contractService = context.getBean(ContractService.class);
            this.meansurementFileService = context.getBean(MeansurementFileService.class);
            this.contractCompInformationService = context.getBean(ContractCompInformationService.class);
            this.logService = context.getBean(LogService.class);
        }
    }

    public BillingContractsTask(ApplicationContext context) {
        BillingContractsTask.context = context;
    }

    private ProcessInstance createAProcessForBilling(DelegateExecution execution, ContractDTO contract) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(PROCESS_INFORMATION_MONITOR_CLIENT, contract.getSNmEmpresaEpce().toString());        
        return this.createAProcessForBilling(execution, Arrays.asList(contract), variables);
    }

    @javax.transaction.Transactional
    private synchronized ProcessInstance createAProcessForBilling(DelegateExecution execution, List<ContractDTO> contracts, Map<String, Object> variables) {

        variables.put(Constants.LIST_CONTRACTS_FOR_BILLING, contracts);

        List<String> meansurementPoint = new ArrayList();
        List<String> nicknames = new ArrayList();
        List<String> cnpjs = new ArrayList();

        contracts.forEach(contract -> {
            meansurementPoint.add(contract.getMeansurementPoint());
            nicknames.add(contract.getSNmApelido());
            cnpjs.add(contract.getSNrCnpj());
        });

        String pointers = meansurementPoint
                .stream()
                .filter(p -> Objects.nonNull(p))
                .collect(Collectors.joining(","));

        String nickname = nicknames
                .stream()
                .filter(p -> Objects.nonNull(p))
                .collect(Collectors.joining(","));
        
        
        String cnpjsString = cnpjs
                .stream()
                .filter(p -> Objects.nonNull(p))
                .collect(Collectors.joining(","));

        ProcessInstance processInstance = execution.getEngineServices().getRuntimeService().startProcessInstanceByMessage(Constants.PROCESS_MEANSUREMENT_FILE_MESSAGE_EVENT, variables);

        variables.put(PROCESS_INFORMATION_MEANSUREMENT_POINT, pointers);
        variables.put(PROCESS_INFORMATION_NICKNAME, nickname);
        variables.put(Constants.PROCESS_INFORMATION_CNPJ, cnpjsString);
        variables.put(PROCESS_INFORMATION_PROCESSO_ID, processInstance.getProcessInstanceId());

        execution.getEngineServices().getRuntimeService().setVariables(processInstance.getProcessInstanceId(), variables);

        return processInstance;
    }

    private void createMeansurementFile(String processInstanceId, ContractDTO contract) {

        LocalDate monthBilling = LocalDate.now().minusMonths(1);

        Long month = Integer.valueOf(monthBilling.getMonthOfYear()).longValue();
        Long year = Integer.valueOf(monthBilling.getYear()).longValue();

        MeansurementFile meansurementFile = new MeansurementFile(contract, processInstanceId, contract.getMeansurementPoint());
        meansurementFile.setMonth(month);
        meansurementFile.setYear(year);
        this.meansurementFileService.saveFile(meansurementFile);

    }

    private void createMeansurementFile(String processInstanceId, List<ContractDTO> contracts) {

        //Remove contract parent
        contracts
                .stream()
                .filter(c -> c.getNCdContratoRateioControlador() != null)
                .forEach(contract -> {
                    this.createMeansurementFile(processInstanceId, contract);
                });
    }

    private boolean hasMeansurementFile(ContractCompInformation contractCompInformation) {

        LocalDate monthBilling = LocalDate.now().minusMonths(1);

        Long month = Integer.valueOf(monthBilling.getMonthOfYear()).longValue();
        Long year = Integer.valueOf(monthBilling.getYear()).longValue();

        return this.meansurementFileService.exists(contractCompInformation.getWbcContract(), contractCompInformation.getMeansurementPoint(), month, year);

    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
            List<ContractDTO> contracts;

            if (execution.hasVariable(PROCESS_CONTRACTS_RELOAD_BILLING)) {
                List<ContractCompInformation> cc = (List<ContractCompInformation>) execution.getVariable(PROCESS_CONTRACTS_RELOAD_BILLING, List.class);

              contracts = Collections.synchronizedList(this.contractService.listForBilling(cc));

            }else{
              contracts = Collections.synchronizedList(this.contractService.listForBilling(null));  
            }

            List<Log> logs = new ArrayList<>();

            //Contracts without rateio
            contracts.stream()
                    .filter(contract -> contract.getBFlRateio().equals(0L))
                    .forEach(contract -> {
                        try {

                            Optional<ContractCompInformation> opt = this.contractCompInformationService.findByContract(Long.parseLong(contract.getSNrContrato()));

                            if (opt.isPresent()) {
                                ContractCompInformation compInformation = opt.get();
                                contract.setMeansurementPoint(compInformation.getMeansurementPoint());

                                if (!this.hasMeansurementFile(compInformation)) {
                                    String processInstanceId = this.createAProcessForBilling(execution, contract).getProcessInstanceId();
                                    this.createMeansurementFile(processInstanceId, contract);
                                }

                            } else {
                                String message = MessageFormat.format("Não foi possivel criar processo de medição para o contrato abaixo:\n{0}", contract.toString());
                                Log log = new Log();
                                log.setMessage(message);
                                log.setNameProcesso(execution.getProcessDefinitionId());
                                logs.add(log);
                            }

                        } catch (Exception e) {
                            Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, "[execute]", e);
                            Log log = new Log();
                            log.setMessage(e.getMessage());
                            log.setNameProcesso(execution.getProcessDefinitionId());
                            logs.add(log);
                        }

                    });

            if (!logs.isEmpty()) {
                this.logService.save(logs);
                logs.clear();
            }

            //Contracts with rateio
            contracts
                    .parallelStream()
                    .filter(contract -> contract.getBFlRateio().equals(1L) && contract.getNCdContratoRateioControlador() != null)
                    .collect(Collectors.groupingBy(ContractDTO::getNCdContratoRateioControlador))
                    .forEach((contractParent, contractsSon) -> {

                        contractsSon.stream().forEach(cc -> {

                            try {
                                Optional<ContractCompInformation> opt = this.contractCompInformationService.findByContract(Long.parseLong(cc.getSNrContrato()));

                                if (opt.isPresent()) {
                                    ContractCompInformation compInformation = opt.get();
                                    cc.setMeansurementPoint(compInformation.getMeansurementPoint());
                                } else {
                                    String message = MessageFormat.format("Não foi possivel criar processo de medição para o contrato [rateio] abaixo:\n{0}", cc.toString());
                                    Log log = new Log();
                                    log.setMessage(message);
                                    log.setNameProcesso(execution.getProcessDefinitionId());
                                    logs.add(log);
                                }
                            } catch (Exception e) {
                                Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, "[execute]", e);
                                Log log = new Log();
                                log.setMessage(e.getMessage());
                                log.setNameProcesso(execution.getProcessDefinitionId());
                                logs.add(log);

                            }

                        });

                        //Get Contract parent
                        Optional<ContractDTO> opt = contracts
                                .parallelStream()
                                .filter(c -> c.getNCdContrato().equals(contractParent))
                                .findFirst();

                        if (opt.isPresent() && logs.isEmpty()) {

                            List<ContractDTO> sons = contractsSon.stream().filter(t -> t.getMeansurementPoint() != null).collect(Collectors.toList());

                            sons.add(opt.get());

                            Map<String, Object> variables = new HashMap<>();
                            variables.put(PROCESS_INFORMATION_MONITOR_CLIENT, sons.stream().findFirst().get().getSNmEmpresaEpce());
                            variables.put(PROCESS_INFORMATION_CLIENT, sons.stream().findFirst().get().getSNmEmpresaEpce());
                            ContractDTO c = sons.stream().findFirst().get();

                            try {
                                Optional<ContractCompInformation> ccc = this.contractCompInformationService.listByContract(Long.parseLong(c.getSNrContrato())).stream().findFirst();

                                if (!this.hasMeansurementFile(ccc.get())) {
                                    String processInstanceId = this.createAProcessForBilling(execution, sons, variables).getProcessInstanceId();
                                    this.createMeansurementFile(processInstanceId, sons);
                                }

                            } catch (Exception ex) {
                                Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, "[Search contracts]", ex);
                            }

                        } else {
                            this.logService.save(logs);
                            logs.clear();
                        }

                    });

        } catch (Exception e) {
            Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, "[ execute ]", e);
            Log log = new Log();
            log.setMessage(e.getMessage());
            log.setNameProcesso(execution.getProcessDefinitionId());
            this.logService.save(log);
        }
    }

}
