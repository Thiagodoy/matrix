/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.DataValidationResultDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.LogService;
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
import java.util.stream.Collectors;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.context.ApplicationContext;
import com.core.matrix.model.Log;
import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.service.MeansurementPointStatusService;
import com.core.matrix.utils.MeansurementFileType;
import com.core.matrix.utils.PointStatus;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;

/**
 *
 * @author thiag
 */
public class DataValidationTask extends Task {

    private MeansurementFileService fileService;
    private LogService logService;

    private DelegateExecution delegateExecution;
    private static ApplicationContext context;

    private List<DataValidationResultDTO> results = null;
    private MeansurementPointStatusService pointStatusService;

    public DataValidationTask() {
        synchronized (DataValidationTask.context) {
            this.fileService = DataValidationTask.context.getBean(MeansurementFileService.class);
            this.logService = DataValidationTask.context.getBean(LogService.class);
            this.pointStatusService = DataValidationTask.context.getBean(MeansurementPointStatusService.class);
        }
    }

    public DataValidationTask(ApplicationContext context) {
        DataValidationTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

        delegateExecution = de;

        this.loadVariables(delegateExecution);

        final String responseResult = MessageFormat.format("{0}:{1}", RESPONSE_RESULT, de.getProcessInstanceId());
        this.results = Collections.synchronizedList(new ArrayList<>());

        if (this.isOnlyContractFlatOrUnitConsumer()) {
            this.setVariable(CONTROLE, RESPONSE_DATA_IS_VALID);
            this.writeVariables(delegateExecution);
            return;
        }

        //REMOVE FILES THAT CONTRACT IS CONSUMER UNIT
        List<MeansurementFile> files = this.getFiles(true)
                .stream()
                .filter(f -> !this.isUnitConsumer(f.getWbcContract()))
                .collect(Collectors.toList());

        TaskService taskService = delegateExecution.getEngineServices().getTaskService();

        files.parallelStream()
                .forEach(file -> {

                    Attachment attachment;

                    synchronized (taskService) {
                        attachment = taskService.getAttachment(file.getFile());
                    }

                    try {

                        if (file.getStatus().equals(MeansurementFileStatus.FILE_MISSING_ALL_HOURS)) {
                            this.fillFileWithHoursMissing(file);
                        } else {
                            this.checkCalendar(file, attachment);
                            this.checkDays(file, attachment);
                            this.checkHour(file, attachment);
                        }

                    } catch (Exception e) {
                        Logger.getLogger(DataValidationTask.class.getName()).log(Level.SEVERE, "[ execute ]", e);
                        Log log = new Log();
                        log.setMessage(e.getMessage());
                        log.setProcessInstanceId(de.getProcessInstanceId());
                        log.setActivitiName(de.getCurrentActivityName());
                        synchronized (this.logService) {
                            this.logService.save(log);
                        }
                        this.setVariable(CONTROLE, RESPONSE_INVALID_DATA);
                    }

                });

        //update files
        files.stream().forEach(file -> {
            fileService.updateStatus(file.getStatus(), file.getId());
        });

        boolean hasInvalidCalendar = files.stream().anyMatch(mdf -> mdf.getStatus().equals(MeansurementFileStatus.DATA_CALENDAR_ERROR));
        boolean hasDetailsInvalid = files.stream().anyMatch(mdf -> mdf.getStatus().equals(MeansurementFileStatus.DATA_DAY_ERROR) || mdf.getStatus().equals(MeansurementFileStatus.DATA_HOUR_ERROR));

        if (hasInvalidCalendar) {
            this.setVariable(CONTROLE, RESPONSE_INVALID_DATA);
            this.writeLogMetrics();
        } else if (hasDetailsInvalid) {
            this.setVariable(CONTROLE, RESPONSE_INCONSISTENT_DATA);
            this.writeLogMetrics();

            if (de.hasVariable(responseResult)) {
                de.removeVariable(responseResult);
            }

            this.setVariable(responseResult, results);
        } else {
            this.setVariable(CONTROLE, RESPONSE_DATA_IS_VALID);
        }

        this.alterStatusPoint(files);
        this.writeVariables(delegateExecution);

    }

    private void alterStatusPoint(List<MeansurementFile> files) {

        try {
            this.getPointsRead().forEach(point -> {
                MeansurementPointStatus pointStatus = this.pointStatusService.getPoint(point);
                MeansurementFile file = files.stream().filter(f -> f.getMeansurementPoint().equals(point)).findFirst().get();

                switch (file.getStatus()) {
                    case DATA_CALENDAR_ERROR:
                        pointStatus.setStatus(PointStatus.PENDING);
                        break;
                    case DATA_DAY_ERROR:
                    case DATA_HOUR_ERROR:
                    case FILE_MISSING_ALL_HOURS:
                        pointStatus.setStatus(PointStatus.PENDING);
                        DataValidationResultDTO resultDTO = this.results.stream().filter(result -> result.getPoint().equals(point)).findFirst().get();
                        pointStatus.setHours(resultDTO.getHours());
                        pointStatus.setMountScde(resultDTO.getTotalScde());
                        break;
                    default:                        
                        Double mount = this.getMapDetails().get(point).stream().mapToDouble(MeansurementFileDetail::getConsumptionActive).sum();
                        pointStatus.setMountScde(mount);
                }
                
                pointStatus.forceUpdate();
            });
        } catch (Exception e) {
            Logger.getLogger(FileValidationTask.class.getName()).log(Level.SEVERE, "[alterStatusPoint]", e);
        }

    }

    private synchronized void fillFileWithHoursMissing(MeansurementFile file) {

        int daysOfMonth = YearMonth.of(file.getYear().intValue(), file.getMonth().intValue()).lengthOfMonth();

        List<MeansurementFileDetail> details = Collections.synchronizedList(this.getMapDetails().get(file.getMeansurementPoint()));
        List<MeansurementFileDetail> detailsTemp = Collections.synchronizedList(new ArrayList());

        String point = file.getType().equals(MeansurementFileType.LAYOUT_C) || file.getType().equals(MeansurementFileType.LAYOUT_C_1)
                ? file.getMeansurementPoint() + " (L)"
                : file.getMeansurementPoint();

        final int year = file.getYear().intValue();
        final int month = file.getMonth().intValue();

        Stream.iterate(1, i -> i + 1)
                .limit(daysOfMonth).parallel().forEach(day -> {

            List<MeansurementFileDetail> hours = new ArrayList<>();

            Stream.iterate(1, i -> i + 1)
                    .limit(24)
                    .forEach(hour -> {

                        LocalDate date = LocalDate.of(year, month, day);

                        final boolean exists = details.stream().anyMatch(d -> d.getHour().intValue() == hour && d.getDate().isEqual(date));

                        if (!exists) {
                            hours.add(new MeansurementFileDetail(LocalDate.of(year, month, day), hour.longValue(), file.getId(), point));
                        }

                    });

            detailsTemp.addAll(hours);

        });

        detailsTemp.parallelStream().forEach(d -> d.setStatus(MeansurementFileDetailStatus.HOUR_ERROR));

        DataValidationResultDTO result = new DataValidationResultDTO();
        result.setIdFile(file.getId());

        String name = "";

        result.setFileName(name);
        result.setPoint(file.getMeansurementPoint());

        Double sum = file.getDetails()
                .stream()
                .mapToDouble(MeansurementFileDetail::getConsumptionActive)
                .reduce(Double::sum)
                .getAsDouble();

        result.setTotalScde(sum);

        final Long qtdHours = detailsTemp.stream().count();
        result.setHours(qtdHours);

        results.add(result);

        this.addDetails(file.getMeansurementPoint(), detailsTemp);
        file.setStatus(MeansurementFileStatus.DATA_HOUR_ERROR);

    }

    private void writeLogMetrics() {
        Log log = new Log();
        log.setType(Log.LogType.DATA_INVALID);
        log.setProcessInstanceId(delegateExecution.getProcessInstanceId());
        this.logService.save(log);
    }

    private synchronized void checkCalendar(MeansurementFile file, final Attachment attachment) throws Exception {

        int daysOnMonth = YearMonth.of(file.getYear().intValue(), Month.of(file.getMonth().intValue())).lengthOfMonth();
        LocalDate init = LocalDate.of(file.getYear().intValue(), file.getMonth().intValue(), 1);
        LocalDate end = LocalDate.of(file.getYear().intValue(), file.getMonth().intValue(), daysOnMonth);

        boolean hasErrorOfCalendar = this.getDetails(file, delegateExecution)
                .parallelStream()
                .map(MeansurementFileDetail::getDate)
                .anyMatch(d -> d.isAfter(end) || d.isBefore(init));

        if (hasErrorOfCalendar) {
            file.setStatus(MeansurementFileStatus.DATA_CALENDAR_ERROR);
            String error = MessageFormat.format("Calendário inválido para o ponto [ {0} ] dentro do arquivo [ {1} ]", file.getMeansurementPoint(), attachment.getName());
            throw new Exception(error);
        }

    }

    private synchronized void checkHour(MeansurementFile file, final Attachment attachment) throws Exception {

        Map<String, List<MeansurementFileDetail>> lotes = this.getDetails(file, delegateExecution)
                .stream()
                .collect(Collectors.groupingBy(MeansurementFileDetail::getMeansurementPoint));

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

                    String name = Optional.ofNullable(attachment).isPresent() ? attachment.getName() : "";

                    result.setFileName(name);
                    result.setPoint(file.getMeansurementPoint());

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

                    this.addDetails(file.getMeansurementPoint(), hoursOut);
                }
            }

        });

        if (this.hasHourError(delegateExecution, file.getMeansurementPoint())) {
            file.setStatus(MeansurementFileStatus.DATA_HOUR_ERROR);
            String error = MessageFormat.format("Arquivo esta com a consolidação diária das hora inválida, para o ponto [ {0} ] dentro do arquivo [ {1} ]", file.getMeansurementPoint(), attachment.getName());

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

    private synchronized void checkDays(MeansurementFile file, final Attachment attachment) throws Exception {

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

                            String name = Optional.ofNullable(attachment).isPresent() ? attachment.getName() : "";

                            result.setFileName(name);
                            result.setPoint(file.getMeansurementPoint());

                            Double sum = lote
                                    .stream()
                                    .mapToDouble(MeansurementFileDetail::getConsumptionActive)
                                    .reduce(Double::sum)
                                    .getAsDouble();

                            result.setTotalScde(sum);

                            final Long qtdHours = detailsOut.stream().count();
                            result.setHours(qtdHours);

                            results.add(result);

                            this.addDetails(file.getMeansurementPoint(), detailsOut);

                        }

                    }
                });

        if (this.hasDayError(delegateExecution, file.getMeansurementPoint())) {

            file.setStatus(MeansurementFileStatus.DATA_DAY_ERROR);
            String error = MessageFormat.format("Arquivo esta com as horas diárias ausente, para o ponto [ {0} ] dentro do arquivo [ {1} ]", file.getMeansurementPoint(), attachment.getName());
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
