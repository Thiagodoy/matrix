/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.wbc.model.MeansurementPoint;
import com.core.matrix.wbc.repository.MeansurementPointRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class MeansurementPointService {

    @Autowired
    private MeansurementPointRepository repository;

    @Transactional(readOnly = true)
    public boolean existsPoint(String point) {
        return this.repository.findByCode(point).isPresent();
    }
    
    public List<MeansurementPoint> listByCompanys(List<Long>companys){
        return this.repository.listByCompanys(companys);
    }
    
    @Transactional(readOnly = true)
    public Page list(Long company , String point, PageRequest page) {        
        
        if(company != null && point != null){
            return this.repository.findByCompanyAndCode(company, point, page);
        }else{
            return this.repository.findByCompany(company, page);
        }
    }

}
