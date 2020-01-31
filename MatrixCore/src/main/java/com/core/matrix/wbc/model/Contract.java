/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.model;

import com.core.matrix.wbc.dto.ContractDTO;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */

@SqlResultSetMapping(name = "contractDTO", 
        classes = @ConstructorResult(
                targetClass = ContractDTO.class,
                columns = {
                        @ColumnResult(name = "nCdContrato", type = Long.class),
                        @ColumnResult(name = "sNrContrato", type = String.class),
                        @ColumnResult(name = "sNmContrato", type = String.class),
                        @ColumnResult(name = "tDdInicio", type = LocalDateTime.class),
                        @ColumnResult(name = "tDdTermino", type = LocalDateTime.class),
                        @ColumnResult(name = "nCdSubmercado", type = Long.class),
                        @ColumnResult(name = "sDsSubmercado", type = String.class),
                        @ColumnResult(name = "nCdTipo", type = Long.class),
                        @ColumnResult(name = "sDsTipo", type = String.class),
                        @ColumnResult(name = "nCdEmpresaContratada", type = Long.class),
                        @ColumnResult(name = "sNmEmpresaEpca", type = String.class),
                        @ColumnResult(name = "nCdEmpresaContratante", type = Long.class),
                        @ColumnResult(name = "sNmEmpresaEpce", type = String.class),
                        @ColumnResult(name = "bFlPublicado", type = Long.class),
                        @ColumnResult(name = "nCdSituacaoContrato", type = Long.class),
                        @ColumnResult(name = "sDsSituacaoContrato", type = String.class),
                        @ColumnResult(name = "sDsObservacao", type = String.class),
                        @ColumnResult(name = "nCdContratoPai", type = Long.class),
                        @ColumnResult(name = "sNrReferencia", type = String.class),
                        @ColumnResult(name = "nCdAgrupador", type = Long.class),
                        @ColumnResult(name = "sDsAgrupador", type = String.class)              
                    
                }))


 @NamedNativeQuery(query = "SELECT COUNT AS 'TOTALDECONTRATOS'\n" +
"  FROM [CE_CONTRATO] CT\n" +
" WHERE CT.[nCdSituacaoContrato] in (2,8)\n" +
"   AND CT.[sNrContrato] IN (SELECT CC.[nCdContrato]\n" +
"  FROM [CE_CONTRATO_CLASSIFICADOR] CC,\n" +
"       [CE_CLASSIFICADOR] CL\n" +
"  WHERE CC.sCdClassificador = CL.sCdClassificador\n" +
"    AND CL.[sNmClassificador] = 'PORTAL')", name = "Contract.countContract")


@NamedNativeQuery(query = "SELECT "
     + "CT.[nCdContrato]\n" +
"      ,CT.[sNrContrato]\n" +
"      ,CT.[sNmContrato]\n" +
"      ,CT.[tDdInicio]\n" +
"      ,CT.[tDdTermino]\n" +
"      ,CT.[nCdSubmercado]\n" +
"      ,SM.[sDsSubmercado]\n" +
"      ,CT.[nCdTipo]\n" +
"      ,TP.[sDsTipo]\n" +
"      ,CT.[nCdEmpresaContratada]\n" +
"      ,EPCA.[sNmEmpresa] as sNmEmpresaEpca\n" +
"      ,CT.[nCdEmpresaContratante]\n" +
"      ,EPCE.[sNmEmpresa] as sNmEmpresaEpce\n" +
"      ,CT.[bFlPublicado]\n" +
"      ,CT.[nCdSituacaoContrato]\n" +
"      ,SC.[sDsSituacaoContrato]\n" +
"      ,CT.[sDsObservacao]\n" +
"      ,CT.[nCdContratoPai]\n" +
"      ,CT.[sNrReferencia]\n" +
"      ,CT.[nCdAgrupador]\n" +
"      ,CA.[sDsAgrupador]\n" +
"  FROM [CE_CONTRATO] CT\n" +
"      ,[CE_SUBMERCADO] SM\n" +
"	  ,[CE_TIPO_CONTRATO] TP\n" +
"	  ,[EMPRESA] EPCA\n" +
"	  ,[EMPRESA] EPCE\n" +
"	  ,[CE_SITUACAO_CONTRATO] SC\n" +
"	  ,[CE_CONTRATO_AGRUPADOR] CA\n" +
" WHERE CT.nCdSubmercado = SM.nCdSubmercado\n" +
"   AND CT.nCdTipo = TP.nCdTipo\n" +
"   AND CT.nCdEmpresaContratada = EPCA.[nCdEmpresa]\n" +
"   AND CT.nCdEmpresaContratante = EPCE.[nCdEmpresa]\n" +
"   AND CT.[nCdSituacaoContrato] = SC.nCdSituacaoContrato\n" +
"   AND CT.[nCdAgrupador] = CA.nCdAgrupador\n" +
"   AND CT.[nCdSituacaoContrato] = 8\n" +
"   AND CT.[sNrContrato] = :contractId", name = "Contract.shortInfomation", resultSetMapping = "contractDTO")

@Entity
@Table(name = "CE_CONTRATO")
@Data
public class Contract {   
    
    @Id
    @Column(name = "nCdContrato")
    private Long id;
}
