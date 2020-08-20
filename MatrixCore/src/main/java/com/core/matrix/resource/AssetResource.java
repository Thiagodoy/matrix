/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
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

    private String version = "v4.0.14";

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping(value = "/versao", method = RequestMethod.GET)
    public ResponseEntity getVersao() throws IOException {
        return ResponseEntity.ok(this.version);
    }

    @RequestMapping(value = "/cleanCache", method = RequestMethod.GET)
    public ResponseEntity cleanCache() throws IOException {

        cacheManager.getCacheNames().parallelStream().forEach(name -> cacheManager.getCache(name).clear());
        return ResponseEntity.ok("Todos os cahes foram limpos");
    }

}
