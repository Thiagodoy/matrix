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

    static {
        bcpe = new BCryptPasswordEncoder();
    }

    public static String encodePassword(String in) {
        return bcpe.encode(in);
    }
    
    
    public static synchronized InputStream generateInpuStream(MultipartFile file) throws FileNotFoundException, IOException{
        
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();       
        
        return new FileInputStream(convFile);
        
    }

}
