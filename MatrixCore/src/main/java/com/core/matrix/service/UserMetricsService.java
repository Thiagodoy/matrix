/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.response.UserMetricResponse;
import com.core.matrix.utils.Utils;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class UserMetricsService {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    public enum MetricType {
        DAILY, WEEKLY, MONTHLY, YEARLY;
    }

    private final String START = "start";
    private final String END = "end";

    public UserMetricResponse metricTaskClosed(MetricType type, String userId) {

        Map<String, LocalDateTime> filter = this.getPeriod(type);

        List<HistoricTaskInstance> result
                = historyService
                        .createHistoricTaskInstanceQuery()
                        .taskAssignee(userId)
                        .taskCompletedAfter(Utils.localDateTimeToDate(filter.get(START)))
                        .taskCompletedBefore(Utils.localDateTimeToDate(filter.get(END)))
                        .orderByTaskCreateTime()
                        .asc()
                        .finished()
                        .list();

        UserMetricResponse response = new UserMetricResponse<LocalDateTime>();
        response.setType(type);
        response.setStart(filter.get(START).format(DateTimeFormatter.ISO_DATE_TIME));
        response.setEnd(filter.get(END).format(DateTimeFormatter.ISO_DATE_TIME));
        Map<LocalDateTime, Long> metrics;

        switch (type) {
            case DAILY:

                metrics = groupInformation(result, ChronoUnit.HOURS);
                response.setData(formatValues(metrics, "HH:mm"));
                return response;

            case MONTHLY:
                metrics = groupInformation(result, ChronoUnit.DAYS);
                response.setData(formatValues(metrics, "dd/MM"));
                return response;

            case WEEKLY:
                metrics = groupInformation(result, ChronoUnit.DAYS);
                response.setData(formatValues(metrics, "E dd"));
                return response;
            case YEARLY:
                metrics = groupInformation(result, ChronoUnit.MONTHS);
                response.setData(formatValues(metrics, "MM/YYYY"));
                return response;

        }

        return null;
    }

    private Map<String, Long> formatValues(Map<LocalDateTime, Long> metrics, String formatKey) {

        Map<String, Long> result = new LinkedHashMap<>();

        metrics.keySet().stream().forEach(key -> {
            Long value = metrics.get(key);
            String keyFormatted = key.format(DateTimeFormatter.ofPattern(formatKey));
            result.put(keyFormatted, value);
        });

        return result;
    }

    private Map<LocalDateTime, Long> groupInformation(List<HistoricTaskInstance> result, ChronoUnit type) {

        Map<LocalDateTime, Long> data = result.stream().parallel().map(t -> {
            LocalDateTime due = Utils.dateToLocalDateTime(t.getEndTime());

            if (type.equals(ChronoUnit.MONTHS)) {
                due = due.truncatedTo(ChronoUnit.DAYS);
                due = due.with(TemporalAdjusters.firstDayOfMonth());
            } else {
                due = due.truncatedTo(type);
            }

            return due;
        }).sorted(Comparator.naturalOrder())
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        Map<LocalDateTime, Long> sortedMap = new TreeMap<LocalDateTime, Long>(data);

        return sortedMap;
    }

    private Map<String, LocalDateTime> getPeriod(MetricType type) {

        Map<String, LocalDateTime> result = new HashMap<>();

        switch (type) {
            case DAILY:
                result.put(START, LocalDate.now().atStartOfDay());
                result.put(END, LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
                return result;

            case MONTHLY:
                result.put(START, LocalDate.now().atStartOfDay().withDayOfMonth(1));
                int days = Utils.getDaysOfMonth(LocalDate.now());
                result.put(END, LocalDate.now().atStartOfDay().withDayOfMonth(days));
                return result;
            case WEEKLY:

                LocalDate now = LocalDate.now();

                DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
                LocalDate startOfCurrentWeek = now.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
                result.put(START, startOfCurrentWeek.atStartOfDay());
                DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(6);
                LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
                result.put(END, LocalDateTime.of(endOfWeek, LocalTime.MAX));

                return result;
            case YEARLY:

                LocalDateTime start = LocalDate.now().atStartOfDay().withDayOfYear(1);
                result.put(START, start);
                LocalDateTime lastDayOfYear = start.with(TemporalAdjusters.lastDayOfYear());
                lastDayOfYear = lastDayOfYear.of(lastDayOfYear.toLocalDate(), LocalTime.MAX);
                result.put(END, lastDayOfYear);
                return result;
            default:
                return null;
        }

    }

    public UserMetricResponse userOpenClosedTasksMetrics(MetricType type, String userId) {

        Map<String, LocalDateTime> filter = this.getPeriod(type);

        Long taskClosed = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .taskCompletedAfter(Utils.localDateTimeToDate(filter.get(START)))
                .taskCompletedBefore(Utils.localDateTimeToDate(filter.get(END)))
                .finished()
                .count();

        Long taskOpen = taskService
                .createTaskQuery()
                .active()
                .taskAssignee(userId)
                .taskCreatedAfter(Utils.localDateTimeToDate(filter.get(START)))
                .taskCreatedBefore(Utils.localDateTimeToDate(filter.get(END)))
                .count();

        long total = taskClosed + taskOpen;
        double percentOpen = taskOpen / (double) total;
        double percentClosed = taskClosed / (double) total;

        UserMetricResponse response = new UserMetricResponse<Double>();
        response.setType(type);
        response.setStart(filter.get(START).format(DateTimeFormatter.ISO_DATE_TIME));
        response.setEnd(filter.get(END).format(DateTimeFormatter.ISO_DATE_TIME));
        Map<String, Double> metrics = new HashMap();
        metrics.put("PERCENT_OPEN", percentOpen);
        metrics.put("PERCENT_CLOSED", percentClosed);
        metrics.put("OPEN_TOTAL", taskOpen.doubleValue());
        metrics.put("CLOSED_TOTAL", taskClosed.doubleValue());
        response.setData(metrics);

        return response;

    }

    public UserMetricResponse userTimeOnTaskMetric(MetricType type, String userId) {

        Map<String, LocalDateTime> filter = this.getPeriod(type);

        List<HistoricTaskInstance> result = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .taskCompletedAfter(Utils.localDateTimeToDate(filter.get(START)))
                .taskCompletedBefore(Utils.localDateTimeToDate(filter.get(END)))
                .finished()
                .list();

        long count = result
                .stream()
                .filter(t -> Objects.nonNull(t.getDurationInMillis())).count();

        result
                .stream()
                .filter(t -> Objects.nonNull(t.getDurationInMillis()))
                .mapToLong(HistoricTaskInstance::getDurationInMillis).forEach(l -> {

        });

        OptionalDouble value = result
                .stream()
                .filter(t -> Objects.nonNull(t.getDurationInMillis()))
                .mapToLong(HistoricTaskInstance::getDurationInMillis)
                .average();

        long rr = value.isPresent() ? Double.valueOf(value.getAsDouble()).longValue() : 0L;

        UserMetricResponse response = new UserMetricResponse<Long>();
        response.setType(type);
        response.setStart(filter.get(START).format(DateTimeFormatter.ISO_DATE_TIME));
        response.setEnd(filter.get(END).format(DateTimeFormatter.ISO_DATE_TIME));
        Map<String, Long> metrics = new HashMap();
        metrics.put("TIME", TimeUnit.MILLISECONDS.toMinutes(rr));
        response.setData(metrics);

        return response;

    }

}
