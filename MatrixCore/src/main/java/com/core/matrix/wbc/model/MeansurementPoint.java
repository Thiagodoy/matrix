/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.model;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@SqlResultSetMapping(name = "pointInformation",
        classes = @ConstructorResult(
                targetClass = MeansurementPoint.class,
                columns = {
                    @ColumnResult(name = "nCdPontoMedicao", type = Long.class)
                    ,
                    @ColumnResult(name = "nCdEmpresa", type = Long.class)
                    ,
                    @ColumnResult(name = "sDsPontoMedicao", type = String.class)
                    ,
                    @ColumnResult(name = "sCdImportacaoPontoMedicao", type = String.class)
                    ,
                    @ColumnResult(name = "nIdTipoPonto", type = Long.class)
                }))
@NamedNativeQuery(query = "SELECT [nCdPontoMedicao]\n"
        + "      ,[nCdEmpresa]\n"
        + "      ,[sDsPontoMedicao]\n"
        + "      ,[sCdImportacaoPontoMedicao]\n"
        + "      ,[nIdTipoPonto]\n"
        + "  FROM [CE_PONTO_MEDICAO]\n"
        + "  WHERE nCdEmpresa in (SELECT [nCdEmpresa]\n"
        + "  FROM [EMPRESA_GRUPO]\n"
        + "  WHERE nCdEmpresaPai in :companys )\n"
        + "UNION\n"
        + "SELECT [nCdPontoMedicao]\n"
        + "      ,[nCdEmpresa]\n"
        + "      ,[sDsPontoMedicao]\n"
        + "      ,[sCdImportacaoPontoMedicao]\n"
        + "      ,[nIdTipoPonto]\n"
        + "  FROM [CE_PONTO_MEDICAO]\n"
        + "  WHERE nCdEmpresa in :companys", resultSetMapping = "pointInformation", name = "MeansurementPoint.listByCompanys")

@Entity
@Table(name = "CE_PONTO_MEDICAO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeansurementPoint {

    @Id
    @Column(name = "nCdPontoMedicao")
    private Long id;

    @Column(name = "nCdEmpresa")
    private Long company;

    @Column(name = "sDsPontoMedicao")
    private String descriptionPoint;

    @Column(name = "sCdImportacaoPontoMedicao")
    private String code;

    @Column(name = "nIdTipoPonto")
    private Long type;

}
