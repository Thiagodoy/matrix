/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.ConsumptionResult;
import com.core.matrix.model.ContractCompInformation;
import com.core.matrix.model.ContractProInfa;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.service.ContractCompInformationService;
import com.core.matrix.service.LogService;

import com.core.matrix.service.MeansurementFileResultService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.Constants;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.service.ContractService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author thiag
 */
public class CalculateTask implements Task {

    private static ApplicationContext context;

    private MeansurementFileService fileService;
    private ContractCompInformationService contractService;
    private LogService logService;

    private MeansurementFileResultService resultService;
    private ContractService contractWbcService;
    private List<ContractWbcInformationDTO> contractWbcInformationDTOs;
    private List<ContractDTO> contractDTOs;

    public CalculateTask() {

        synchronized (CalculateTask.context) {
            this.fileService = CalculateTask.context.getBean(MeansurementFileService.class);
            this.contractService = CalculateTask.context.getBean(ContractCompInformationService.class);
            this.resultService = CalculateTask.context.getBean(MeansurementFileResultService.class);
            this.contractWbcService = CalculateTask.context.getBean(ContractService.class);
            this.logService = CalculateTask.context.getBean(LogService.class);

        }

    }

    public CalculateTask(ApplicationContext context) {
        CalculateTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) {

        long start = System.currentTimeMillis();
        List<MeansurementFile> files = fileService.findByProcessInstanceId(de.getProcessInstanceId());

        loggerPerformance(start, "Carregando os arquivos");

        try {

            final List<ContractDTO> contracts = (List<ContractDTO>) de.getVariable(Constants.LIST_CONTRACTS_FOR_BILLING, Object.class);

            if (contracts.get(0).getBFlRateio().equals(1L)) {
                this.calculateWithRateio(de, files, contracts);
            } else {
                this.calculateWithoutRateio(de, files.get(0), contracts);
            }

        } catch (Exception e) {
            Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ execute ]", e);
            Log log = new Log();
            log.setProcessInstanceId(de.getProcessInstanceId());
            log.setMessage("Erro ao selecionar processo de rateio ou sem rateio.");
            log.setMessageErrorApplication(e.getMessage());
            logService.save(log);
        }
    }

    public void calculateWithoutRateio(DelegateExecution de, MeansurementFile file, List<ContractDTO> contracts) {

        try {

            List<MeansurementFileDetail> details = this
                    .getDetails(file, de)
                    .stream()
                    .filter(detail -> detail.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(file.getMeansurementPoint()))
                    .collect(Collectors.toList());

            ContractCompInformation compInformation = contractService
                    .findByWbcContractAndMeansurementPoint(file.getWbcContract(), file.getMeansurementPoint())
                    .orElseThrow(() -> new Exception("[Matrix] -> Não foi possivel encontrar as informações complementares do contrato!"));

            ContractWbcInformationDTO contractWbcInformationDTO = this.contractWbcService
                    .getInformation(file.getYear(), file.getMonth(), file.getWbcContract())
                    .orElseThrow(() -> new Exception("[WBC] -> Não foi possivel carregar as informações complementares!\n Referente as informações de [CE_SAZONALIZACAO] e [CE_REGRA_OPCIONALIDADE] "));

            final ConsumptionResult result = new ConsumptionResult();
            result.setMeansurementPoint(file.getMeansurementPoint());

            final double factorAtt = compInformation.getFactorAttendanceCharge() / 100;
            final double percentLoss = compInformation.getPercentOfLoss() / 100;
            final double proinfa = this.getProinfa(file, compInformation.getProinfas());
            final double sum = this.getSumConsumptionActive(details);

            double consumptionTotal = ((sum / 1000) + ((sum / 1000) * percentLoss) - proinfa) * factorAtt;

            // String point = file.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim();
            String nickname = compInformation.getNickname();
            String name = compInformation.getNameCompany();

            MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformationDTO, de.getProcessInstanceId());
            fileResult.setAmountScde(this.roundValue((sum / 1000), 6));

            fileResult.setMeansurementFileId(file.getId());
            Double consumptionLiquid = solicitadoLiquido(consumptionTotal, contractWbcInformationDTO);
            fileResult.setAmountLiquido(this.roundValue(consumptionLiquid, 3));
            fileResult.setAmountBruto(this.roundValue(consumptionTotal, 3));
            fileResult.setWbcContract(Long.valueOf(contractWbcInformationDTO.getNrContract()));
            fileResult.setMeansurementPoint(file.getMeansurementPoint());
            fileResult.setNickNameCompany(nickname);
            fileResult.setNameCompany(name);
            fileResult.setPercentLoss(percentLoss);
            fileResult.setProinfa(proinfa);
            fileResult.setFactorAtt(factorAtt);
            fileResult.setWbcSubmercado(compInformation.getWbcSubmercado());
            fileResult.setWbcPerfilCCEE(consultaPerfilCCEE(contracts, Long.valueOf(contractWbcInformationDTO.getNrContract())));

            resultService.save(fileResult);

        } catch (Exception e) {
            Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithoutRateio ]", e);
            Log log = new Log();
            log.setProcessInstanceId(de.getProcessInstanceId());
            log.setMessage(MessageFormat.format("Erro ao calcular a medição referente ao ponto : {0} - \n Contrato : {1}", file.getMeansurementPoint(), file.getWbcContract()));
            log.setMessageErrorApplication(e.getMessage());
            logService.save(log);

        }

    }

    private synchronized Double roundValue(Double value, int qtd) {
        return new BigDecimal(value).setScale(qtd, RoundingMode.HALF_EVEN).doubleValue();
    }

    public void calculateWithRateio(DelegateExecution de, List<MeansurementFile> files, List<ContractDTO> contracts) {

        try {
            List<MeansurementFileDetail> details = new CopyOnWriteArrayList(new ArrayList<>());

            long start = System.currentTimeMillis();
            //Join all datas
            files.stream().forEach(file -> {
                try {
                    details.addAll(this.getDetails(file, de));
                } catch (Exception ex) {
                    Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithRateio ]", ex);
                }
            });

            loggerPerformance(start, "Unindo os detalhes");

            MeansurementFile fileM = files.stream().findFirst().orElseThrow(() -> new Exception("Não existe nenhum arquivo para ser processado"));

            start = System.currentTimeMillis();
            final List<ContractCompInformation> contractsInformations = new CopyOnWriteArrayList<ContractCompInformation>(contractService.listByContract(files.get(0).getWbcContract()));
            loggerPerformance(start, "Carregando as informações complementares");

            List<Long> contractsId = contractsInformations.stream().mapToLong(ContractCompInformation::getWbcContract).boxed().collect(Collectors.toList());
            contractWbcInformationDTOs = new CopyOnWriteArrayList<ContractWbcInformationDTO>(this.getWbcInformation(fileM.getYear(), fileM.getMonth(), contractsId));

            start = System.currentTimeMillis();
            contractDTOs = new CopyOnWriteArrayList<ContractDTO>(this.contractWbcService.findAll(fileM.getWbcContract(), null));
            loggerPerformance(start, "Carregando contratos WBC");
            final List<MeansurementFileResult> results = Collections.synchronizedList(new ArrayList<>());

            Long fileId = fileM.getId();

            final ContractCompInformation contractInformationParent = this.getContractInformationParent(contractsInformations);

            //final Double factorAtt = contractInformationParent.getFactorAttendanceCharge();
            //Contracts sons
            start = System.currentTimeMillis();
            files.parallelStream().forEach(file -> {

                try {
                    final List<MeansurementFileDetail> filteredByPoint = details
                            //                            .stream()
                            .parallelStream()
                            .filter(mpd -> mpd.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(file.getMeansurementPoint()))
                            .collect(Collectors.toList());

//                    String pointTemp = filteredByPoint
//                            .stream()
//                            .map(MeansurementFileDetail::getMeansurementPoint)
//                            .findFirst()
//                            .orElse(file.getMeansurementPoint());
                    String point = file.getMeansurementPoint();//pointTemp.replaceAll("\\((L|B)\\)", "").trim();

                    final ContractCompInformation contractInformation = contractsInformations
                            .stream()
                            // .filter(c -> Optional.ofNullable(c.getMeansurementPoint()).isPresent())
                            .filter(c -> c.getMeansurementPoint().equals(point))
                            .findFirst()
                            .orElseThrow(() -> new Exception("[Matrix] Informação complementar do contrato não encontrada!"));

                    final ContractWbcInformationDTO contractWbcInformation = contractWbcInformationDTOs
                            .stream()
                            .filter(c-> c.getNrContract().equals(String.valueOf(file.getWbcContract())))
                            .findFirst()
                            .orElseThrow(() -> new Exception("[WBC] -> Não foi possivel carregar as informações complementares!\n Referente as informações de [CE_SAZONALIZACAO] e [CE_REGRA_OPCIONALIDADE] "));

                    /**
                     * Set result to zero when the contract is consumer unit
                     */
                    boolean isConsumerUnit = contractInformation.getIsConsumerUnit().equals("1");

                    final Double factorAtt = contractInformation.getFactorAttendanceCharge();
                    final double percentLoss = contractInformation.getPercentOfLoss() / 100;
                    final double proinfa = isConsumerUnit ? 0d : this.getProinfa(file, contractInformation.getProinfas());
                    final Double sum = isConsumerUnit ? 0d : this.getSumConsumptionActive(filteredByPoint);

                    final ConsumptionResult result = new ConsumptionResult();
                    result.setMeansurementPoint(file.getMeansurementPoint());

                    double consumptionTotal = ((sum / 1000) + ((sum / 1000) * percentLoss) - proinfa) * factorAtt;

                    Optional<ContractDTO> contractDTO = contractDTOs
                            .stream()
                            .filter(c -> c.getSNrContrato().equals(file.getWbcContract().toString()))
                            .findFirst();

                    String nickname = contractInformation.getNickname();
                    String name = contractInformation.getNameCompany();

                    MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformation, de.getProcessInstanceId());

                    Double amountScde = isConsumerUnit ? 0D : (sum / 1000d);

                    fileResult.setAmountScde(amountScde);
                    fileResult.setMeansurementFileId(file.getId());

                    final Double amount = this.roundValue((consumptionTotal / 100), 3);

                    fileResult.setAmountBruto(amount);
                    fileResult.setAmountLiquido(amount);
                    fileResult.setWbcContract(Long.valueOf(contractWbcInformation.getNrContract()));
                    fileResult.setMeansurementPoint(point);
                    fileResult.setNickNameCompany(nickname);
                    fileResult.setNameCompany(name);
                    fileResult.setPercentLoss(percentLoss);
                    fileResult.setProinfa(proinfa);
                    fileResult.setFactorAtt(factorAtt);
                    fileResult.setContractParent(0L);
                    fileResult.setWbcSubmercado(contractInformation.getWbcSubmercado());

                    Long perfil = contractDTO.isPresent() ? contractDTO.get().getNCdPerfilCCEE() : 0;
                    fileResult.setWbcPerfilCCEE(perfil.intValue());

                    results.add(fileResult);
                    //resultService.save(fileResult);

                } catch (Exception e) {
                    Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithRateio ]", e);
                    Log log = new Log();
                    log.setProcessInstanceId(de.getProcessInstanceId());
                    log.setMessage(MessageFormat.format("Erro ao calcular a medição referente ao ponto : {0} - \n Contrato : {1}", file.getMeansurementPoint(), file.getWbcContract()));
                    log.setMessageErrorApplication(e.getMessage());
                    logService.save(log);
                }
            });

            loggerPerformance(start, "Gerando os resultados");

            if (!results.isEmpty()) {
                start = System.currentTimeMillis();
                resultService.saveAll(results);
                loggerPerformance(start, "Salvando os resultados");
            }

            boolean hasUnitConsumer = this.hasUnitConsumer(contractsInformations);

            if (hasUnitConsumer) {
                start = System.currentTimeMillis();
                this.mountFakeResultToContractIsUnitConsumer(fileM, contractsInformations, de);
                loggerPerformance(start, "Salvando o resultado para unidades de medição");
            }

            //ContractWbcInformationDTO contractWbcInformation = this.getWbcInformation(fileM.getYear(), fileM.getMonth(), contractInformationParent.getWbcContract());
            start = System.currentTimeMillis();
            this.mountResultParent(de, contractInformationParent, fileId, results, contracts);
            loggerPerformance(start, "Salvando o resultado para ocontratoPai");

            /**
             * Set result to zero when the contract is consumer unit
             */
//            boolean isConsumerUnit = contractInformationParent.getIsConsumerUnit().equals("1");
//
//            Double sum = isConsumerUnit ? 0D : results.stream().mapToDouble(MeansurementFileResult::getAmountBruto).reduce(0d, Double::sum);
//            Double sumScde = isConsumerUnit ? 0D : results.stream().mapToDouble(MeansurementFileResult::getAmountScde).reduce(0d, Double::sum);
//
//            String name = results.stream().findFirst().get().getNameCompany();
//
//            MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformation, de.getProcessInstanceId());
//
//            Double factorAttParent = Optional.ofNullable(contractInformationParent.getFactorAttendanceCharge()).isPresent()
//                    ? contractInformationParent.getFactorAttendanceCharge() / 100
//                    : 0;
//
//            fileResult.setFactorAtt(factorAttParent);
//            fileResult.setAmountBruto(this.roundValue(sum, 3));
//            fileResult.setAmountScde(sumScde);
//            Double consumptionLiquid = isConsumerUnit ? 0D : solicitadoLiquido(this.roundValue(sum, 3), contractWbcInformation);
//            fileResult.setAmountLiquido(consumptionLiquid);
//            fileResult.setMeansurementFileId(fileId);
//            fileResult.setWbcContract(Long.valueOf(contractWbcInformation.getNrContract()));
//            fileResult.setContractParent(1L);
//            fileResult.setNameCompany(name);
//            fileResult.setWbcSubmercado(contractInformationParent.getWbcSubmercado());
//            fileResult.setWbcPerfilCCEE(consultaPerfilCCEE(contracts, contractInformationParent.getWbcContract()));
//
//            resultService.save(fileResult);
        } catch (Exception e) {
            Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithRateio ]", e);

            Log log = new Log();
            log.setProcessInstanceId(de.getProcessInstanceId());
            log.setMessage(MessageFormat.format("Erro ao calcular a medição de contratos com rateio processo : {0}", de.getProcessInstanceId()));
            log.setMessageErrorApplication(e.getMessage());

            logService.save(log);

        }

    }

    private void mountResultParent(DelegateExecution de, ContractCompInformation contractInformationParent, Long fileId, List<MeansurementFileResult> results, List<ContractDTO> contracts) throws Exception {

        ContractWbcInformationDTO contractWbcInformation =  contractWbcInformationDTOs
                            .stream()
                            .filter(c-> c.getNrContract().equals(String.valueOf(contractInformationParent.getWbcContract())))
                            .findFirst()
                            .orElseThrow(() -> new Exception("[WBC] -> Não foi possivel carregar as informações complementares!\n Referente as informações de [CE_SAZONALIZACAO] e [CE_REGRA_OPCIONALIDADE] "));
                
                
                //this.getWbcInformation(fileM.getYear(), fileM.getMonth(), contractInformationParent.getWbcContract());

        /**
         * Set result to zero when the contract is consumer unit
         */
        boolean isConsumerUnit = contractInformationParent.getIsConsumerUnit().equals("1");

        Double sum = isConsumerUnit ? 0D : results.stream().mapToDouble(MeansurementFileResult::getAmountBruto).reduce(0d, Double::sum);
        Double sumScde = isConsumerUnit ? 0D : results.stream().mapToDouble(MeansurementFileResult::getAmountScde).reduce(0d, Double::sum);

        String name = results.stream().findFirst().get().getNameCompany();

        MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformation, de.getProcessInstanceId());

        Double factorAttParent = Optional.ofNullable(contractInformationParent.getFactorAttendanceCharge()).isPresent()
                ? contractInformationParent.getFactorAttendanceCharge() / 100
                : 0;

        fileResult.setFactorAtt(factorAttParent);
        fileResult.setAmountBruto(this.roundValue(sum, 3));
        fileResult.setAmountScde(sumScde);
        Double consumptionLiquid = isConsumerUnit ? 0D : solicitadoLiquido(this.roundValue(sum, 3), contractWbcInformation);
        fileResult.setAmountLiquido(consumptionLiquid);
        fileResult.setMeansurementFileId(fileId);
        fileResult.setWbcContract(Long.valueOf(contractWbcInformation.getNrContract()));
        fileResult.setContractParent(1L);
        fileResult.setNameCompany(name);
        fileResult.setWbcSubmercado(contractInformationParent.getWbcSubmercado());
        fileResult.setWbcPerfilCCEE(consultaPerfilCCEE(contracts, contractInformationParent.getWbcContract()));

        resultService.save(fileResult);

    }

    private ContractCompInformation getContractInformationParent(List<ContractCompInformation> contractsInformations) throws Exception {
        return contractsInformations
                .stream()
                .filter(c -> c.getCodeContractApportionment() == null || c.getCodeContractApportionment().equals(0L))
                .findFirst()
                .orElseThrow(() -> new Exception("[Matrix] Informação do contrato [ PAI ] do rateio não foi encontrada!"));
    }

    private void mountFakeResultToContractIsUnitConsumer(MeansurementFile file, List<ContractCompInformation> contractsInformations, DelegateExecution de) {

        contractsInformations
                .stream()
                .filter(c -> Optional.ofNullable(c.getIsConsumerUnit()).isPresent() && c.getIsConsumerUnit().equals("1"))
                .parallel()
                .forEach(c -> {

                    ContractWbcInformationDTO contractWbcInformation = contractWbcInformationDTOs
                            .stream()
                            .filter( cc-> cc.getNrContract().equals(String.valueOf(c.getCodeWbcContract())))
                            .findFirst()
                            .orElse(null);

                    Optional<ContractDTO> contractDTO = contractDTOs
                            .stream()
                            .filter(x -> x.getSNrContrato().equals(c.getWbcContract().toString()))
                            .findFirst();

                    String nickname = c.getNickname();
                    String name = c.getNameCompany();

                    MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformation, de.getProcessInstanceId());

                    Double amountScde = 0D;
                    fileResult.setAmountScde(amountScde);
                    fileResult.setAmountBruto(0D);
                    fileResult.setAmountLiquido(0D);
                    fileResult.setWbcContract(c.getWbcContract());
                    fileResult.setMeansurementPoint(null);
                    fileResult.setNickNameCompany(nickname);
                    fileResult.setNameCompany(name);
                    fileResult.setPercentLoss(c.getPercentOfLoss() / 100);
                    fileResult.setProinfa(0D);
                    fileResult.setFactorAtt(c.getFactorAttendanceCharge());
                    fileResult.setContractParent(0L);
                    fileResult.setWbcSubmercado(c.getWbcSubmercado());
                    Long perfil = contractDTO.isPresent() ? contractDTO.get().getNCdPerfilCCEE() : 0;
                    fileResult.setWbcPerfilCCEE(perfil.intValue());
                    fileResult.setMeansurementFileId(file.getId());

                    synchronized (this.resultService) {
                        resultService.save(fileResult);
                    }

                });

    }

    private boolean hasUnitConsumer(List<ContractCompInformation> contractsInformations) {
        return contractsInformations
                .stream()
                .anyMatch(c -> Optional.ofNullable(c.getIsConsumerUnit()).isPresent() && c.getIsConsumerUnit().equals("1"));
    }

    private List<ContractWbcInformationDTO> getWbcInformation(Long year, Long month, List<Long> contract) throws Exception {

        return this.contractWbcService.getInformation(year, month, contract);
        //.orElseThrow(() -> new Exception("[WBC] -> Não foi possivel carregar as informações complementares!\n Referente as informações de [CE_SAZONALIZACAO] e [CE_REGRA_OPCIONALIDADE] "));
        //return contractWbcInformation;

    }

//    private synchronized ContractWbcInformationDTO getWbcInformation(MeansurementFile file) throws Exception {
//        return this.getWbcInformation(file.getYear(), file.getMonth(), file.getWbcContract());
//    }
    private Double solicitadoLiquido(Double consumptionTotal, ContractWbcInformationDTO contractWbcInformationDTO) {

        BigDecimal consumptionTotalArredondado = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

        BigDecimal solicitadoLiquido = new BigDecimal(contractWbcInformationDTO.getNrQtdMin()).setScale(3, RoundingMode.HALF_EVEN);

        if (consumptionTotalArredondado.doubleValue() < contractWbcInformationDTO.getNrQtd() && consumptionTotalArredondado.doubleValue() > contractWbcInformationDTO.getNrQtdMin()) {

            solicitadoLiquido = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

        } else if (consumptionTotalArredondado.doubleValue() > contractWbcInformationDTO.getNrQtd() && consumptionTotalArredondado.doubleValue() < contractWbcInformationDTO.getNrQtdMax()) {

            solicitadoLiquido = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

        } else if (consumptionTotalArredondado.doubleValue() > contractWbcInformationDTO.getNrQtd() && consumptionTotalArredondado.doubleValue() > contractWbcInformationDTO.getNrQtdMax()) {

            solicitadoLiquido = new BigDecimal(contractWbcInformationDTO.getNrQtdMax()).setScale(3, RoundingMode.HALF_EVEN);

        }

        return solicitadoLiquido.doubleValue();

    }

    public int consultaPerfilCCEE(List<ContractDTO> contracts, Long numeroContrato) {
        return contracts
                .stream()
                .filter(c -> Long.valueOf(c.getSNrContrato()).equals(numeroContrato))
                .mapToInt(c -> c.getNCdPerfilCCEE().intValue())
                .findFirst()
                .orElse(0);
    }

    private synchronized Double getSumConsumptionActive(List<MeansurementFileDetail> details) {
        return details.stream()
                .map(d -> new BigDecimal(d.getConsumptionActive()).setScale(6, RoundingMode.HALF_EVEN))
                .reduce(new BigDecimal(0D), BigDecimal::add).doubleValue();
    }

    private synchronized Double getProinfa(MeansurementFile file, List<ContractProInfa> proInfas) throws Exception {

        Long monthRef = file.getMonth();
        Long yearRef = file.getYear();

        ContractProInfa contractProInfa = proInfas
                .stream()
                .filter(infa -> infa.getMonth().equals(monthRef) && infa.getYear().equals(yearRef))
                .findFirst()
                .orElseThrow(() -> new Exception("Não foi encontrado nenhum proinfa cadastrada para esse contrato!\n Mês/Ano refência: " + monthRef + "/" + yearRef));

        return contractProInfa.getProinfa();

    }

    private void loggerPerformance(long start, String fase) {
        Logger.getLogger(FileValidationTask.class.getName()).log(Level.INFO, MessageFormat.format("[loggerPerformance] -> etapa: {0} tempo : {1} min", fase, (System.currentTimeMillis() - start) / 60000D));
    }

}
