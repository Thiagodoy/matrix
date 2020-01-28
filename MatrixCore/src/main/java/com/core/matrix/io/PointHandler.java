/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.io;

import java.util.regex.Pattern;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

/**
 *
 * @author thiag
 */
public class PointHandler implements TypeHandler {

    private static final String PATTERN = "[a-zA-Z0-9-\\s\\(\\)]+";
    private Pattern pattern = Pattern.compile(PATTERN);
    private static final String MESSAGE_ERROR = "Ponto de medição é obrigatório!";

    @Override
    public Object parse(String text) throws TypeConversionException {

        if (pattern.matcher(text).matches()) {
            return text;
        } else {
            throw new TypeConversionException(MESSAGE_ERROR);
        }

    }

    @Override
    public String format(Object value) {
        return String.valueOf(value);
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

}
