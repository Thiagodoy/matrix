/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
@Entity
@Table(name = "mtx_parametros")
public class Parameters implements Model<Parameters> {

    public enum ParameterType {
        BOOLEAN,
        TEXT,
        DOUBLE,
        LONG;
    }

    public Object getValue() {

        switch (this.type) {
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case TEXT:
                return value;
            case DOUBLE:
                return Double.valueOf(value);
            case LONG:
                return Long.valueOf(value);
            default:
                return null;

        }

    }

    @Id
    @Column(name = "id_parametro")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_SEQUENCES)
    protected Long id;

    @Column(name = "chave")
    protected String key;

    @Column(name = "valor")
    protected String value;

    @Column(name = "descricao")
    protected String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    protected ParameterType type;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "data_criacao")
    protected LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(name = "data_atualizacao")
    protected LocalDateTime updateAt;
    
    @Column(name = "parametro_de_aplicacao")
    protected boolean isApplication;

    @PrePersist
    public void generateCreateAT() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void generatedUpdateAT() {
        this.updateAt = LocalDateTime.now();
    }

}
