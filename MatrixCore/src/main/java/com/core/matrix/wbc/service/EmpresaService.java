/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.response.PageResponse;
import com.core.matrix.wbc.dto.CompanyDTO;
import com.core.matrix.wbc.model.AgentType;
import com.core.matrix.wbc.model.Empresa;
import com.core.matrix.wbc.repository.EmpresaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    @Autowired
    private AgentTypeService agentTypeService;
    
    
    @Transactional(readOnly = true)
    public Optional<CompanyDTO>listByPoint(String point){
       return this.repository.listByPoint(point);
    }

    public PageResponse<CompanyDTO> findAll(String cnpj, String razaoSocial,String apelido, PageRequest page) {

        Page<Empresa> result = null;
        if (Optional.ofNullable(cnpj).isPresent()) {
            result = this.repository.findByNrCnpjStartingWith(cnpj, page);
        } else if (Optional.ofNullable(razaoSocial).isPresent()) {
            result = this.repository.findByEmpresaContaining(razaoSocial, page);
        } else if(Optional.ofNullable(apelido).isPresent()){
            result = this.repository.findByApelidoContaining(apelido, page);
        } else {
            result = this.repository.findAll(page);
        }

        if (result != null && result.hasContent()) {

            List<CompanyDTO> empresaDTOs = result
                    .getContent()
                    .parallelStream()
                    .map(e -> {

                        Optional<AgentType> opt = agentTypeService
                                .listAll()
                                .stream()
                                .filter(a -> a.getNCdTipoAgente().equals(e.getNCdTipoAgente()))
                                .findFirst();

                        return new CompanyDTO(e, opt.isPresent() ? opt.get().getSDsTipoAgente() : "");
                    })
                    .collect(Collectors.toList());
            
            return new PageResponse<CompanyDTO>(empresaDTOs,(long)result.getNumberOfElements(),(long)result.getTotalElements(), (long)page.getPageNumber());

        } else {
            return new PageResponse<CompanyDTO>(null,0L,0L, 0L);
        }

    }

}
