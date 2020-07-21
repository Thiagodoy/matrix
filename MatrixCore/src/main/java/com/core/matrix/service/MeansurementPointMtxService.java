/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.ContractMtx;
import com.core.matrix.model.MeansurementPointMtx;
import com.core.matrix.model.MeansurementPointProInfa;
import com.core.matrix.repository.MeansurementPointMtxRepository;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class MeansurementPointMtxService extends Service<MeansurementPointMtx, MeansurementPointMtxRepository> {

    @Autowired
    private ContractMtxService contractMtxService;

    @Autowired
    private MeansurementPointProInfaService meansurementPointProInfaService;

    public MeansurementPointMtxService(MeansurementPointMtxRepository repositoy) {
        super(repositoy);
    }

    @Override
    public Long save(MeansurementPointMtx entity) {

        entity.getContracts().forEach(c -> {
            try {

                if (c.getId() != null) {
                    ContractMtx contractMtx = this.contractMtxService.find(c.getId());
                    contractMtx.update(c);
                    c = contractMtx; 
                }

            } catch (Exception ex) {
                Logger.getLogger(MeansurementPointMtxService.class.getName()).log(Level.SEVERE, "[save]", ex);
            }
        });

        entity.getProinfas().forEach( p-> {
            try {   
                
                if(p.getId() != null){
                    MeansurementPointProInfa meansurementPointProInfa = meansurementPointProInfaService.find(p.getId());
                    meansurementPointProInfa.update(p);
                    p = meansurementPointProInfa;
                }                
            } catch (Exception ex) {
                Logger.getLogger(MeansurementPointMtxService.class.getName()).log(Level.SEVERE, "[save]", ex);
            }
        });

        return super.save(entity);
    }

    @Transactional(readOnly = true)
    public MeansurementPointMtx getByPoint(String point) throws Exception {
        return this.repository.findByPoint(point).orElseThrow(() -> new Exception("Not found entity!"));
    }

    @Transactional(readOnly = true)
    public List<String> findAllPoints() {
        return this.repository.findAllPoints();
    }

}
