/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    static {
        bcpe = new BCryptPasswordEncoder();
        sdfHour = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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

        FileUtils.copyInputStreamToFile(initialStream, targetFile);
        return targetFile;
    }

}
