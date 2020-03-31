/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.model;

import com.core.matrix.wbc.dto.ProdutoDTO;
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

@SqlResultSetMapping(name = "ProdutoDTO", 
        classes = @ConstructorResult(
                targetClass = ProdutoDTO.class,
                columns = {
                        @ColumnResult(name = "nCdPerfilCCEE", type = Integer.class),
                        @ColumnResult(name = "sDsSiglaCCEE", type = String.class),
                        @ColumnResult(name = "sDsPerfilCCEE", type = String.class),
                }))

@NamedNativeQuery(name = "Produto.list",
        resultSetMapping = "ProdutoDTO",
        query = "SELECT sc.nCdPerfilCCEE\n" +
                "      ,sc.sDsSiglaCCEE\n" +
                "      ,pc.sDsPerfilCCEE\n" +
                "FROM CE_SIGLA_CCEE sc\n" +
                "    ,CE_PERFIL_CCEE pc\n" +
                "WHERE sc.nCdPerfilCCEE = pc.nCdPerfilCCEE\n" +
                "and sc.nCdEmpresa = 3\n" +
                "ORDER BY pc.sDsPerfilCCEE\n")

@Table
@Entity
@Data
public class Produto {

    @Id
    @Column(name = "nCdPerfilCCEE")
    private Integer nCdPerfilCCEE;

    @Column(name = "sDsSiglaCCEE")
    private String sDsSiglaCCEE;  

    @Column(name = "sDsPerfilCCEE")
    private String sDsPerfilCCEE;      
}
