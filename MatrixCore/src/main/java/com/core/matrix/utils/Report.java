/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import com.core.matrix.annotation.ReportColumn;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author thiag
 */
public interface Report {

    default Object[] export() {

        List<Field> fields = Arrays.asList(this.getClass().getDeclaredFields());        
        
        fields = fields
                .stream()
                .filter(f -> f.isAnnotationPresent(ReportColumn.class))
                .sorted((a,b)->{
                    
                    ReportColumn r1 = a.getAnnotation(ReportColumn.class);
                    ReportColumn r2 = b.getAnnotation(ReportColumn.class);
                    
                    return Integer.compare(r1.position(), r2.position());                    
                    
                })                
                .collect(Collectors.toList());
        
        Object [] values = new Object[fields.size()];
        
        fields.forEach(f->{
        
            ReportColumn reportColumn = f.getAnnotation(ReportColumn.class);
            try {
                values[reportColumn.position()] = f.get(this);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Report.class.getName()).log(Level.SEVERE, null, ex);
            }           
        
        });
        
        
            
        

        return values;
    }
}
