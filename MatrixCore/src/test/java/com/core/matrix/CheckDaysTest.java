/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.dto.DataValidationResultDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import com.core.matrix.workflow.task.Task;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.task.Attachment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author thiag
 */
@SpringBootTest
public class CheckDaysTest implements Task {

    @Autowired
    private MeansurementFileService fileService;

    private DelegateExecution delegateExecution;

    
    @Override
    public void execute(DelegateExecution de) throws Exception {
        
    }
    
    @Test    
    public void teste() throws Exception {
        checkDays(fileService.findById(9842L));
    }

    
    public void checkDays(MeansurementFile file) throws Exception {

        int daysOnMonth = YearMonth.of(file.getYear().intValue(), Month.of(file.getMonth().intValue())).lengthOfMonth();

        Map<String, List<MeansurementFileDetail>> lotes = this.getDetails(file, delegateExecution)
                .stream()
                .collect(Collectors.groupingBy(MeansurementFileDetail::getMeansurementPoint));

        List<MeansurementFileDetail> detailsOut = new ArrayList<>();

        lotes.values()
                .parallelStream()
                .forEach(lote -> {

                    final String point = lote.stream().findFirst().get().getMeansurementPoint();
                    Map<LocalDate, Long> days
                            = lote.stream()
                                    .collect(Collectors.groupingBy(MeansurementFileDetail::getDate, Collectors.counting()));

                    if (daysOnMonth != days.size()) {

                        LocalDate checkDay = LocalDate.of(file.getYear().intValue(), Month.of(file.getMonth().intValue()), 1);

                        for (int day = 1; day <= daysOnMonth; day++) {

                            if (!days.containsKey(checkDay)) {

                                // Make hours of day
                                for (int i = 1; i <= 24; i++) {
                                    detailsOut.add(new MeansurementFileDetail(checkDay, (long) i, file.getId(), point));
                                }
                            }
                            checkDay = checkDay.plusDays(1L);
                        }

                        detailsOut.parallelStream().forEach(e -> {
                            e.setStatus(MeansurementFileDetailStatus.DAY_ERROR);
                        });

                        if (!detailsOut.isEmpty()) {

                            DataValidationResultDTO result = new DataValidationResultDTO();
                            result.setIdFile(file.getId());

                            Optional<Attachment> opta = delegateExecution
                                    .getEngineServices()
                                    .getTaskService()
                                    .getProcessInstanceAttachments(delegateExecution.getProcessInstanceId())
                                    .stream()
                                    .filter(t -> t.getId().equals(file.getFile()))
                                    .findFirst();

                            String name = "";
                            if (opta.isPresent()) {
                                name = opta.get().getName();
                            }

                            result.setFileName(name);
                            result.setPoint(point);

                            Double sum = lote
                                    .stream()
                                    .mapToDouble(MeansurementFileDetail::getConsumptionActive)
                                    .reduce(Double::sum)
                                    .getAsDouble();

                            result.setTotalScde(sum);

                            final Long qtdHours = detailsOut.stream().count();
                            result.setHours(qtdHours);
                            //results.add(result);

                            file.getDetails().addAll(detailsOut);

                            //this.fileDetailService.save(detailsOut);
                        }

                    }
                });

        long hasError = detailsOut
                .stream()
                .filter(mfd -> mfd.getStatus().equals(MeansurementFileDetailStatus.DAY_ERROR))
                .count();

        if (hasError > 1L) {

            throw new Exception("");
        }

    }

}
