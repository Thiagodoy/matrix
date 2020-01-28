/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

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
    
    
    static {
        bcpe = new BCryptPasswordEncoder();
        sdfHour = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public static String encodePassword(String in) {
        return bcpe.encode(in);
    }
    
    public synchronized static String dateTimeNowFormated(){
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
    
    public synchronized static String dateToString(Date date){
        return sdfHour.format(date);
    }
    
    
    public static synchronized InputStream generateInpuStream(MultipartFile file) throws FileNotFoundException, IOException{
        
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();       
        convFile.deleteOnExit();
        
        return new FileInputStream(convFile);
        
    }
    
    public static int  getDaysOfMonth(LocalDate date){
        return YearMonth.of(date.getYear(), Month.of(date.getMonthValue())).lengthOfMonth();
    }


}
