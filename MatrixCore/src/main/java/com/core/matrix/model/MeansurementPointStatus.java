/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.PointStatusSummaryDTO;
import com.core.matrix.utils.PointStatus;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@SqlResultSetMapping(name = "pointStatusMap", classes = @ConstructorResult(targetClass = PointStatusSummaryDTO.class, columns = {
    @ColumnResult(name = "status", type = String.class)
    ,
    @ColumnResult(name = "qtd", type = Long.class)
}))

@NamedNativeQuery(name = "MeansurementPointStatus.summary",
        query = "SELECT status, count(1) as qtd FROM matrix.mtx_ponto_de_medicao_status where ano = :year and mes = :month group by status",
        resultSetMapping = "pointStatusMap")

@Entity
@Table(name = "mtx_ponto_de_medicao_status")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class MeansurementPointStatus extends Observable implements Model<MeansurementPointStatus> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Column(name = "mes")
    protected Long month;

    @Column(name = "ano")
    protected Long year;

    @Column(name = "wbc_ponto")
    protected String point;

    @Column(name = "empresa")
    protected String company;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15)
    protected PointStatus status;

    @Column(name = "total_scde")
    protected Double mountScde;

    @Column(name = "montante_bruto")
    protected Double amountGross;

    @Column(name = "montante_liquido")
    protected Double amountLiquid;

    @Column(name = "horas_faltantes")
    protected Long hours;

    public MeansurementPointStatus(String point, Long month, Long year) {
        this.point = point;
        this.status = PointStatus.NO_READ;
        this.month = month;
        this.year = year;
    }

    public void forceUpdate() {
        this.setChanged();
        this.notifyObservers(Boolean.TRUE);
        this.clearChanged();

    }

}
