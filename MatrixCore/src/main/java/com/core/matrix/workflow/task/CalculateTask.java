/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.ConsumptionResult;
import com.core.matrix.dto.ContractInformationDTO;
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
import static com.core.matrix.utils.Constants.FILE_MEANSUREMENT_ID;
import static com.core.matrix.utils.Constants.RESPONSE_RESULT;
import static com.core.matrix.utils.Constants.RESPONSE_RESULT_MESSAGE;
import com.core.matrix.wbc.dto.ContractWbcInformationDTO;
import com.core.matrix.wbc.dto.CompanyDTO;
import com.core.matrix.wbc.service.ContractService;
import com.core.matrix.wbc.service.EmpresaService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

        if (files.size() > 1) {
            this.calculateWithRateio(de, files);
        } else {
            this.calculateWithoutRateio(de, files.get(0));
        }

    }

    public void calculateWithoutRateio(DelegateExecution de, MeansurementFile file) {

        try {

            List<MeansurementFileDetail> details = this
                    .getDetails(file, de)
                    .stream()
                    .filter(detail -> detail.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(file.getMeansurementPoint()))
                    .collect(Collectors.toList());

            ContractCompInformation compInformation = contractService
                    .findByMeansurementPoint(file.getMeansurementPoint())
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

            // BigDecimal consumptionTotalArredondado = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);
            //Double solicitadoLiquido = solicitadoLiquido(consumptionTotal, contractWbcInformationDTO);
            String point = file.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim();

            Optional<CompanyDTO> optEmp = this.empresaService.listByPoint(point);
            String nickname = optEmp.isPresent() ? optEmp.get().getSNmApelido() : "";
            String name = optEmp.isPresent() ? optEmp.get().getSNmEmpresa() : "";

            MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformationDTO, de.getProcessInstanceId());
            fileResult.setAmountScde(this.roundValue((sum / 1000), 6));            
            
            fileResult.setMeansurementFileId(file.getId());
            Double consumptionLiquid = solicitadoLiquido(consumptionTotal, contractWbcInformationDTO);
            fileResult.setAmountLiquido(this.roundValue(consumptionLiquid, 6));
            fileResult.setAmountBruto(this.roundValue(consumptionTotal, 6));
            fileResult.setWbcContract(Long.valueOf(contractWbcInformationDTO.getNrContract()));
            fileResult.setMeansurementPoint(file.getMeansurementPoint());
            fileResult.setNickNameCompany(nickname);
            fileResult.setNameCompany(name);
            fileResult.setPercentLoss(percentLoss);
            fileResult.setProinfa(proinfa);
            fileResult.setFactorAtt(factorAtt);

            resultService.save(fileResult);

        } catch (Exception e) {
            Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithoutRateio ]", e);
            Log log = new Log();
            log.setActIdProcesso(de.getProcessInstanceId());
            log.setMessage(MessageFormat.format("Erro ao calcular a medição referente ao ponto : {0} - \n Contrato : {1}", file.getMeansurementPoint(), file.getWbcContract()));
            log.setMessageErrorApplication(e.getMessage());
            logService.save(log);

        }

    }

    private Double roundValue(Double value, int qtd) {

        return new BigDecimal(value).setScale(qtd, RoundingMode.HALF_EVEN).doubleValue();

    }

    public void calculateWithRateio(DelegateExecution de, List<MeansurementFile> files) {

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

            final List<ContractCompInformation> contractsInformations = contractService.listByContract(files.get(0).getWbcContract());

            final List<MeansurementFileResult> results = new ArrayList<>();

            Long fileId = files
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new Exception("Não existe nenhum arquivo para ser processado"))
                    .getId();

            ContractCompInformation contractInformationParent = contractsInformations
                    .stream()
                    .filter(c -> c.getCodeContractApportionment() == null)
                    .findFirst()
                    .orElseThrow(() -> new Exception("[Matrix] Informação do contrato não encontrada!"));

            final Double factorAtt = contractInformationParent.getFactorAttendanceCharge();

            //Contracts sons
            files.stream().forEach(file -> {

                try {
                    List<MeansurementFileDetail> filteredByPoint = details
                            .stream()
                            .filter(mpd -> mpd.getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim().equals(file.getMeansurementPoint()))
                            .collect(Collectors.toList());

                    String point = filteredByPoint.stream().findFirst().get().getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim();

                    ContractCompInformation contractInformation = contractsInformations
                            .stream()
                            .filter(c -> c.getMeansurementPoint().equals(point))
                            .findFirst()
                            .orElseThrow(() -> new Exception("[Matrix] Informação do contrato não encontrada!"));

                    ContractWbcInformationDTO contractWbcInformation = this.contractWbcService
                            .getInformation(file.getYear(), file.getMonth(), file.getWbcContract())
                            .orElseThrow(() -> new Exception("[WBC] -> Não foi possivel carregar as informações complementares!\n Referente as informações de [CE_SAZONALIZACAO] e [CE_REGRA_OPCIONALIDADE] "));

                    final double percentLoss = contractInformation.getPercentOfLoss() / 100;
                    final double proinfa = this.getProinfa(file, contractInformation.getProinfas());
                    final Double sum = this.getSumConsumptionActive(filteredByPoint);

                    Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ getSumConsumptionActive ] -> " + sum.toString());
                    Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ getSumConsumptionActive (sum / 1000d) ] -> " + (sum / 1000d));

                    final ConsumptionResult result = new ConsumptionResult();
                    result.setMeansurementPoint(file.getMeansurementPoint());

                    double consumptionTotal = ((sum / 1000) + ((sum / 1000) * percentLoss) - proinfa) * factorAtt;
                    // double consumptionTotal = (((sum / 1000) + (sum / 1000) * percentLoss) - proinfa);

                    Optional<CompanyDTO> optEmp = this.empresaService.listByPoint(point);
                    String nickname = optEmp.isPresent() ? optEmp.get().getSNmApelido() : "";
                    String name = optEmp.isPresent() ? optEmp.get().getSNmEmpresa() : "";

                    MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformation, de.getProcessInstanceId());

                    fileResult.setAmountScde((sum / 1000d));
                    fileResult.setMeansurementFileId(file.getId());
                    //Double consumptionLiquid = solicitadoLiquido(consumptionTotal, contractWbcInformation);
                    fileResult.setAmountBruto(consumptionTotal / 100);
                    fileResult.setAmountLiquido(consumptionTotal / 100);
                    fileResult.setWbcContract(Long.valueOf(contractWbcInformation.getNrContract()));
                    fileResult.setMeansurementPoint(point);
                    fileResult.setNickNameCompany(nickname);
                    fileResult.setNameCompany(name);
                    fileResult.setPercentLoss(percentLoss);
                    fileResult.setProinfa(proinfa);
                    fileResult.setContractParent(0L);

                    results.add(fileResult);
                    resultService.save(fileResult);

                } catch (Exception e) {
                    Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithRateio ]", e);
                    Log log = new Log();
                    log.setActIdProcesso(de.getProcessInstanceId());
                    log.setMessage(MessageFormat.format("Erro ao calcular a medição referente ao ponto : {0} - \n Contrato : {1}", file.getMeansurementPoint(), file.getWbcContract()));
                    log.setMessageErrorApplication(e.getLocalizedMessage());
                    logService.save(log);
                }
            });

            MeansurementFile file = files.stream().findFirst().orElseThrow(() -> new Exception("Nenhum Arquivo!"));

            ContractWbcInformationDTO contractWbcInformation = this.contractWbcService
                    .getInformation(file.getYear(), file.getMonth(), contractInformationParent.getWbcContract())
                    .orElseThrow(() -> new Exception("[WBC] -> Não foi possivel carregar as informações complementares!\n Referente as informações de [CE_SAZONALIZACAO] e [CE_REGRA_OPCIONALIDADE] "));

            Double sum = results.stream().mapToDouble(MeansurementFileResult::getAmountBruto).reduce(0d, Double::sum);
            Double sumScde = results.stream().mapToDouble(MeansurementFileResult::getAmountScde).reduce(0d, Double::sum);

            String name = results.stream().findFirst().get().getNameCompany();

            MeansurementFileResult fileResult = new MeansurementFileResult(contractWbcInformation, de.getProcessInstanceId());
            fileResult.setFactorAtt(contractInformationParent.getFactorAttendanceCharge());
            fileResult.setAmountBruto(sum);
            fileResult.setAmountScde(sumScde);
            fileResult.setMeansurementFileId(fileId);
            fileResult.setWbcContract(contractInformationParent.getCodeWbcContract());
            fileResult.setContractParent(1L);
            fileResult.setNameCompany(name);

            resultService.save(fileResult);

        } catch (Exception e) {
            Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[ calculateWithRateio ]", e);

            Log log = new Log();
            log.setActIdProcesso(de.getProcessInstanceId());
            log.setMessage(MessageFormat.format("Erro ao calcular a medição de contratos com rateio processo : {0}", de.getProcessInstanceId()));
            log.setMessageErrorApplication(e.getMessage());

            logService.save(log);

        }

    }

    private Double solicitadoLiquido(Double consumptionTotal, ContractWbcInformationDTO contractWbcInformationDTO) {

        BigDecimal consumptionTotalArredondado = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

        BigDecimal solicitadoLiquido = new BigDecimal(0).setScale(3, RoundingMode.HALF_EVEN);

        if (consumptionTotalArredondado.doubleValue() < contractWbcInformationDTO.getNrQtd() && consumptionTotalArredondado.doubleValue() > contractWbcInformationDTO.getNrQtdMin()) {

            solicitadoLiquido = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

        } else if (consumptionTotalArredondado.doubleValue() > contractWbcInformationDTO.getNrQtd() && consumptionTotalArredondado.doubleValue() < contractWbcInformationDTO.getNrQtdMax()) {

            solicitadoLiquido = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

        } else if (consumptionTotalArredondado.doubleValue() > contractWbcInformationDTO.getNrQtd() && consumptionTotalArredondado.doubleValue() > contractWbcInformationDTO.getNrQtdMax()) {

            solicitadoLiquido = new BigDecimal(contractWbcInformationDTO.getNrQtdMax()).setScale(3, RoundingMode.HALF_EVEN);

        }

        return solicitadoLiquido.doubleValue();

    }

    private Double getSumConsumptionActive(List<MeansurementFileDetail> details) {
        return details.stream()
                .map(d -> new BigDecimal(d.getConsumptionActive()).setScale(6,RoundingMode.HALF_EVEN))
                //.mapToDouble(MeansurementFileDetail::getConsumptionActive)
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

    public void executeOld(DelegateExecution de) throws Exception {

        try {
            Long id = de.getVariable(FILE_MEANSUREMENT_ID, Long.class);
            MeansurementFile file = fileService.findById(id);
            List<MeansurementFileDetail> details = this.getDetails(file, de);

            Map<String, List<MeansurementFileDetail>> lotes = details
                    .parallelStream()
                    .collect(Collectors.groupingBy(MeansurementFileDetail::getMeansurementPoint));

            List<ConsumptionResult> results = Collections.synchronizedList(new ArrayList<>());

            lotes
                    .values()
                    .parallelStream()
                    .forEach((List<MeansurementFileDetail> lote) -> {

                        String point = lote.stream().findFirst().get().getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim();
                        Optional<ContractInformationDTO> opt = this.contractService.listByPoint(point);
                        Optional<ContractWbcInformationDTO> optWbc = this.contractWbcService.getInformation(file.getYear(), file.getMonth(), opt.get().getContractId());
                        final ConsumptionResult result = new ConsumptionResult();
                        result.setMeansurementPoint(point);

                        if (opt.isPresent()) {
                            ContractInformationDTO informationDTO = opt.get();
                            final double factorAtt = informationDTO.getFactorAttendanceCharge() / 100;
                            final double percentLoss = informationDTO.getPercentOfLoss() / 100;
                            final double proinfa = informationDTO.getProinfa();
                            final double sum = lote.stream()
                                    .mapToDouble(MeansurementFileDetail::getConsumptionActive)
                                    .reduce(0, Double::sum);

                            double consumptionTotal = ((sum / 1000) + ((sum / 1000) * percentLoss) - proinfa) * factorAtt;

                            BigDecimal consumptionTotalArredondado = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

                            BigDecimal solicitadoLiquido = new BigDecimal(0).setScale(3, RoundingMode.HALF_EVEN);

                            if (consumptionTotalArredondado.doubleValue() < optWbc.get().getNrQtd() && consumptionTotalArredondado.doubleValue() > optWbc.get().getNrQtdMin()) {

                                solicitadoLiquido = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

                            } else if (consumptionTotalArredondado.doubleValue() > optWbc.get().getNrQtd() && consumptionTotalArredondado.doubleValue() < optWbc.get().getNrQtdMax()) {

                                solicitadoLiquido = new BigDecimal(consumptionTotal).setScale(3, RoundingMode.HALF_EVEN);

                            } else if (consumptionTotalArredondado.doubleValue() > optWbc.get().getNrQtd() && consumptionTotalArredondado.doubleValue() > optWbc.get().getNrQtdMax()) {

                                solicitadoLiquido = new BigDecimal(optWbc.get().getNrQtdMax()).setScale(3, RoundingMode.HALF_EVEN);

                            }

                            Optional<CompanyDTO> optEmp = this.empresaService.listByPoint(point);
                            result.setResult(consumptionTotalArredondado.doubleValue());
                            result.setContractId(informationDTO.getContractId());
                            result.setPercentLoss(percentLoss);
                            result.setFactorAtt(factorAtt);
                            result.setProinfa(proinfa);
                            result.setEmpresa(optEmp.get());
                            result.setInformation(optWbc.get());
                            result.setSolicitadoLiquido(solicitadoLiquido.doubleValue());

                            // Optional<ContractMeasurementPoint> optional = pointService.findByPoint(point);
                            MeansurementFileResult fileResult = new MeansurementFileResult();
                            fileResult.setMeansurementFileId(id);
                            fileResult.setPercentLoss(percentLoss);
                            fileResult.setFactorAtt(factorAtt);
                            fileResult.setProinfa(proinfa);
                            // fileResult.setMeansurementPointId(optional.get().getId());
                            //fileResult.setResult(consumptionTotalArredondado.doubleValue());
                            //fileResult.setMontanteLiquido(solicitadoLiquido.doubleValue());

                            resultService.save(fileResult);

                        } else {
                            result.setError("Não existe cadastro do contrato associado ao ponto!");
                        }
                        results.add(result);
                    });

            de.setVariable(RESPONSE_RESULT, results);

        } catch (Exception e) {
            de.setVariable(RESPONSE_RESULT_MESSAGE, "Erro ao realizar o calculo!");
            Logger.getLogger(CalculateTask.class.getName()).log(Level.SEVERE, "[execute]", e);
        }

    }

}
