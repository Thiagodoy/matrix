/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.ContractStatusSummaryDTO;
import com.core.matrix.utils.ContractStatus;
import java.time.LocalDate;
import java.util.Observable;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@SqlResultSetMapping(name = "contractStatusSummary", classes = @ConstructorResult(targetClass = ContractStatusSummaryDTO.class, columns = {
    @ColumnResult(name = "status", type = String.class)
    ,
        @ColumnResult(name = "qtd", type = Long.class)
}))
@NamedNativeQuery(
        name = "ContractMtxStatus.summary",
        query = "select status, count(1) as qtd from mtx_contrato_status where mes = :month and ano = :year group by status",
        resultSetMapping = "contractStatusSummary")

@Entity
@Table(name = "mtx_contrato_status")
@Data
@NoArgsConstructor
public class ContractMtxStatus extends Observable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "wbc_contrato")
    private Long wbcContract;

    @Column(name = "apelido")
    private String nickname;

    @Column(name = "flat")
    private boolean isFlat;

    @Column(name = "unidade_consumidora")
    private boolean isUnitConsumer;

    @Column(name = "rateio")
    private boolean isApportionment;

    @Column(name = "razao_status")
    private String reasonStatus;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "montante_liquido")
    private Double amountLiquid;

    @Column(name = "montante_bruto")
    private Double amountGross;

    @Column(name = "mes")
    private Long month;

    @Column(name = "ano")
    private Long year;

    public ContractMtxStatus(ContractMtx contractMtx) {
        this.wbcContract = contractMtx.getWbcContract();
        this.nickname = contractMtx.getNickname();
        this.isFlat = contractMtx.getIsFlat();
        this.isApportionment = contractMtx.getIsApportionment();
        this.isUnitConsumer = contractMtx.getIsConsumerUnit();
        this.status = ContractStatus.NO_BILL;        
        this.month = (long) LocalDate.now().minusMonths(1).getMonthValue();
        this.year = (long) LocalDate.now().getYear();
    }

    public void forceUpdate() {
        this.setChanged();
        this.notifyObservers(Boolean.TRUE);
        this.clearChanged();

    }

}
