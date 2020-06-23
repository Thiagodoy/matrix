/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.DataValidationResultDTO;
import com.core.matrix.io.BeanIO;
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
import java.text.MessageFormat;
import java.util.Collections;
import org.activiti.engine.task.Attachment;

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

    private Attachment attachment;

    private List<DataValidationResultDTO> results = null;

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

        final String responseResult = MessageFormat.format("{0}:{1}", RESPONSE_RESULT, de.getProcessInstanceId());
        this.results = new ArrayList<>();

        //REMOVE FILES THAT CONTRACT IS CONSUMER UNIT
        List<MeansurementFile> files = this.fileService
                .findByProcessInstanceId(delegateExecution.getProcessInstanceId())
                .stream()
                .filter(f -> !this.contractInformationService.isConsumerUnit(f.getWbcContract()))
                .collect(Collectors.toList());

        files.forEach(file -> {            
            file.setStatus(MeansurementFileStatus.SUCCESS);
        });

        files.stream().forEach(file -> {

            attachment = delegateExecution.getEngineServices().getTaskService().getAttachment(file.getFile());

            try {
                this.checkCalendar(file);
                this.checkDays(file);
                this.checkHour(file);

            } catch (Exception e) {

                Log log = new Log();
                log.setMessage(e.getMessage());
                log.setProcessInstanceId(de.getProcessInstanceId());
                log.setProcessName(de.getProcessBusinessKey());
                log.setActivitiName(de.getCurrentActivityName());
                this.logService.save(log);                
                de.setVariable(CONTROLE, RESPONSE_INVALID_DATA);

            }

        });

        boolean hasInvalidaData = files.stream().anyMatch(mdf -> mdf.getStatus().equals(MeansurementFileStatus.DATA_CALENDAR_ERROR));
        boolean hasDataForPersist = files.stream().anyMatch(mdf -> mdf.getStatus().equals(MeansurementFileStatus.DATA_DAY_ERROR) || mdf.getStatus().equals(MeansurementFileStatus.DATA_HOUR_ERROR));

        if (hasInvalidaData) {
            de.setVariable(CONTROLE, RESPONSE_INVALID_DATA);
            this.writeLogMetrics();
        } else if (hasDataForPersist) {
            de.setVariable(CONTROLE, RESPONSE_INCONSISTENT_DATA);            
            this.writeLogMetrics();
            
            if (de.hasVariable(responseResult)) {
                de.removeVariable(responseResult);             
            }

            de.setVariable(responseResult, results, true);
        } else {
            de.setVariable(CONTROLE, RESPONSE_DATA_IS_VALID);
        }

    }

    private void writeLogMetrics() {
        Log log = new Log();
        log.setType(Log.LogType.DATA_INVALID);
        log.setProcessInstanceId(delegateExecution.getProcessInstanceId());
        this.logService.save(log);
    }

    private void checkCalendar(MeansurementFile file) throws Exception {        

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

            String error = MessageFormat.format("Calendário inválido para o ponto [ {0} ] dentro do arquivo [ {1} ]", file.getMeansurementPoint(), this.attachment.getName());

            throw new Exception(error);
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

                    final Long qtdHours = hoursOut.stream().count();
                    result.setHours(qtdHours);

                    results.add(result);

                    hoursOut.parallelStream().forEach(d -> {
                        d.setStatus(MeansurementFileDetailStatus.HOUR_ERROR);
                    });

                    details.addAll(hoursOut);
                    file.getDetails().addAll(hoursOut);
                    this.fileDetailService.save(details);
                }
            }

        });

        if (!details.isEmpty()) {
            file.setStatus(MeansurementFileStatus.DATA_HOUR_ERROR);
            //details.forEach(mpd -> mpd.setStatus(MeansurementFileDetailStatus.HOUR_ERROR));

            String error = MessageFormat.format("Arquivo esta com a consolidação diária das hora inválida, para o ponto [ {0} ] dentro do arquivo [ {1} ]", file.getMeansurementPoint(), this.attachment.getName());

            throw new Exception(error);
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

        List<MeansurementFileDetail> detailsOut = Collections.synchronizedList(new ArrayList<>());

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

                            if (!(days.containsKey(checkDay) && days.get(checkDay).compareTo(24L) == 0)) {

                                final List<MeansurementFileDetail> hours = getHours(lote, checkDay);
                                // Make hours of day
                                for (int i = 1; i <= 24; i++) {
                                    if (!existsHours(hours, (long) i)) {
                                        detailsOut.add(new MeansurementFileDetail(checkDay, (long) i, file.getId(), point));
                                    }
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
                            results.add(result);

                            file.getDetails().addAll(detailsOut);

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
            String error = MessageFormat.format("Arquivo esta com as horas diárias ausente, para o ponto [ {0} ] dentro do arquivo [ {1} ]", file.getMeansurementPoint(), this.attachment.getName());
            throw new Exception(error);
        }

    }

    public synchronized List<MeansurementFileDetail> getHours(List<MeansurementFileDetail> lote, LocalDate checkDay) {
        return lote.stream().filter(detail -> detail.getDate().isEqual(checkDay)).collect(Collectors.toList());
    }

    public synchronized boolean existsHours(List<MeansurementFileDetail> lote, Long hour) {
        return lote.stream().anyMatch(d -> d.getHour().equals(hour));
    }

}
