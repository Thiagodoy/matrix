/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import com.core.matrix.annotation.ReportColumn;
import com.core.matrix.utils.ReportConstants;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data

@NoArgsConstructor
public class MeansurementFileResultStatusDTO {

    public Long id;

    @ReportColumn(name = "ANO", position = 0, typeValue = Long.class, typeReport = {"FULL", "SHORT"})
    public Long year;

    @ReportColumn(name = "MES", position = 1, typeValue = Long.class, typeReport = {"FULL", "SHORT"})
    public Long month;

    @ReportColumn(name = "CONTRATO", position = 3, typeValue = Long.class, typeReport = {"FULL", "SHORT"})
    public Long wbcContract;

    @ReportColumn(name = "PONTO MEDIÇÃO", position = 4, typeValue = String.class, typeReport = {"FULL"})
    public String wbcMeansurementPoint;

    @ReportColumn(name = "MONTANTE", position = 6, typeValue = String.class, typeReport = {"FULL", "SHORT"})
    public Double mount;

    @ReportColumn(name = "STATUS", position = 5, typeValue = String.class, typeReport = {"FULL"})
    public String status;

    @ReportColumn(name = "CÓD. CCEE", position = 2, typeValue = String.class, typeReport = {"FULL", "SHORT"})
    public String scde;

    public Date dataCriacao;

    public Boolean isExported;

    public String empresa;

    public String responsavel;

    public MeansurementFileResultStatusDTO(Long id, Long year, Long month, Long wbcContract, String wbcMeansurementPoint, Double mount, String status, Date dataCriacao, Boolean isExported,String empresa, String responsavel) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.wbcContract = wbcContract;
        this.wbcMeansurementPoint = wbcMeansurementPoint;
        this.mount = mount;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.isExported = isExported;
        this.scde = "";
        this.empresa = empresa;
        this.responsavel = responsavel;

    }

    public Object[] export(ReportConstants.ReportType type) {

        NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));

        String year = nf.format(this.year).replaceAll("\\.", "");
        String contracto = nf.format(this.wbcContract).replaceAll("\\.", "");

        switch (type) {
            case EXPORT_RESULT_FULL_WBC:
                return new Object[]{year, this.month, "", contracto, this.wbcMeansurementPoint, this.status, this.mount};
            case EXPORT_RESULT_WBC:
                return new Object[]{year, this.month, "", contracto, this.mount};
            default:
                return new Object[]{};
        }

    }

}
