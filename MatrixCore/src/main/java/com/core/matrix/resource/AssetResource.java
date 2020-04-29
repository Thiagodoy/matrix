/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = "/asset")
public class AssetResource {

    private String version = "v1.1.5";

    @RequestMapping(value = "/versao", method = RequestMethod.GET)
    public ResponseEntity getVersao() throws IOException {
        return ResponseEntity.ok(this.version);
    }

}
