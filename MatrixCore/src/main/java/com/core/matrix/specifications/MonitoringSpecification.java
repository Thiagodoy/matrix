/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Monitoring;
import com.core.matrix.model.Monitoring_;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class MonitoringSpecification {

    public static Specification<Monitoring> parameters(String status, String instanciaDoProcesso,String wbcContrato, String pontoMedicao, String empresa, String ano, String mes ) {
        
        List<Specification> predicatives = new ArrayList<>();
        
        
        if(Optional.ofNullable(status).isPresent()){
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Monitoring_.status), status));
        }
        
        if(Optional.ofNullable(wbcContrato).isPresent()){
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Monitoring_.wbcContrato), wbcContrato));
        }
        
        if(Optional.ofNullable(pontoMedicao).isPresent()){
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Monitoring_.pontoDeMedicao), pontoMedicao));
        }
        
        if(Optional.ofNullable(empresa).isPresent()){
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(Monitoring_.empresa)), "%" + empresa + "%"));
        }
        
        if(Optional.ofNullable(mes).isPresent()){
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Monitoring_.mes), mes));
        }
        
        if(Optional.ofNullable(ano).isPresent()){
             predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(Monitoring_.ano), ano));
        }
        
        
        Specification<Monitoring> spc = predicatives.stream().reduce((a,b)-> a.and(b)).orElse(null);
        
        return spc;
        
    }

   

}
