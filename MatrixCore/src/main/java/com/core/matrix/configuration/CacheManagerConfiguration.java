/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author thiag
 */
@Configurable
public class CacheManagerConfiguration {

    @Autowired
    private CacheManager cacheManager;

    @Scheduled(fixedRate = 300000)
    public void clearCache() {
        cacheManager.getCacheNames().forEach(ke -> {
            cacheManager.getCache(ke).clear();
        });
    }

}
