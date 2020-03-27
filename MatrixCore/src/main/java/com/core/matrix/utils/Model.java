/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author thiag
 */
public interface Model<T> {

    default void merge(T t) {
        List<Field> fields =  Arrays.asList(t.getClass().getDeclaredFields());
        
        //fields
        
        
        
    }

}
