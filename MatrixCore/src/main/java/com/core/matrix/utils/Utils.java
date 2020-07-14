/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import com.core.matrix.annotation.PositionBatchParameter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.beanio.StreamFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author thiag
 */
@Component
public class Utils {

    private static BCryptPasswordEncoder bcpe;
    private static SimpleDateFormat sdfHour;
    private static JaroWinklerDistance n = new JaroWinklerDistance();
    private static final Map<String, String> entitiesHtml = new HashMap<String, String>();

    private static DateTimeFormatter dateTimeFormatter;
    private static SimpleDateFormat formmatDate;

    static {
        bcpe = new BCryptPasswordEncoder();
        sdfHour = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        entitiesHtml.put("à", "&#224;");
        entitiesHtml.put("è", "&#232;");
        entitiesHtml.put("ì", "&#236;");
        entitiesHtml.put("ò", "&#242;");
        entitiesHtml.put("ù", "&#249;");
        entitiesHtml.put("À", "&#192;");
        entitiesHtml.put("È", "&#200;");
        entitiesHtml.put("Ì", "&#204;");
        entitiesHtml.put("Ò", "&#210;");
        entitiesHtml.put("Ù", "&#217;");
        entitiesHtml.put("á", "&#225;");
        entitiesHtml.put("é", "&#233;");
        entitiesHtml.put("í", "&#237;");
        entitiesHtml.put("ó", "&#243;");
        entitiesHtml.put("ú", "&#250;");
        entitiesHtml.put("ý", "&#253;");
        entitiesHtml.put("Á", "&#193;");
        entitiesHtml.put("É", "&#201;");
        entitiesHtml.put("Í", "&#205;");
        entitiesHtml.put("Ó", "&#211;");
        entitiesHtml.put("Ú", "&#218;");
        entitiesHtml.put("Ý", "&#221;");
        entitiesHtml.put("â", "&#226;");
        entitiesHtml.put("ê", "&#234;");
        entitiesHtml.put("î", "&#238;");
        entitiesHtml.put("ô", "&#244;");
        entitiesHtml.put("û", "&#251;");
        entitiesHtml.put("Â", "&#194;");
        entitiesHtml.put("Ê", "&#202;");
        entitiesHtml.put("Î", "&#206;");
        entitiesHtml.put("Ô", "&#212;");
        entitiesHtml.put("Û", "&#219;");
        entitiesHtml.put("ã", "&#227;");
        entitiesHtml.put("ñ", "&#241;");
        entitiesHtml.put("õ", "&#245;");
        entitiesHtml.put("Ã", "&#195;");
        entitiesHtml.put("Ñ", "&#209;");
        entitiesHtml.put("Õ", "&#213;");
        entitiesHtml.put("ä", "&#228;");
        entitiesHtml.put("ë", "&#235;");
        entitiesHtml.put("ï", "&#239;");
        entitiesHtml.put("ö", "&#246;");
        entitiesHtml.put("ü", "&#252;");
        entitiesHtml.put("ÿ", "&#255;");
        entitiesHtml.put("Ä", "&#196;");
        entitiesHtml.put("Ë", "&#203;");
        entitiesHtml.put("Ï", "&#207;");
        entitiesHtml.put("Ö", "&#214;");
        entitiesHtml.put("Ü", "&#220;");
        entitiesHtml.put("ç", "&#231;");
        entitiesHtml.put("Ç", "&#199;");

        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        formmatDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    }

    public static synchronized double checkDistance(String one, String two) {
        return n.apply(one.toUpperCase(), two.toUpperCase());
    }

    public static String encodePassword(String in) {
        return bcpe.encode(in);
    }

    public synchronized static String dateTimeNowFormated() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public synchronized static String dateToString(Date date) {
        return sdfHour.format(date);
    }

    public static synchronized InputStream generateInpuStream(MultipartFile file) throws FileNotFoundException, IOException {

        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        convFile.deleteOnExit();

        return new FileInputStream(convFile);

    }

    public static Map<String, String> toMap(String value) throws IOException {
        return new ObjectMapper().readValue(value, HashMap.class);
    }

    public static int getDaysOfMonth(LocalDate date) {
        return YearMonth.of(date.getYear(), Month.of(date.getMonthValue())).lengthOfMonth();
    }

    public static String replaceAccentToEntityHtml(String value) {

        for (int i = 0; i < value.length(); i++) {
            String letter = Character.toString(value.charAt(i));

            if (entitiesHtml.containsKey(letter)) {
                value = value.replace(letter, entitiesHtml.get(letter));
            }
        }
        return value;
    }

    public static File loadLogo(String path)
            throws IOException {
        StreamFactory factory = StreamFactory.newInstance();
        InputStream initialStream = factory.getClass().getClassLoader().getResourceAsStream(path);

        File targetFile = File.createTempFile("logo", ".png");
        targetFile.deleteOnExit();
        FileUtils.copyInputStreamToFile(initialStream, targetFile);
        return targetFile;
    }

    public synchronized static String mapToString(Map ma) {
        try {
            return new ObjectMapper().writeValueAsString(ma);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public static Date localDateToDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date localDateTimeToDate(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    public synchronized static LocalDateTime dateToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public synchronized static String getStatus(String name) {
        return getStatus(MeansurementFileStatus.valueOf(name));
    }

    public synchronized static String getStatus(MeansurementFileStatus status) {

        switch (status) {

            case APPROVED:
                return "Processado com sucesso";
            case SUCCESS:
                return "Em andamento";
            case DATA_CALENDAR_ERROR:
                return "Arquivo com calendario Inválido";
            case DATA_DAY_ERROR:
                return "Arquivo com dias faltando";
            case DATA_HOUR_ERROR:
                return "Arquivo com horas faltando";
            case FILE_ERROR:
                return "Arquivo inválido";
            case POINT_ERROR:
                return "Arquivo com ponto não cadastrado na base da [Matrix]";
            case FILE_PENDING:
                return "Pendente";
            case LAYOUT_ERROR:
                return "Arquivo com layout inválido";
            default:
                return "Arquivo sem estatus definido";
        }

    }

    public static <T> List<String> mountBatchInsert(List<T> collection) {

        List<String> parameters = new ArrayList<String>();
        TreeMap<Integer, String> values = new TreeMap<>();
        
        List<String> records = new ArrayList<String>();
        

        Optional<T> entity = collection.stream().findFirst();

        if (entity.isPresent()) {

        } else {
            return Collections.emptyList();
        }

        List<Field> fields = new CopyOnWriteArrayList<>(Arrays.asList(entity.get().getClass().getDeclaredFields())
                .stream()
                .filter(f -> f.isAnnotationPresent(PositionBatchParameter.class))
                .collect(Collectors.toList()));

        collection.forEach(element -> {
            fields.forEach(f -> {
                try {
                    Object value = f.get(element);
                    Integer index = f.getAnnotation(PositionBatchParameter.class).value();
                    putParameter(values, index, value);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
                }
            });

            for (Integer key : values.keySet()) {
                parameters.add(values.get(key));
            }            
            String record = "(" + parameters.stream().collect(Collectors.joining(",")) + ")";
            records.add(record);
            parameters.clear();
        });
     
        return records;
    }

    private static void putParameter(TreeMap<Integer, String> parameters, Integer index, Object value) {

        if (value == null) {
            parameters.put(index, "NULL");
        } else if (value instanceof String) {
            parameters.put(index, "'" + value.toString().replace("'", " ") + "'");
        } else if ((value instanceof Long) || (value instanceof Integer) || (value instanceof Double)) {
            parameters.put(index, value.toString());
        } else if (value instanceof LocalDateTime) {
            parameters.put(index, "'" + ((LocalDateTime) value).format(dateTimeFormatter) + "'");
        } else if (value instanceof Date) {
            parameters.put(index, "'" + formmatDate.format(value) + "'");
        } else {
            parameters.put(index, "'" + value.toString() + "'");
        }

    }
}
