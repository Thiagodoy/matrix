/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@AllArgsConstructor
public class ContractWbcInformationDTO implements Serializable{

    private String nrContract;
    private Long nrYear;
    private Long nrMonth;
    private Double nrQtd;
    private Double nrQtdMin;
    private Double nrQtdMax;
    private Double percentMin;
    private Double percentMax;
    private Double qtdHired;
    private Double qtdHiredMin;
    private Double qtdHiredMax;
    private Double limitMin;
    private Double limitMax;

}
