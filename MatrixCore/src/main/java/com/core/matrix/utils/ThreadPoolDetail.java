/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.utils;

import com.core.matrix.jobs.PersistDetailsJob;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author thiag
 */
public class ThreadPoolDetail {

    private static ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    private static Map<String, Future> running = new HashMap<>();

    public synchronized void submit(PersistDetailsJob job) {

        if (!running.containsKey(job.getProcessInstanceId())) {
            Future future = pool.submit(job);
            running.put(job.getProcessInstanceId(), future);
        }

    }

    public synchronized static boolean isRunning(String processInstanceId) {
        return running.keySet().stream().anyMatch(l -> l.equals(processInstanceId));
    }

    public synchronized static void finalize(String processIntanceId) {
        running.remove(processIntanceId);
    }

    public void cancel(String processInstanceId) {
        if (running.containsKey(processInstanceId)) {
            running.get(processInstanceId).cancel(true);
        }
    }

    public static void shutdown() {
        ThreadPoolDetail.pool.shutdown();
    }

}
