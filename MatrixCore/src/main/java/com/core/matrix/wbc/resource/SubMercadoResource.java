/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.resource;

import com.core.matrix.response.PageResponse;
import com.core.matrix.wbc.dto.SubMercadoDTO;
import com.core.matrix.wbc.service.SubMercadoService;
import java.util.List;
import java.util.logging.Level;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/wbc/submercado")
public class SubMercadoResource {

    @Autowired
    private SubMercadoService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "nCdSubmercado", required = false) Integer nCdSubmercado,
            @RequestParam(name = "sDsSubmercado", required = false) String sDsSubmercado
    ) {

        try {
            List<SubMercadoDTO> response = service.list();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(SubMercadoService.class.getName()).log(Level.SEVERE, "[getSubMercado]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

}
