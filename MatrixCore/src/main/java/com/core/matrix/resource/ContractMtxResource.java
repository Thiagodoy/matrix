/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.exceptions.ContractNotAssociatedWithPointException;
import com.core.matrix.exceptions.EntityNotFoundException;
import com.core.matrix.model.ContractMtx;
import com.core.matrix.response.ContractMtxResponse;
import com.core.matrix.service.ContractMtxService;
import static com.core.matrix.utils.Url.URL_API_CONTRACT;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@RequestMapping(value = URL_API_CONTRACT)
public class ContractMtxResource extends Resource<ContractMtx, ContractMtxService> {

    public ContractMtxResource(ContractMtxService service) {
        super(service);
    }

    @RequestMapping(value = "/associate", method = RequestMethod.POST)
    public ResponseEntity associate(@RequestParam(name = "contract") Long contract, @RequestParam(name = "point")String point) {
        try {
            this.service.associateContractToPoint(contract, point);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "[associate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }   
    
    @RequestMapping(value = "/unassociate", method = RequestMethod.POST)
    public ResponseEntity unAssociate(@RequestParam(name = "contract") Long contract, @RequestParam(name = "point")String point) {
        try {
            this.service.unAssociateContractToPoint(contract, point);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "[associate]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity list(@RequestParam(name = "contract") Long contract) {
        try {
            ContractMtxResponse response = this.service.findAll(contract);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "[list]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/reloadProcess", method = RequestMethod.POST)
    public ResponseEntity reloadProcess(@RequestParam(required = true, name = "contractId") Long contractId) {
        try {
            this.service.reloadProcess(contractId);
            return ResponseEntity.ok().build();

        } catch(ContractNotAssociatedWithPointException e){            
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
        catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
