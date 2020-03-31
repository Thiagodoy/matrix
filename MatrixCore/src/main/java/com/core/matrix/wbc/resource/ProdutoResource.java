/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.wbc.resource;

import com.core.matrix.wbc.dto.ProdutoDTO;
import com.core.matrix.wbc.service.ProdutoService;
import java.util.List;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/api/wbc/produto")
public class ProdutoResource {

    @Autowired
    private ProdutoService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "nCdPerfilCCEE", required = false) Integer nCdPerfilCCEE,
            @RequestParam(name = "sDsSiglaCCEE", required = false) String sDsSiglaCCEE,
            @RequestParam(name = "sDsPerfilCCEE", required = false) String sDsPerfilCCEE
    ) {

        try {
            List<ProdutoDTO> response = service.list();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(ProdutoService.class.getName()).log(Level.SEVERE, "[getProduto]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }

    }

}
