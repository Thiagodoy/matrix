/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import com.core.matrix.utils.ReportConstants;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
public class MeansurementFileResultStatusDTO {

    private Long id;
    private Long year;
    private Long month;
    private Long wbcContract;
    private String wbcMeansurementPoint;
    private Double mount;
    private String status;
    private Date dataCriacao;

    public Object[] export(ReportConstants.ReportType type) {

        NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));

        String year = nf.format(this.year).replaceAll("\\.", "");
        String contracto = nf.format(this.wbcContract).replaceAll("\\.", "");

        switch (type) {
            case EXPORT_RESULT_FULL_WBC:
                return new Object[]{year, this.month, "", contracto, this.wbcMeansurementPoint, this.status, this.mount};
            case EXPORT_RESULT_WBC:                
                return new Object[]{year, this.month, "",contracto,  this.mount};
            default:
                return new Object[]{};
        }

    }

}
