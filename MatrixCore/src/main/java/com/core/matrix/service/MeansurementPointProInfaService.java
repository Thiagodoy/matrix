/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.MeansurementPointProInfa;
import com.core.matrix.repository.MeansurementPointProInfaRepository;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class MeansurementPointProInfaService extends Service<MeansurementPointProInfa, MeansurementPointProInfaRepository>{    
    public MeansurementPointProInfaService(MeansurementPointProInfaRepository repositoy) {
        super(repositoy);
    }    
}
