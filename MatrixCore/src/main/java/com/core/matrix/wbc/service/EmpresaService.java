/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.service;

import com.core.matrix.response.PageResponse;
import com.core.matrix.wbc.dto.EmpresaDTO;
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

    public PageResponse<EmpresaDTO> findAll(String cnpj, String razaoSocial, PageRequest page) {

        Page<Empresa> result = null;
        if (Optional.ofNullable(cnpj).isPresent()) {
            result = this.repository.findByNrCnpjStartingWith(cnpj, page);
        } else if (Optional.ofNullable(razaoSocial).isPresent()) {
            result = this.repository.findByEmpresaStartingWith(razaoSocial, page);
        } else {
            result = this.repository.findAll(page);
        }

        if (result != null && result.hasContent()) {

            List<EmpresaDTO> empresaDTOs = result
                    .getContent()
                    .parallelStream()
                    .map(e -> {

                        Optional<AgentType> opt = agentTypeService
                                .listAll()
                                .stream()
                                .filter(a -> a.getNCdTipoAgente().equals(e.getNCdTipoAgente()))
                                .findFirst();

                        return new EmpresaDTO(e, opt.isPresent() ? opt.get().getSDsTipoAgente() : "");
                    })
                    .collect(Collectors.toList());
            
            return new PageResponse<EmpresaDTO>(empresaDTOs,(long)result.getNumberOfElements(),(long)result.getTotalElements(), (long)page.getPageNumber());

        } else {
            return new PageResponse<EmpresaDTO>(null,0L,0L, 0L);
        }

    }

}
