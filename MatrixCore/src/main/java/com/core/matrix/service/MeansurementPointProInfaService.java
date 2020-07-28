/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.exceptions.EntityNotFoundException;
import com.core.matrix.model.MeansurementPointProInfa;
import com.core.matrix.repository.MeansurementPointProInfaRepository;
import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class MeansurementPointProInfaService extends Service<MeansurementPointProInfa, MeansurementPointProInfaRepository>{    
    public MeansurementPointProInfaService(MeansurementPointProInfaRepository repositoy) {
        super(repositoy);
    }    
    
    
    
    
    @Transactional(readOnly = true)
    public synchronized MeansurementPointProInfa getCurrentProInfa(String point) throws EntityNotFoundException{
        
        final Long month = (long) LocalDate.now().minusMonths(1).getMonthValue();
        final Long year = (long)  LocalDate.now().getYear();
        
        return this.repository.findByPointAndMonthAndYear(point, month, year).orElseThrow(()-> new EntityNotFoundException());
    }
    
    
}
