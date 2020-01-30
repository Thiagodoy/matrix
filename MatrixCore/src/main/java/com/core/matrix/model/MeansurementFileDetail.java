/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.dto.FileDetailDTO;
import com.core.matrix.utils.MeansurementFileType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_arquivo_de_medicao_detalhe")
@Data
@NoArgsConstructor
public class MeansurementFileDetail implements Serializable {

    @Id
    @Column(name = "id_arquivo_de_medicao_detalhe")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_arquivo_de_medicao")
    private Long idMeansurementFile;

    @Column(name = "ponto_medicao")
    private String meansurementPoint;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "data")
    private LocalDate date;

    @Column(name = "hora")
    private Long hour;

    @Column(name = "tipo_energia")
    private String energyType;

    @Column(name = "ativa_geracao")
    private Double generationActive;

    @Column(name = "ativa_consumo")
    private Double consumptionActive;

    @Column(name = "reativa_geracao")
    private Double generationReactivate;

    @Column(name = "reativa_consumo")
    private Double consumptionReactivate;

    @Column(name = "situacao_da_medida")
    private String situation;

    @Column(name = "motivo_da_situacao")
    private String reasonOfSituation;

    @Column(name = "intervalo")
    private Long range;

    @Column(name = "origem_coleta")
    private String sourceCollection;

    @Column(name = "notificacao_coleta")
    private String notificationCollection;

    @Column(name = "agente")
    private String agent;

    @Column(name = "qualidade")
    private String quality;

    @Column(name = "origem")
    private String origem;

    @Transient
    private List<String> erros;

    public MeansurementFileDetail(LocalDate date, Long fileId, String meansurementPoint) {
        this.date = date;
        this.idMeansurementFile = fileId;
        this.meansurementPoint = meansurementPoint;
    }

    public MeansurementFileDetail(LocalDate date, Long hour, Long fileId, String meansurementPoint) {
        this.date = date;
        this.hour = hour;
        this.idMeansurementFile = fileId;
        this.meansurementPoint = meansurementPoint;
    }

    public MeansurementFileDetail(FileDetailDTO detail, MeansurementFileType type) {

        DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        switch (type) {
            case LAYOUT_A:
                this.meansurementPoint = detail.getMeansurementPoint();
                this.date = LocalDate.parse(detail.getDate(), formater);
                this.hour = Long.valueOf(detail.getHour());
                this.energyType = detail.getEnergyType();
                this.generationActive = Double.parseDouble(detail.getGenerationActive().replaceAll(",", "."));
                this.consumptionActive = Double.parseDouble(detail.getConsumptionActive().replaceAll(",", "."));
                this.generationReactivate = Double.parseDouble(detail.getGenerationReactivate().replaceAll(",", "."));
                this.consumptionReactivate = Double.parseDouble(detail.getConsumptionReactivate().replaceAll(",", "."));
                this.range = Long.parseLong(detail.getRange());
                this.situation = detail.getSituation();
                this.reasonOfSituation = detail.getReason();
                break;

            case LAYOUT_B:
                this.meansurementPoint = detail.getMeansurementPoint();
                this.date = LocalDate.parse(detail.getDate(), formater);
                this.hour = Long.valueOf(detail.getHour());
                this.energyType = detail.getEnergyType();
                this.generationActive = Double.parseDouble(detail.getGenerationActive().replaceAll(",", "."));
                this.consumptionActive = Double.parseDouble(detail.getConsumptionActive().replaceAll(",", "."));
                this.generationReactivate = Double.parseDouble(detail.getGenerationReactivate().replaceAll(",", "."));
                this.consumptionReactivate = Double.parseDouble(detail.getConsumptionReactivate().replaceAll(",", "."));
                this.sourceCollection = detail.getSourceCollection();
                this.notificationCollection = detail.getNotificationCollection();
                break;
            case LAYOUT_C:
                this.agent = detail.getAgent();
                this.meansurementPoint = detail.getMeansurementPoint();
                this.date = LocalDate.parse(detail.getDate(), formater);
                this.hour = Long.valueOf(detail.getHour());
                this.consumptionActive = Double.parseDouble(detail.getConsumptionActive().replaceAll(",", "."));
                this.generationActive = Double.parseDouble(detail.getGenerationActive().replaceAll(",", "."));
                this.consumptionReactivate = Double.parseDouble(detail.getConsumptionReactivate().replaceAll(",", "."));
                this.generationReactivate = Double.parseDouble(detail.getGenerationReactivate().replaceAll(",", "."));
                this.quality = detail.getQuality();
                this.origem = detail.getOrigem();
                break;
        }

    }

}