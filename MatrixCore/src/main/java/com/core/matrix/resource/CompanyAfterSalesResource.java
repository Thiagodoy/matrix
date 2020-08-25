/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.CompanyAfterSales;
import com.core.matrix.service.CompanyAfterSalesService;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/api/company/aftersales")
public class CompanyAfterSalesResource {

    @Autowired
    private CompanyAfterSalesService afterSalesService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity associate(@RequestBody(required = true) CompanyAfterSales body) {
        try {
            afterSalesService.save(body);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            Logger.getLogger(CompanyAfterSales.class.getName()).log(Logger.Level.FATAL, "[associate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(MessageFormat.format("A empresa [ {0} ] já esta associada a um usuário pós venda.", body.getCompany()));
        } catch (Exception e) {
            Logger.getLogger(CompanyAfterSales.class.getName()).log(Logger.Level.FATAL, "[associate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(@RequestParam(name = "code", required = true) Long code) {
        try {
            List<CompanyAfterSales> response = afterSalesService.findByCodCompany(Arrays.asList(code));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(CompanyAfterSales.class.getName()).log(Logger.Level.FATAL, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity put(@RequestBody(required = true) CompanyAfterSales body) {
        try {
            afterSalesService.update(body);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(CompanyAfterSales.class.getName()).log(Logger.Level.FATAL, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

    @RequestMapping(value = "/{company}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable(name = "company") Long company) {
        try {

            afterSalesService.delete(company);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(CompanyAfterSales.class.getName()).log(Logger.Level.FATAL, "[delete]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
