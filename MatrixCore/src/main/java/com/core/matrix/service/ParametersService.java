/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.Parameters;
import com.core.matrix.repository.ParametersRepository;
import java.util.Optional;

/**
 *
 * @author thiag
 */
@org.springframework.stereotype.Service
public class ParametersService extends Service<Parameters, ParametersRepository> {

    public ParametersService(ParametersRepository repositoy) {
        super(repositoy);
    }

    public Optional<Parameters> findByKey(String key) {
        return this.repository.findByKey(key);
    }

}
