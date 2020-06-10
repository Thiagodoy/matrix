/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.MeansurementFileAuthority;
import com.core.matrix.service.MeansurementFileAuthorityService;
import static com.core.matrix.utils.Url.URL_API_AUTHORITY;
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
@RequestMapping(value = URL_API_AUTHORITY)
public class MeansurementFileAuthorityResource extends Resource<MeansurementFileAuthority, MeansurementFileAuthorityService>{    

    public MeansurementFileAuthorityResource(MeansurementFileAuthorityService service) {
        super(service);
    }    

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "idMeansurementFile", required = false) Long idMeansurementFile,
            @RequestParam(name = "authority", required = false) String authority,
            @RequestParam(name = "user", required = false) String user,
            @RequestParam(name = "processIntanceId", required = false) String processIntanceId,
            @RequestParam(name = "userName", required = false) String userName) {
        try {
            List<MeansurementFileAuthority> response = this.getService().find(idMeansurementFile, processIntanceId, authority, user, userName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(MeansurementFileDetailResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
