/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * @author thiag
 */

public class ReportConstants {

    public enum ReportType {
        EXPORT_RESULT_WBC,
        EXPORT_RESULT_FULL_WBC,
        FULL,
        SHORT
    }
    
    public List<Field> fields;
    
    

    public static String[] getHeaders(ReportType type) {

        switch (type) {
            case EXPORT_RESULT_FULL_WBC:
                return new String[]{"Ano", "Mês", "Cód. CCEE", "Número do Contrato", "Ponto de Medição", "Status", "Montantes (MWh)","Valor Faturado (WBC)"};
            case EXPORT_RESULT_WBC:
                return new String[]{"Ano", "Mês", "Cód. CCEE", "Número do Contrato", "Montantes (MWh)"};
            default:
                return new String[]{};
        }
    }

}
