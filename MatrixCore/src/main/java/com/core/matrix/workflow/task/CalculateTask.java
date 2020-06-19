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
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CLIENT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_MONITOR_CLIENT;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_NICKNAME;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.core.matrix.wbc.dto.ContractDTO;
import com.core.matrix.wbc.service.ContractService;
import com.core.matrix.wbc.service.EmpresaService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private EmpresaService empresaService;
    private LogService logService;

    private MeansurementFileResultService resultService;
    private ContractService contractWbcService;

    public CalculateTask() {

        synchronized (CalculateTask.context) {
            this.fileService = CalculateTask.context.getBean(MeansurementFileService.class);
            this.contractService = CalculateTask.context.getBean(ContractCompInformationService.class);
            this.empresaService = CalculateTask.context.getBean(EmpresaService.class);
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

        List<MeansurementFile> files = fileService.findByProcessInstanceId(de.getProcessInstanceId());

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

            String point = file.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim();

            String nickname = de.hasVariable(PROCESS_INFORMATION_NICKNAME) ? de.getVariable(PROCESS_INFORMATION_NICKNAME, String.class) : null;
            String name = de.hasVariable(PROCESS_INFORMATION_MONITOR_CLIENT) ? de.getVariable(PROCESS_INFORMATION_MONITOR_CLIENT, String.class) : null;

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

    private Double roundValue(Double value, int qtd) {
        return new BigDecimal(value).setScale(qtd, RoundingMode.HALF_EVEN).doubleValue();
    }

    public void calculateWithRateio(DelegateExecution de, List<MeansurementFile> files, List<ContractDTO> contracts) {

        try {
            List<MeansurementFileDetail> details = new ArrayList<>();

            //Join all datas
            files.stream().forEach(file -> {
                try {
                    details.addAll(this.getDetails(file, de));
                } catch (Exception ex) {
                    Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithRateio ]", ex);
                }
            });

            final List<ContractCompInformation> contractsInformations = contractService
                    .listByContract(files.get(0).getWbcContract());

            final List<MeansurementFileResult> results = new ArrayList<>();

            Long fileId = files
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new Exception("Não existe nenhum arquivo para ser processado"))
                    .getId();

            ContractCompInformation contractInformationParent = contractsInformations
                    .stream()
                    .filter(c -> c.getCodeContractApportionment() == null || c.getCodeContractApportionment().equals(0L))
                    .findFirst()
                    .orElseThrow(() -> new Exception("[Matrix] Informação do contrato [ PAI ] do rateio não foi encontrada!"));

            //final Double factorAtt = contractInformationParent.getFactorAttendanceCharge();
            //Contracts sons
            files.stream().forEach(file -> {

                try {
                    List<MeansurementFileDetail> filteredByPoint = details
                            .stream()
                            .filter(mpd -> mpd.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(file.getMeansurementPoint()))
                            .collect(Collectors.toList());

                    String pointTemp = filteredByPoint
                            .stream()
                            .map(MeansurementFileDetail::getMeansurementPoint)
                            .findFirst()
                            .orElse(file.getMeansurementPoint());

                    String point = pointTemp.replaceAll("\\((L|B)\\)", "").trim();

                    ContractCompInformation contractInformation = contractsInformations
                            .stream()
                            .filter(c -> Optional.ofNullable(c.getMeansurementPoint()).isPresent())
                            .filter(c -> c.getMeansurementPoint().equals(point))
                            .findFirst()
                            .orElseThrow(() -> new Exception("[Matrix] Informação do contrato não encontrada!"));

                    ContractWbcInformationDTO contractWbcInformation = this.contractWbcService
                            .getInformation(file.getYear(), file.getMonth(), file.getWbcContract())
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

                    Optional<ContractDTO> contractDTO = this.contractWbcService
                            .findAll(file.getWbcContract(), null)
                            .stream()
                            .filter(c -> c.getSNrContrato().equals(file.getWbcContract().toString()))
                            .findFirst();

                    String nickname = contractDTO.isPresent() ? contractDTO.get().getSNmApelido() : null;
                    String name = contractDTO.isPresent() ? contractDTO.get().getSNmEmpresaEpce() : null;

                    MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformation, de.getProcessInstanceId());

                    Double amountScde = isConsumerUnit ? 0D : (sum / 1000d);

                    fileResult.setAmountScde(amountScde);
                    fileResult.setMeansurementFileId(file.getId());

                    fileResult.setAmountBruto(this.roundValue((consumptionTotal / 100), 3));
                    fileResult.setAmountLiquido(this.roundValue((consumptionTotal / 100), 3));
                    fileResult.setWbcContract(Long.valueOf(contractWbcInformation.getNrContract()));
                    fileResult.setMeansurementPoint(point);
                    fileResult.setNickNameCompany(nickname);
                    fileResult.setNameCompany(name);
                    fileResult.setPercentLoss(percentLoss);
                    fileResult.setProinfa(proinfa);
                    fileResult.setFactorAtt(factorAtt);
                    fileResult.setContractParent(0L);
                    fileResult.setWbcSubmercado(contractInformation.getWbcSubmercado());

                    results.add(fileResult);
                    resultService.save(fileResult);

                } catch (Exception e) {
                    Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithRateio ]", e);
                    Log log = new Log();
                    log.setProcessInstanceId(de.getProcessInstanceId());
                    log.setMessage(MessageFormat.format("Erro ao calcular a medição referente ao ponto : {0} - \n Contrato : {1}", file.getMeansurementPoint(), file.getWbcContract()));
                    log.setMessageErrorApplication(e.getMessage());
                    logService.save(log);
                }
            });

            boolean hasUnitConsumer = contractsInformations
                    .stream()
                    .anyMatch(c -> Optional.ofNullable(c.getIsConsumerUnit()).isPresent() && c.getIsConsumerUnit().equals("1"));

            if (hasUnitConsumer) {

                MeansurementFile file = files.stream().findFirst().get();

                contractsInformations
                        .stream()
                        .filter(c -> Optional.ofNullable(c.getIsConsumerUnit()).isPresent() && c.getIsConsumerUnit().equals("1"))
                        .forEach(c -> {

                            ContractWbcInformationDTO contractWbcInformation = this.contractWbcService
                                    .getInformation(file.getYear(), file.getMonth(), c.getCodeWbcContract())
                                    .orElse(null);

                            Optional<ContractDTO> contractDTO = this.contractWbcService
                                    .findAll(file.getWbcContract(), null)
                                    .stream()
                                    .filter(x -> x.getSNrContrato().equals(c.getWbcContract().toString()))
                                    .findFirst();

                            String nickname = contractDTO.isPresent() ? contractDTO.get().getSNmApelido() : null;
                            String name = contractDTO.isPresent() ? contractDTO.get().getSNmEmpresaEpce() : null;

                            MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformation, de.getProcessInstanceId());

                            Double amountScde = 0D;
                            fileResult.setAmountScde(amountScde);
                            fileResult.setAmountBruto(0D);
                            fileResult.setAmountLiquido(0D);
                            fileResult.setWbcContract(Long.valueOf(contractWbcInformation.getNrContract()));
                            fileResult.setMeansurementPoint(null);
                            fileResult.setNickNameCompany(nickname);
                            fileResult.setNameCompany(name);
                            fileResult.setPercentLoss(c.getPercentOfLoss() / 100);
                            fileResult.setProinfa(0D);
                            fileResult.setFactorAtt(c.getFactorAttendanceCharge());
                            fileResult.setContractParent(0L);
                            fileResult.setWbcSubmercado(c.getWbcSubmercado());
                        });
            }

            MeansurementFile file = files.stream().findFirst().orElseThrow(() -> new Exception("Nenhum Arquivo!"));

            ContractWbcInformationDTO contractWbcInformation = this.contractWbcService
                    .getInformation(file.getYear(), file.getMonth(), contractInformationParent.getWbcContract())
                    .orElseThrow(() -> new Exception("[WBC] -> Não foi possivel carregar as informações complementares!\n Referente as informações de [CE_SAZONALIZACAO] e [CE_REGRA_OPCIONALIDADE] "));

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

        } catch (Exception e) {
            Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithRateio ]", e);

            Log log = new Log();
            log.setProcessInstanceId(de.getProcessInstanceId());
            log.setMessage(MessageFormat.format("Erro ao calcular a medição de contratos com rateio processo : {0}", de.getProcessInstanceId()));
            log.setMessageErrorApplication(e.getMessage());

            logService.save(log);

        }

    }

    private Double solicitadoLiquido(Double consumptionTotal, ContractWbcInformationDTO contractWbcInformationDTO) {

        Logger.getLogger(CalculateTask.class.getName()).log(Level.INFO, "consumptionTotal -> " + consumptionTotal);
        Logger.getLogger(CalculateTask.class.getName()).log(Level.INFO, "getNrQtd    ->" + contractWbcInformationDTO.getNrQtd());
        Logger.getLogger(CalculateTask.class.getName()).log(Level.INFO, "getNrQtdMin ->" + contractWbcInformationDTO.getNrQtdMin());
        Logger.getLogger(CalculateTask.class.getName()).log(Level.INFO, "getNrQtdMax ->" + contractWbcInformationDTO.getNrQtdMax());

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

    private Double getSumConsumptionActive(List<MeansurementFileDetail> details) {
        return details.stream()
                .map(d -> new BigDecimal(d.getConsumptionActive()).setScale(6, RoundingMode.HALF_EVEN))
                .reduce(new BigDecimal(0D), BigDecimal::add).doubleValue();
    }

    private Double getProinfa(MeansurementFile file, List<ContractProInfa> proInfas) throws Exception {

        Long monthRef = file.getMonth();
        Long yearRef = file.getYear();

        ContractProInfa contractProInfa = proInfas
                .stream()
                .filter(infa -> infa.getMonth().equals(monthRef) && infa.getYear().equals(yearRef))
                .findFirst()
                .orElseThrow(() -> new Exception("Não foi encontrado nenhum proinfa cadastrada para esse contrato!\n Mês/Ano refência: " + monthRef + "/" + yearRef));

        return contractProInfa.getProinfa();

    }

}
