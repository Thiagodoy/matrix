/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.workflow.task.FileValidationTask;
import org.activiti.engine.delegate.DelegateExecution;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *
 * @author thiag
 */
public class ValidationTaskTest {

    public ValidationTaskTest() {
    }

    @Test
    public void run() throws Exception {

     //   DelegateExecution delegateExecution = Mockito.mock(DelegateExecution.class);
        FileValidationTask task = new FileValidationTask();
        task.execute(null);

    }
}
