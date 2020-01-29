/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.dto;

import com.core.matrix.wbc.model.Empresa;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor()
@NoArgsConstructor
public class EmpresaDTO implements Serializable{

    private Long nCdEmpresa;
    private String sNrCnpj;
    private String sNmEmpresa;
    private String sNmFantasia;
    private String sNmApelido;
    private Long nCdTipoAgente;
    private String sDsTipoAgente;

    public EmpresaDTO(Empresa empresa, String dsTipoAgent) {
        this.nCdEmpresa = empresa.getNCdEmpresa();
        this.sNrCnpj = empresa.getNrCnpj();
        this.sNmEmpresa = empresa.getEmpresa();
        this.sNmFantasia = empresa.getSNmFantasia();
        this.sNmApelido = empresa.getApelido();
        this.nCdTipoAgente = empresa.getNCdTipoAgente();
        this.sDsTipoAgente = dsTipoAgent;
    }

    public EmpresaDTO(Long nCdEmpresa, String sNrCnpj, String sNmEmpresa, String sNmFantasia, String sNmApelido) {
        this.nCdEmpresa = nCdEmpresa;
        this.sNrCnpj = sNrCnpj;
        this.sNmEmpresa = sNmEmpresa;
        this.sNmFantasia = sNmFantasia;
        this.sNmApelido = sNmApelido;

    }

}
