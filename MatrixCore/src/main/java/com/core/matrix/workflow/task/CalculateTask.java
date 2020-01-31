/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.ConsumptionResult;
import com.core.matrix.dto.ContractInformationDTO;
import com.core.matrix.model.ContractMeasurementPoint;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.model.MeansurementFileResult;
import com.core.matrix.service.ContractCompInformationService;
import com.core.matrix.service.ContractMeasurementPointService;
import com.core.matrix.service.MeansurementFileResultService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.FILE_MEANSUREMENT_ID;
import static com.core.matrix.utils.Constants.RESPONSE_RESULT;
import static com.core.matrix.utils.Constants.RESPONSE_RESULT_MESSAGE;
import com.core.matrix.wbc.dto.EmpresaDTO;
import com.core.matrix.wbc.service.EmpresaService;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private ContractMeasurementPointService pointService;
    private MeansurementFileResultService resultService;

    public CalculateTask() {

        synchronized (CalculateTask.context) {
            this.fileService = CalculateTask.context.getBean(MeansurementFileService.class);
            this.contractService = CalculateTask.context.getBean(ContractCompInformationService.class);
            this.empresaService = CalculateTask.context.getBean(EmpresaService.class);
            this.pointService = CalculateTask.context.getBean(ContractMeasurementPointService.class);
            this.resultService = CalculateTask.context.getBean(MeansurementFileResultService.class);

        }

    }

    public CalculateTask(ApplicationContext context) {
        CalculateTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

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
                    .forEach(lote -> {

                        String point = lote.stream().findFirst().get().getMeansurementPoint().replaceAll("\\((L|B)\\)", "").trim();
                        Optional<ContractInformationDTO> opt = this.contractService.listByPoint(point);

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
                            
                            Optional<EmpresaDTO> optEmp = this.empresaService.listByPoint(point);
                            result.setResult(consumptionTotalArredondado.doubleValue());
                            result.setContractId(informationDTO.getContractId());
                            result.setPercentLoss(percentLoss);
                            result.setFactorAtt(factorAtt);
                            result.setProinfa(proinfa);
                            result.setEmpresa(optEmp.get());
                            
                            Optional<ContractMeasurementPoint> optional =  pointService.findByPoint(point);
                            MeansurementFileResult fileResult = new MeansurementFileResult();
                            fileResult.setMeansurementFileId(id);
                            fileResult.setPercentLoss(percentLoss);
                            fileResult.setFactorAtt(factorAtt);
                            fileResult.setProinfa(proinfa);
                            fileResult.setMeansurementPointId(optional.get().getId());
                            fileResult.setResult(consumptionTotalArredondado.doubleValue());
                            
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
