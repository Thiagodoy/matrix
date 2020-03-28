/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.model;

import com.core.matrix.wbc.dto.SubMercadoDTO;
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
 * @author aloysio
 */

@SqlResultSetMapping(name = "SubMercadoDTO", 
        classes = @ConstructorResult(
                targetClass = SubMercadoDTO.class,
                columns = {
                        @ColumnResult(name = "nCdSubmercado", type = Integer.class),
                        @ColumnResult(name = "sDsSubmercado", type = String.class),                                                  
                }))

@NamedNativeQuery(name = "SubMercado.list",
        resultSetMapping = "SubMercadoDTO",
        query = "SELECT [nCdSubmercado]\n" +
                "      ,[sDsSubmercado]\n" +
                "  FROM [CE_SUBMERCADO]\n" +
                " WHERE [nCdSubmercado] > 0\n" +                
                " ORDER BY [nCdSubmercado]")


@Table
@Entity
@Data
public class SubMercado {

    @Id
    @Column(name = "nCdSubmercado")
    private Integer nCdEmpresa;

    @Column(name = "sDsSubmercado")
    private String sDsSubmercado;  
    
}
