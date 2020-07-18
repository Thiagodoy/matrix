/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import com.core.matrix.annotation.PositionBatchParameter;
import com.core.matrix.dto.FileDetailDTO;
import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
import com.core.matrix.utils.MeansurementFileDetailStatus;
import com.core.matrix.utils.MeansurementFileType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    private static final long serialVersionUID = 5486497046886735671L;

    @Id
    @Column(name = "id_arquivo_de_medicao_detalhe")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_SEQUENCES)
    @PositionBatchParameter(value = 0)
    public Long id;

    @Column(name = "id_arquivo_de_medicao")
    @PositionBatchParameter(value = 1)
    public Long idMeansurementFile;

    @Column(name = "ponto_medicao")
    @PositionBatchParameter(value = 2)
    public String meansurementPoint;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "data")
    @PositionBatchParameter(value = 3)
    public LocalDate date;

    @Column(name = "hora")
    @PositionBatchParameter(value = 4)
    public Long hour;

    @Column(name = "tipo_energia")
    @PositionBatchParameter(value = 5)
    public String energyType;

    @Column(name = "ativa_geracao")
    @PositionBatchParameter(value = 6)
    public Double generationActive;

    @Column(name = "ativa_consumo")
    @PositionBatchParameter(value = 7)
    public Double consumptionActive;

    @Column(name = "reativa_geracao")
    @PositionBatchParameter(value = 8)
    public Double generationReactivate;

    @Column(name = "reativa_consumo")
    @PositionBatchParameter(value = 9)
    public Double consumptionReactivate;

    @Column(name = "situacao_da_medida")
    @PositionBatchParameter(value = 10)
    public String situation;

    @Column(name = "motivo_da_situacao")
    @PositionBatchParameter(value = 11)
    public String reasonOfSituation;

    @Column(name = "intervalo")
    @PositionBatchParameter(value = 12)
    public Long range;

    @Column(name = "origem_coleta")
    @PositionBatchParameter(value = 13)
    public String sourceCollection;

    @Column(name = "notificacao_coleta")
    @PositionBatchParameter(value = 14)
    public String notificationCollection;

    @Column(name = "agente")
    @PositionBatchParameter(value = 15)
    public String agent;

    @Column(name = "qualidade")
    @PositionBatchParameter(value = 16)
    public String quality;

    @Column(name = "origem")
    @PositionBatchParameter(value = 17)
    public String origem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @PositionBatchParameter(value = 18)
    public MeansurementFileDetailStatus status;

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

    public Double parseToDouble(String value) {

        if (!Optional.ofNullable(value).isPresent() || Optional.ofNullable(value).isPresent() && value.length() == 0) {
            value = "0,0";
        }

        if (value.contains("R$")) {
            value = value.replaceAll("[R$]*", "").trim();
        }

        value = value.trim();
        
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMANY);

        try {
            return nf.parse(value).doubleValue();
        } catch (ParseException e) {
            Logger.getLogger(MeansurementFileDetail.class.getName()).log(Level.SEVERE, "Não foi possivel converter o valor -> " + value);
            throw new NumberFormatException("Não foi possivel converter o valor -> " + value);
        }

    }

    private LocalDate parseToLocaDate(String value) {

        try {
            DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(value, formater);
        } catch (DateTimeParseException e) {
            DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(value, formater);
        }
    }

    public MeansurementFileDetail(FileDetailDTO detail, MeansurementFileType type) {

        this.status = MeansurementFileDetailStatus.SUCCESS;

        switch (type) {
            case LAYOUT_A:
                this.meansurementPoint = detail.getMeansurementPoint();
                this.date = this.parseToLocaDate(detail.getDate());
                this.hour = Long.valueOf(detail.getHour());
                this.energyType = detail.getEnergyType();
                this.generationActive = this.parseToDouble(detail.getGenerationActive());
                this.consumptionActive = this.parseToDouble(detail.getConsumptionActive());
                this.generationReactivate = this.parseToDouble(detail.getGenerationReactivate());
                this.consumptionReactivate = this.parseToDouble(detail.getConsumptionReactivate());
                this.range = Long.parseLong(detail.getRange());
                this.situation = detail.getSituation();
                this.reasonOfSituation = detail.getReason();
                break;

            case LAYOUT_B:
                this.meansurementPoint = detail.getMeansurementPoint();
                this.date = this.parseToLocaDate(detail.getDate());
                this.hour = Long.valueOf(detail.getHour());
                this.energyType = detail.getEnergyType();
                this.generationActive = this.parseToDouble(detail.getGenerationActive());
                this.consumptionActive = this.parseToDouble(detail.getConsumptionActive());
                this.generationReactivate = this.parseToDouble(detail.getGenerationReactivate());
                this.consumptionReactivate = this.parseToDouble(detail.getConsumptionReactivate());
                this.sourceCollection = detail.getSourceCollection();
                this.notificationCollection = detail.getNotificationCollection();
                break;
            case LAYOUT_C:
                this.agent = detail.getAgent();
                this.meansurementPoint = detail.getMeansurementPoint();
                this.date = this.parseToLocaDate(detail.getDate());
                this.hour = Long.valueOf(detail.getHour());
                this.consumptionActive = this.parseToDouble(detail.getConsumptionActive());
                this.generationActive = this.parseToDouble(detail.getGenerationActive());
                this.consumptionReactivate = this.parseToDouble(detail.getConsumptionReactivate());
                this.generationReactivate = this.parseToDouble(detail.getGenerationReactivate());
                this.quality = detail.getQuality();
                this.origem = detail.getOrigem();
                break;
            case LAYOUT_C_1:
                this.agent = detail.getAgent();
                this.meansurementPoint = detail.getMeansurementPoint();
                this.date = this.parseToLocaDate(detail.getDate());
                this.hour = Long.valueOf(detail.getHour());
                this.consumptionActive = this.parseToDouble(detail.getConsumptionActive());
                this.quality = detail.getQuality();
                this.origem = detail.getOrigem();
                break;
        }

    }

}
