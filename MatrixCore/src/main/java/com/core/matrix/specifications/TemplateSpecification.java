/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Template;
import com.core.matrix.model.Template_;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class TemplateSpecification {

    public static Specification<Template> filter(Long id, String subject, int version) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Template_.id), id);
    }  
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    protected Long id;
    
    @Column(name = "template",columnDefinition = "longtext")
    protected String template;
    
    @Column(name = "assunto")
    protected String subject;    
    
    @Version
    protected Long version;
    
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Column(name = "data_criacao")
    protected LocalDateTime createdAt;
    
    @Column(name = "parametros")
    protected String parameters;

}
