/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
public class DataValidationTask implements JavaDelegate {

    @Autowired
    private MeansurementFileService fileService;

    private DelegateExecution delegateExecution;

    @Override
    public void execute(DelegateExecution de) throws Exception {

        delegateExecution = de;

        Long id = de.getVariable(FILE_MEANSUREMENT_ID, Long.class);

        try {

            MeansurementFile file = fileService.findById(id);
            this.checkDays(file);
            this.checkCalendar(file);
            this.checkHour(file);

        } catch (Exception e) {
            Logger.getLogger(DataValidationTask.class.getName()).log(Level.SEVERE, "[execute]", e);
        }

    }

    private void checkCalendar(MeansurementFile file) throws Exception {

        int daysOnMonth = YearMonth.of(file.getYear().intValue(), Month.of(file.getMonth().intValue())).lengthOfMonth();
        LocalDate init = LocalDate.of(file.getYear().intValue(), file.getMonth().intValue(), 1);
        LocalDate end = LocalDate.of(file.getYear().intValue(), file.getMonth().intValue(), daysOnMonth);

        List<MeansurementFileDetail> detail = file.getDetails().parallelStream().filter(d -> d.getDate().isAfter(end) || d.getDate().isBefore(init)).collect(Collectors.toList());

        if (detail.isEmpty()) {
            delegateExecution.setVariable(RESPONSE_RESULT, detail);
            delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Calendário apresenta registros fora do ciclo de avaliação!");
            delegateExecution.setVariable(CONTROLE, RESPONSE_CALENDAR_INVALID, true);
            throw new Exception("Calendário inválido! arquivo ->" + file.getId());
        }

    }

    private void checkHour(MeansurementFile file) throws Exception {

        Map<LocalDate, Long> days = file
                .getDetails()
                .stream()
                .collect(Collectors.groupingBy(MeansurementFileDetail::getDate, Collectors.counting()));

        List<LocalDate> daysInvalids = days.keySet().stream().filter(d -> days.get(d) < 24).sorted().collect(Collectors.toList());

        List<MeansurementFileDetail> hoursOut = new ArrayList<>();

        if (!days.isEmpty()) {

            daysInvalids.forEach(day -> {
                for (int i = 1; i <= 24; i++) {
                    if (this.hourIsNotPresent((long) i, file, day)) {
                        hoursOut.add(new MeansurementFileDetail(day, (long) i));
                    }
                }
            });

            if (!hoursOut.isEmpty()) {
                hoursOut
                        .stream()
                        .sorted(Comparator
                                .comparing(MeansurementFileDetail::getDate)
                                .thenComparing(MeansurementFileDetail::getHour));

                delegateExecution.setVariable(RESPONSE_RESULT, hoursOut);
                delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Arquivo esta com a consolidação diária das hora inválida");
                delegateExecution.setVariable(CONTROLE, Constants.RESPONSE_NO_DATA_FOUND, true);
                throw new Exception("Arquivo esta com a consolidação diária das hora inválida arquivo -> " + file.getId());

            }
        }
    }

    private boolean hourIsNotPresent(final Long hour, MeansurementFile file, LocalDate day) {

        Optional<MeansurementFileDetail> exists = file.getDetails()
                .parallelStream()
                .filter(detail -> detail.getDate().equals(day) && detail.getHour().equals(hour))
                .findFirst();

        return !exists.isPresent();
    }

    private void checkDays(MeansurementFile file) throws Exception {

        int daysOnMonth = YearMonth.of(file.getYear().intValue(), Month.of(file.getMonth().intValue())).lengthOfMonth();

        Map<LocalDate, Long> days = file
                .getDetails()
                .stream()
                .collect(Collectors.groupingBy(MeansurementFileDetail::getDate, Collectors.counting()));

        List<MeansurementFileDetail> detailsOut = new ArrayList<>();

        if (daysOnMonth != days.size()) {

            LocalDate checkDay = LocalDate.of(file.getYear().intValue(), Month.of(file.getMonth().intValue()), 1);
            for (int day = 1; day <= daysOnMonth; day++) {

                if (!days.containsKey(checkDay)) {
                    detailsOut.add(new MeansurementFileDetail(checkDay));
                }
                checkDay = checkDay.plusDays(1L);
            }
        }

        if (!detailsOut.isEmpty()) {
            delegateExecution.setVariable(RESPONSE_RESULT, detailsOut);
            delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Arquivo esta com o calendário  de apuração com dias faltantes");
            delegateExecution.setVariable(CONTROLE, Constants.RESPONSE_NO_DATA_FOUND, true);
            throw new Exception("Dados ausentes no arquivo -> " + file.getId());
        }

    }

}
