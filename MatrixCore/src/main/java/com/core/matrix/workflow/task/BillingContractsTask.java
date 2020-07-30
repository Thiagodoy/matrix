/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.exceptions.ContractNotAssociatedWithPointException;
import com.core.matrix.exceptions.EntityNotFoundException;
import com.core.matrix.exceptions.PointWithoutProinfaException;
import com.core.matrix.factory.EmailFactory;
import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.model.ContractMtx;
import com.core.matrix.model.Email;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.Template;
import com.core.matrix.service.ContractMtxService;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.service.MeansurementPointMtxService;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.GROUP_MANAGER_PORTAL;
import static com.core.matrix.utils.Constants.GROUP_SUPPORT_TI;
import static com.core.matrix.utils.Constants.PROCESS_CONTRACTS_RELOAD_BILLING;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CLIENT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CONTRACTS_MATRIX;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CONTRACT_NUMBERS;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_MEANSUREMENT_POINT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_MONITOR_CLIENT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_NICKNAME;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_PROCESSO_ID;
import static com.core.matrix.utils.Constants.PROCESS_NEW_INSTANCE_ID;
import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.service.ContractService;
import java.util.ArrayList;
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
import org.springframework.context.ApplicationContext;
import com.core.matrix.utils.ThreadPoolEmail;
import com.core.matrix.utils.Utils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author thiag
 */
public class BillingContractsTask implements JavaDelegate {

    private ContractService contractService;
    private MeansurementFileService meansurementFileService;
    private LogService logService;
    private EmailFactory emailFactory;
    private ThreadPoolEmail threadPoolEmail;

    private ContractMtxService contractMtxService;
    private MeansurementPointMtxService meansurementPointMtxService;
    private Set<ContractDTO> contractsNotRegistered = new HashSet<>();
    private Set<ContractDTO> contractsAreNotAssociatedWithPoint = new HashSet<>();
    private Set<String> pointsAreNotAssociatedWithProInfa = new HashSet<>();

    private static ApplicationContext context;

    public BillingContractsTask() {
        synchronized (BillingContractsTask.context) {
            this.contractService = context.getBean(ContractService.class);
            this.meansurementFileService = context.getBean(MeansurementFileService.class);
            this.logService = context.getBean(LogService.class);
            this.emailFactory = context.getBean(EmailFactory.class);
            this.threadPoolEmail = context.getBean(ThreadPoolEmail.class);
            this.contractMtxService = context.getBean(ContractMtxService.class);
            this.meansurementPointMtxService = context.getBean(MeansurementPointMtxService.class);
        }
    }

    public BillingContractsTask(ApplicationContext context) {
        BillingContractsTask.context = context;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        try {
            List<ContractDTO> contracts;

            if (execution.hasVariable(PROCESS_CONTRACTS_RELOAD_BILLING)) {
                List<ContractCompInformation> cc = (List<ContractCompInformation>) execution.getVariable(PROCESS_CONTRACTS_RELOAD_BILLING, List.class);

                contracts = Collections.synchronizedList(this.contractService.listForBilling(cc));

            } else {
                contracts = Collections.synchronizedList(this.contractService.listForBilling(null));
            }

            List<Log> logs = new ArrayList<>();

            this.contractWithoutRateio(contracts, logs, execution);
            this.contractWithRateio(contracts, logs, execution);
            this.sendEmailWithErrors(execution);

            if (!logs.isEmpty()) {
                this.logService.save(logs);
            }

        } catch (Throwable e) {
            Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, "[ execute ]", e);
            Log log = new Log();
            log.setMessage(e.getMessage());
            log.setProcessName(execution.getProcessDefinitionId());
            this.logService.save(log);
        }
    }

    private void sendEmailWithErrors(DelegateExecution execution) {

        File contractsNotRegisteredFile = null;
        File contractsAreNotAssociatedWithPointFile = null;
        File pointsAreNotAssociatedWithProInfaFile = null;
        
        
        if (execution.hasVariable(PROCESS_CONTRACTS_RELOAD_BILLING)) {
            return;
        }

        List<File> attachaments = new ArrayList<>();

        if (contractsNotRegistered.size() > 0) {
            String text = contractsNotRegistered.stream().map(String::valueOf).collect(Collectors.joining("\n"));
            String header = "Os contratos abaixo, não estão cadastrados na base da [ Matrix ]\n";
            String body = header + "" + text;

            contractsNotRegisteredFile = new File("contratos_sem_cadastro.txt");
            this.writeFile(contractsNotRegisteredFile, body);
            attachaments.add(contractsNotRegisteredFile);
        }

        if (contractsAreNotAssociatedWithPoint.size() > 0) {
            String text = contractsAreNotAssociatedWithPoint.stream().map(String::valueOf).collect(Collectors.joining("\n"));
            String header = "Os contratos abaixo, não estão associados a nenhum ponto na base da [ Matrix ]\n";
            String body = header + "" + text;

            contractsAreNotAssociatedWithPointFile = new File("contratos_sem_pontos_associados.txt");
            this.writeFile(contractsAreNotAssociatedWithPointFile, body);
            attachaments.add(contractsAreNotAssociatedWithPointFile);
        }

        if (pointsAreNotAssociatedWithProInfa.size() > 0) {
            String text = pointsAreNotAssociatedWithProInfa.stream().map(String::valueOf).collect(Collectors.joining("\n"));
            String header = "Os pontos abaixo, não possui proinfa do mês vigente cadastrado na base da [ Matrix ]\n";
            String body = header + "" + text;

            pointsAreNotAssociatedWithProInfaFile = new File("pontos_sem_proinfa.txt");
            this.writeFile(pointsAreNotAssociatedWithProInfaFile, body);
            attachaments.add(pointsAreNotAssociatedWithProInfaFile);
        }

        if (attachaments.isEmpty()) {
            return;
        }

        try {
            File zip = Utils.zipFiles("informação", attachaments);
            Email email = emailFactory.createEmailTemplate(Template.TemplateBusiness.PROCESS_BILLING_ERROR);

            Map<String, File> att = new HashMap<>();
            att.put("informação.zip", zip);
            email.setAttachment(att);

            String emails = execution
                    .getEngineServices()
                    .getIdentityService()
                    .createUserQuery()
                    .memberOfGroup(GROUP_MANAGER_PORTAL)
                    .list()
                    .stream()
                    .map(u -> u.getEmail())
                    .collect(Collectors.joining(";"));

            email.setParameter(Constants.TEMPLATE_PARAM_USER_EMAIL, emails);

            email.setParameter(":1", String.valueOf(contractsNotRegistered.size()));
            email.setParameter(":2", String.valueOf(contractsAreNotAssociatedWithPoint.size()));
            email.setParameter(":3", String.valueOf(pointsAreNotAssociatedWithProInfa.size()));

            threadPoolEmail.submit(email);

        } catch (IOException ex) {
            Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            for (File attachament : attachaments) {
                try {
                    FileUtils.forceDelete(attachament);
                } catch (IOException ex) {
                    Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, "[delete]", ex);
                }
            }
        }
    }

    private void writeFile(File file, String body) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(body);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, "[writeFile]", ex);
        }
    }

    private void contractWithRateio(List<ContractDTO> contracts, List<Log> logs, DelegateExecution execution) {

        contracts
                .parallelStream()
                .filter(contract -> contract.getBFlRateio().equals(1L) && contract.getNCdContratoRateioControlador() != null)
                .collect(Collectors.groupingBy(ContractDTO::getNCdContratoRateioControlador))
                .forEach((contractParent, contractsSon) -> {

                    contractsSon.forEach(contract -> {
                        String point = null;
                        try {
                            ContractMtx contractMtx = this.contractMtxService.findByWbcContract(Long.parseLong(contract.getSNrContrato()));
                            point = contractMtx.getPoint();
                            contract.setMeansurementPoint(point);

                            if ((!contractMtx.isFlat() || !contractMtx.isConsumerUnit()) && Optional.ofNullable(point).isPresent()) {
                                this.meansurementPointMtxService
                                        .getByPoint(point)
                                        .checkProInfa();
                            }

                        } catch (EntityNotFoundException e) {
                            contractsNotRegistered.add(contract);
                        } catch (ContractNotAssociatedWithPointException ex) {
                            contractsAreNotAssociatedWithPoint.add(contract);
                        } catch (PointWithoutProinfaException ex) {
                            pointsAreNotAssociatedWithProInfa.add(point);
                        }
                    });

                    //Get Contract parent
                    Optional<ContractDTO> opt = contracts
                            .parallelStream()
                            .filter(c -> c.getNCdContrato().equals(contractParent))
                            .findFirst();

                    if (opt.isPresent()) {

                        List<ContractDTO> sons = contractsSon.stream().filter(t -> t.getMeansurementPoint() != null).collect(Collectors.toList());

                        sons.add(opt.get());

                        ContractDTO c = sons.stream().findFirst().get();

                        try {

                            Long contractWbc = Long.valueOf(opt.get().getSNrContrato());
                            List<ContractMtx> contractMtxs = this.contractMtxService.findAll(contractWbc).getContracts();

                            Map<String, Object> variables = new HashMap<>();
                            variables.put(PROCESS_INFORMATION_CONTRACTS_MATRIX, contractMtxs);
                            variables.put(PROCESS_INFORMATION_MONITOR_CLIENT, sons.stream().findFirst().get().getSNmEmpresaEpce());
                            variables.put(PROCESS_INFORMATION_CLIENT, sons.stream().findFirst().get().getSNmEmpresaEpce());

                            String processInstanceId = this.createAProcessForBilling(execution, sons, variables).getProcessInstanceId();
                            this.createMeansurementFile(processInstanceId, sons);
                        } catch (Exception ex) {
                            Logger.getLogger(BillingContractsTask.class.getName()).log(Level.SEVERE, "[Search contracts] -> contrato :" + c.getSNrContrato(), ex);
                        }
                    }

                });

    }

    private void contractWithoutRateio(List<ContractDTO> contracts, List<Log> logs, DelegateExecution execution) {

        contracts.stream()
                .filter(contract -> contract.getBFlRateio().equals(0L))
                .forEach(contract -> {

                    String point = null;

                    try {
                        ContractMtx contractMtx = this.contractMtxService.findByWbcContract(Long.parseLong(contract.getSNrContrato()));
                        point = contractMtx.getPoint();
                        contract.setMeansurementPoint(point);

                        if ((!contractMtx.isFlat() || !contractMtx.isConsumerUnit()) && Optional.ofNullable(point).isPresent()) {
                            this.meansurementPointMtxService
                                    .getByPoint(point)
                                    .checkProInfa();
                        }

                        if (execution.hasVariable(PROCESS_CONTRACTS_RELOAD_BILLING) || !this.hasMeansurementFile(contractMtx.getWbcContract(), point)) {
                            Map<String, Object> variables = new HashMap();
                            variables.put(PROCESS_INFORMATION_CONTRACTS_MATRIX, Arrays.asList(contractMtx));
                            String processInstanceId = this.createAProcessForBilling(execution, contract, variables).getProcessInstanceId();
                            this.createMeansurementFile(processInstanceId, contract);
                        }

                    } catch (EntityNotFoundException e) {
                        contractsNotRegistered.add(contract);
                    } catch (ContractNotAssociatedWithPointException ex) {
                        contractsAreNotAssociatedWithPoint.add(contract);
                    } catch (PointWithoutProinfaException ex) {
                        pointsAreNotAssociatedWithProInfa.add(point);
                    }
                });

    }

    private ProcessInstance createAProcessForBilling(DelegateExecution execution, ContractDTO contract, Map<String, Object> variables) {
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

        String contractsNumber = contracts.stream().map(c -> c.getSNrContrato()).collect(Collectors.joining(";"));

        ProcessInstance processInstance = execution.getEngineServices().getRuntimeService().startProcessInstanceByMessage(Constants.PROCESS_MEANSUREMENT_FILE_MESSAGE_EVENT, variables);

        if (Optional.ofNullable(processInstance).isPresent()) {
            variables.put(PROCESS_INFORMATION_MEANSUREMENT_POINT, pointers);
            variables.put(PROCESS_INFORMATION_NICKNAME, nickname);
            variables.put(Constants.PROCESS_INFORMATION_CNPJ, cnpjsString);
            variables.put(PROCESS_INFORMATION_CONTRACT_NUMBERS, contractsNumber);
            variables.put(PROCESS_INFORMATION_PROCESSO_ID, processInstance.getProcessInstanceId());

            if (execution.hasVariable(PROCESS_CONTRACTS_RELOAD_BILLING)) {
                execution.setVariable(PROCESS_NEW_INSTANCE_ID, processInstance.getProcessInstanceId());
            }

            execution.getEngineServices()
                    .getRuntimeService()
                    .setVariables(processInstance.getProcessInstanceId(), variables);

        } else {
            this.sendEmailError(execution, contracts.stream().findFirst().get().getSNrContrato());
            throw new NullPointerException();
        }

        return processInstance;
    }

    private void sendEmailError(DelegateExecution execution, String contract) {
        try {

            Email email = emailFactory.createEmailTemplate(Template.TemplateBusiness.PROCESS_ERROR);

            String emails = execution
                    .getEngineServices()
                    .getIdentityService()
                    .createUserQuery()
                    .memberOfGroup(GROUP_SUPPORT_TI)
                    .list()
                    .stream()
                    .map(u -> u.getEmail())
                    .collect(Collectors.joining(";"));

            email.setParameter(Constants.TEMPLATE_PARAM_USER_EMAIL, emails);
            email.setParameter(Constants.TEMPLATE_PARAM_CONTRACT, contract);

            threadPoolEmail.submit(email);

        } catch (Exception e) {
            Logger.getLogger(BillingContractsTask.class
                    .getName()).log(Level.SEVERE, "[sendEmailError]", e);
        }
    }

    private void createMeansurementFile(String processInstanceId, ContractDTO contract) {

        LocalDate monthBilling = LocalDate.now().minusMonths(1);

        Long month = Integer.valueOf(monthBilling.getMonthValue()).longValue();
        Long year = Integer.valueOf(monthBilling.getYear()).longValue();

        MeansurementFile meansurementFile = new MeansurementFile(contract, processInstanceId, contract.getMeansurementPoint());
        meansurementFile.setMonth(month);
        meansurementFile.setYear(year);
        meansurementFile.setNickname(contract.getSNmApelido());
        meansurementFile.setCompanyName(contract.getSNmFantasia());

        try {
            this.meansurementFileService.saveFile(meansurementFile);

        } catch (Exception e) {
            Logger.getLogger(BillingContractsTask.class
                    .getName()).log(Level.SEVERE, "[createMeansurementFile]", e);
        }

    }

    private void createMeansurementFile(String processInstanceId, List<ContractDTO> contracts) {

        //Remove contract parent
        contracts
                .stream()
                .filter(c -> c.getNCdContratoRateioControlador() != null)
                .filter(c -> !this.hasMeansurementFile(Long.parseLong(c.getSNrContrato()), c.getMeansurementPoint()))
                .forEach(contract -> {
                    this.createMeansurementFile(processInstanceId, contract);
                });
    }

    private boolean hasMeansurementFile(Long contract, String point) {

        LocalDate monthBilling = LocalDate.now().minusMonths(1);

        Long month = Integer.valueOf(monthBilling.getMonth().getValue()).longValue();
        Long year = Integer.valueOf(monthBilling.getYear()).longValue();

        return this.meansurementFileService.exists(contract, point, month, year);

    }

}
