/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.task;

import com.core.matrix.dto.ErrorInformationDTO;
import com.core.matrix.dto.LoteDTO;
import com.core.matrix.model.MeansurementFile;
import com.core.matrix.model.MeansurementFileDetail;
import com.core.matrix.service.MeansurementFileDetailService;
import com.core.matrix.service.MeansurementFileService;
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.*;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import com.core.matrix.wbc.dto.CompanyDTO;
import com.core.matrix.wbc.service.EmpresaService;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
public class DataValidationTask implements Task {

    private MeansurementFileService fileService;
    private MeansurementFileDetailService meansurementFileDetailService;
    private EmpresaService empresaService;

    private DelegateExecution delegateExecution;
    private static ApplicationContext context;

    public DataValidationTask() {
        synchronized (DataValidationTask.context) {
            this.fileService = DataValidationTask.context.getBean(MeansurementFileService.class);
            this.empresaService = DataValidationTask.context.getBean(EmpresaService.class);
            this.meansurementFileDetailService = DataValidationTask.context.getBean(MeansurementFileDetailService.class);
        }
    }

    public DataValidationTask(ApplicationContext context) {
        DataValidationTask.context = context;
    }

    @Override
    public void execute(DelegateExecution de) throws Exception {

        delegateExecution = de;

        Long id = de.getVariable(FILE_MEANSUREMENT_ID, Long.class);

        try {

            MeansurementFile file = fileService.findById(id);            
            this.checkDays(file);
            this.checkCalendar(file);
            this.checkHour(file);
            delegateExecution.setVariable(CONTROLE, RESPONSE_DATA_IS_VALID, true);

        } catch (Exception e) {
            Logger.getLogger(DataValidationTask.class.getName()).log(Level.SEVERE, "[execute]", e);
        }

    }   

    private void checkCalendar(MeansurementFile file) throws Exception {

        int daysOnMonth = YearMonth.of(file.getYear().intValue(), Month.of(file.getMonth().intValue())).lengthOfMonth();
        LocalDate init = LocalDate.of(file.getYear().intValue(), file.getMonth().intValue(), 1);
        LocalDate end = LocalDate.of(file.getYear().intValue(), file.getMonth().intValue(), daysOnMonth);

        Map<String, ErrorInformationDTO<MeansurementFileDetail>> lotesErrors = new HashMap<>();

        Map<String, List<MeansurementFileDetail>> lotes = this.getDetails(file,delegateExecution)
                .stream()
                .collect(Collectors.groupingBy(MeansurementFileDetail::getMeansurementPoint));

        lotes.values().parallelStream().forEach(lote -> {

            List<MeansurementFileDetail> detail = lote
                    .parallelStream()
                    .filter(d -> d.getDate().isAfter(end) || d.getDate().isBefore(init))
                    .collect(Collectors.toList());

            if (!detail.isEmpty()) {
                String point = detail.stream().findFirst().get().getMeansurementPoint();
                Optional<CompanyDTO> opt = this.empresaService.listByPoint(point.replaceAll("\\((L|B)\\)", "").trim());
                ErrorInformationDTO<MeansurementFileDetail> error = new ErrorInformationDTO<>("Lote apresenta registros fora do ciclo de faturamento", detail, opt.orElse(new CompanyDTO()));

                synchronized (lotesErrors) {
                    lotesErrors.put(point, error);
                }
            }

        });

        if (!lotesErrors.isEmpty()) {

            LoteDTO lote = new LoteDTO();
            lote.setLotes(lotesErrors);

            delegateExecution.setVariable(RESPONSE_RESULT, lote);
            delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Lote apresenta registros fora do ciclo de avaliação!");
            delegateExecution.setVariable(CONTROLE, RESPONSE_CALENDAR_INVALID, true);
            throw new Exception("Calendário inválido! arquivo ->" + file.getId());
        }

    }

    private void checkHour(MeansurementFile file) throws Exception {

        Map<String, ErrorInformationDTO<MeansurementFileDetail>> lotesErrors = new HashMap<>();

        Map<String, List<MeansurementFileDetail>> lotes = this.getDetails(file,delegateExecution)
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
                    hoursOut
                            .stream()
                            .sorted(Comparator
                                    .comparing(MeansurementFileDetail::getDate)
                                    .thenComparing(MeansurementFileDetail::getHour));

                    String p = point.replaceAll("\\((L|B)\\)", "").trim();
                    Optional<CompanyDTO> opt = this.empresaService.listByPoint(p);
                    ErrorInformationDTO<MeansurementFileDetail> error = new ErrorInformationDTO<>("Arquivo esta com a consolidação diária das hora inválida", hoursOut, opt.orElse(new CompanyDTO()));

                    synchronized (lotesErrors) {
                        lotesErrors.put(point, error);
                    }
                }
            }

        });

        if (!lotesErrors.isEmpty()) {

            LoteDTO lote = new LoteDTO();
            lote.setLotes(lotesErrors);

            delegateExecution.setVariable(RESPONSE_RESULT, lote);
            delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Arquivo esta com a consolidação diária das hora inválida");
            delegateExecution.setVariable(CONTROLE, Constants.RESPONSE_NO_DATA_FOUND, true);
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

        Map<String, ErrorInformationDTO<MeansurementFileDetail>> lotesErrors = new HashMap<>();

        Map<String, List<MeansurementFileDetail>> lotes = this.getDetails(file,delegateExecution)
                .stream()
                .collect(Collectors.groupingBy(MeansurementFileDetail::getMeansurementPoint));

        lotes.values().parallelStream().forEach(lote -> {

            final String point = lote.stream().findFirst().get().getMeansurementPoint();
            Map<LocalDate, Long> days
                    = lote.stream()
                            .collect(Collectors.groupingBy(MeansurementFileDetail::getDate, Collectors.counting()));

            List<MeansurementFileDetail> detailsOut = new ArrayList<>();

            if (daysOnMonth != days.size()) {

                LocalDate checkDay = LocalDate.of(file.getYear().intValue(), Month.of(file.getMonth().intValue()), 1);
                for (int day = 1; day <= daysOnMonth; day++) {

                    if (!days.containsKey(checkDay)) {
                        // Make de hours of day
                        for (int i = 1; i <= 24; i++) {
                            detailsOut.add(new MeansurementFileDetail(checkDay, (long) i, file.getId(), point));
                        }
                    }
                    checkDay = checkDay.plusDays(1L);
                }
                
                
                detailsOut.parallelStream().forEach(e->{
                    e.setStatus(MeansurementFileDetailStatus.ERROR_HOUR);
                });
                
                this.meansurementFileDetailService.save(detailsOut);
                
            }

            if (!detailsOut.isEmpty()) {
                String p = point.replaceAll("\\((L|B)\\)", "").trim();
                Optional<CompanyDTO> opt = this.empresaService.listByPoint(p);
                ErrorInformationDTO<MeansurementFileDetail> error = new ErrorInformationDTO<>("Arquivo esta com o calendário  de apuração com dias faltantes!", detailsOut, opt.orElse(new CompanyDTO()));

                synchronized (lotesErrors) {
                    lotesErrors.put(point, error);
                }
            }

        });

        if (!lotesErrors.isEmpty()) {

            LoteDTO lote = new LoteDTO();
            lote.setLotes(lotesErrors);

            delegateExecution.setVariable(RESPONSE_RESULT, lote);
            delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Arquivo esta com o calendário de apuração com dias faltantes");
            delegateExecution.setVariable(CONTROLE, Constants.RESPONSE_NO_DATA_FOUND, true);
            throw new Exception("Dados ausentes no arquivo -> " + file.getId());
        }

    }

}
