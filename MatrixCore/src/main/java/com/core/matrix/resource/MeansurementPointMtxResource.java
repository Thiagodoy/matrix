/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.MeansurementPointMtx;
import com.core.matrix.service.MeansurementPointMtxService;
import static com.core.matrix.utils.Url.URL_API_MEANSUREMENT_POINT;
import java.util.List;
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
@RequestMapping(value = URL_API_MEANSUREMENT_POINT)
public class MeansurementPointMtxResource extends Resource<MeansurementPointMtx, MeansurementPointMtxService> {

    public MeansurementPointMtxResource(MeansurementPointMtxService service) {
        super(service);
    }

    @RequestMapping(value = "/byPoint", method = RequestMethod.GET)
    public ResponseEntity getByPoint(@RequestParam(value = "point", required = true) String point) {
        try {
            List<MeansurementPointMtx> response = this.service.findByPointContaining(point);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    
    @RequestMapping(value = "/allpoints", method = RequestMethod.GET)
    public ResponseEntity findAllPoints(){
        try {
            List response = this.service.findAllPoints();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"[findAllPoints]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
