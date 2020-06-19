/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import com.core.matrix.jobs.ParseFileJob;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author thiag
 */
@Component
public class ThreadPoolParseFile {
    
    private ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    private List<Future> future = new ArrayList<>();

    public void submit(ParseFileJob job) {
        Future f = pool.submit(job);
        future.add(f);
    }

    public boolean isDone() {

        boolean result = this.future.stream().map(f -> f.isDone()).reduce(Boolean.TRUE, Boolean::logicalAnd).booleanValue();

        if (result) {
            this.shutdown();
        }

        return result;
    }

    public void shutdown() {
        this.pool.shutdown();
    }

    public void monitor() {
        while (!this.pool.isTerminated()) {
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                Logger.getLogger(ThreadPoolParseFile.class.getName()).log(Level.SEVERE, "Monitor can't sleep the thread!");
            }
        }
    }

}
