/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.io;

import com.core.matrix.utils.Utils;
import java.text.MessageFormat;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

/**
 *
 * @author thiag
 */
public class ReasonSituationHandler implements TypeHandler {

    private static final String MESSAGE_ERROR = "Motivo da situação não foi preenchido, com nenhuma das constantes definida para esse campo: \n ( {0}, {1}, {2},{3} )";

    private static final String CONST_1 = "Consistido";
    private static final String CONST_2 = "Coleta Diária";
    private static final String CONST_3 = "Hora Ajustada";
    private static final String CONST_4 = "Inspeção Lógica";

    @Override
    public Object parse(String text) throws TypeConversionException {

        if ((text != null && text.length() > 0) && (Utils.checkDistance(CONST_1, text) > 0.95
                || (Utils.checkDistance(CONST_2, text) > 0.95)
                || (Utils.checkDistance(CONST_3, text) > 0.95)
                || (Utils.checkDistance(CONST_4, text) > 0.95))) {
            return text;
        } else {
            throw new TypeConversionException(MessageFormat.format(MESSAGE_ERROR, CONST_1, CONST_2, CONST_3, CONST_4));
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
