/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.io.BeanIoReader;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.LogService;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.service.MeansurementFileService;
import static com.core.matrix.utils.Constants.*;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import com.core.matrix.utils.MeansurementFileStatus;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationContext;
import com.core.matrix.model.Log;
import com.core.matrix.service.ContractCompInformationService;

/**
 *
 * @author thiag
 */
public class DataValidationTask implements Task {

    private MeansurementFileService fileService;
    private MeansurementFileDetailService fileDetailService;
    private ContractCompInformationService contractInformationService;

    private LogService logService;

    private DelegateExecution delegateExecution;
    private static ApplicationContext context;

    public DataValidationTask() {
        synchronized (DataValidationTask.context) {
            this.fileService = DataValidationTask.context.getBean(MeansurementFileService.class);
            this.fileDetailService = DataValidationTask.context.getBean(MeansurementFileDetailService.class);
            this.logService = DataValidationTask.context.getBean(LogService.class);
            this.contractInformationService = DataValidationTask.context.getBean(ContractCompInformationService.class);
        }
    }

    public DataValidationTask(ApplicationContext context) {
        DataValidationTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

        delegateExecution = de;

        List<MeansurementFile> files = this.fileService
                .findByProcessInstanceId(delegateExecution.getProcessInstanceId())
                .stream() //Remove files that is a consumer unit
                .filter( f-> !this.contractInformationService.isConsumerUnit(f.getWbcContract()))
                .collect(Collectors.toList());

        files.forEach(file -> {
            Logger.getLogger(BeanIoReader.class.getName()).log(Level.SEVERE, "File id -> " + file.getId());
            file.setStatus(MeansurementFileStatus.SUCCESS);
        });

        files.stream().forEach(file -> {

            try {
                this.checkCalendar(file);
                this.checkDays(file);
                this.checkHour(file);

            } catch (Exception e) {

                Log log = new Log();
                log.setMessage(e.getMessage());
                log.setNameProcesso(de.getProcessInstanceId());
                this.logService.save(log);

                Logger.getLogger(DataValidationTask.class.getName()).log(Level.SEVERE, "[execute]", e);
                de.setVariable(CONTROLE, RESPONSE_INVALID_DATA);

            }

        });

        boolean hasInvalidaData = files.stream().anyMatch(mdf -> mdf.getStatus().equals(MeansurementFileStatus.DATA_CALENDAR_ERROR));
        boolean hasDataForPersist = files.stream().anyMatch(mdf -> mdf.getStatus().equals(MeansurementFileStatus.DATA_DAY_ERROR) || mdf.getStatus().equals(MeansurementFileStatus.DATA_HOUR_ERROR));

        if (hasInvalidaData) {
            de.setVariable(CONTROLE, RESPONSE_INVALID_DATA);
        } else if (hasDataForPersist) {
            de.setVariable(CONTROLE, RESPONSE_INCONSISTENT_DATA);
        } else {
            de.setVariable(CONTROLE, RESPONSE_DATA_IS_VALID);
        }

    }

    private void checkCalendar(MeansurementFile file) throws Exception {

        long monthTeste = file.getMonth();

        int daysOnMonth = YearMonth.of(file.getYear().intValue(), Month.of(file.getMonth().intValue())).lengthOfMonth();
        LocalDate init = LocalDate.of(file.getYear().intValue(), file.getMonth().intValue(), 1);
        LocalDate end = LocalDate.of(file.getYear().intValue(), file.getMonth().intValue(), daysOnMonth);

        Map<String, List<MeansurementFileDetail>> lotes = this.getDetails(file, delegateExecution)
                .stream()
                .collect(Collectors.groupingBy(MeansurementFileDetail::getMeansurementPoint));

        List<MeansurementFileDetail> details = new ArrayList<>();

        lotes.values().stream().forEach(lote -> {

            List<MeansurementFileDetail> out = lote
                    .parallelStream()
                    .filter(d -> d.getDate().isAfter(end) || d.getDate().isBefore(init))
                    .collect(Collectors.toList());

            if (!out.isEmpty()) {

                out.forEach(mfd -> {
                    mfd.setStatus(MeansurementFileDetailStatus.CALENDAR_ERROR);
                });

                details.addAll(out);
            }

        });

        if (!details.isEmpty()) {
            file.setStatus(MeansurementFileStatus.DATA_CALENDAR_ERROR);
            fileService.updateStatus(MeansurementFileStatus.DATA_CALENDAR_ERROR, file.getId());

            throw new Exception("Calendário inválido! arquivo -> " + file.getId());
        }

    }

    private void checkHour(MeansurementFile file) throws Exception {

        Map<String, List<MeansurementFileDetail>> lotes = this.getDetails(file, delegateExecution)
                .stream()
                .collect(Collectors.groupingBy(MeansurementFileDetail::getMeansurementPoint));

        List<MeansurementFileDetail> details = new ArrayList<>();

        lotes.values().parallelStream().forEach(lote -> {

            final String point = lote.stream().findFirst().get().getMeansurementPoint();

            Map<LocalDate, Long> days
                    = lote.stream()
                            .collect(Collectors.groupingBy(MeansurementFileDetail::getDate, Collectors.counting()));

            List<LocalDate> daysInvalids = days.keySet().stream().filter(d -> days.get(d) < 24).sorted().collect(Collectors.toList());

            List<MeansurementFileDetail> hoursOut = new ArrayList<>();

            if (!days.isEmpty()) {

                daysInvalids.forEach(day -> {
                    for (int i = 1; i <= 24; i++) {
                        if (this.hourIsNotPresent((long) i, file, day)) {
                            hoursOut.add(new MeansurementFileDetail(day, (long) i, file.getId(), point));
                        }
                    }
                });

                if (!hoursOut.isEmpty()) {

                    hoursOut.parallelStream().forEach(d -> {
                        d.setStatus(MeansurementFileDetailStatus.HOUR_ERROR);
                    });

                    details.addAll(hoursOut);
                    this.fileDetailService.save(details);
                }
            }

        });

        if (!details.isEmpty()) {
            file.setStatus(MeansurementFileStatus.DATA_HOUR_ERROR);
            details.forEach(mpd -> mpd.setStatus(MeansurementFileDetailStatus.HOUR_ERROR));
            throw new Exception("Arquivo esta com a consolidação diária das hora inválida arquivo -> " + file.getId());
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
                            this.fileDetailService.save(detailsOut);
                        }

                    }
                });

        long hasError = detailsOut
                .stream()
                .filter(mfd -> mfd.getStatus().equals(MeansurementFileDetailStatus.DAY_ERROR))
                .count();

        if (hasError > 1L) {

            file.setStatus(MeansurementFileStatus.DATA_DAY_ERROR);
            fileService.updateStatus(MeansurementFileStatus.DATA_DAY_ERROR, file.getId());
            throw new Exception("Dados ausentes no arquivo -> " + file.getId());
        }

    }

}
