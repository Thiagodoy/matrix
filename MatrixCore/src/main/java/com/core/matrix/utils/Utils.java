/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */

@Component
public class Utils {
    
    private static BCryptPasswordEncoder bcpe;
    
    
    static{
        bcpe = new BCryptPasswordEncoder();
    }
    
    
  public static  String encodePassword(String in)  {
      return bcpe.encode(in);
  }
    
}
