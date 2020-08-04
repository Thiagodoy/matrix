/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.model.ContractMtx;
import com.core.matrix.service.ContractMtxService;
import static com.core.matrix.utils.Constants.PROCESS_INFORMATION_CONTRACTS_MATRIX;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author thiag
 */
@SpringBootTest
public class ComplementarContratosMatrixProcessoTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ContractMtxService contractMtxService;
    
    
     @Test
    void contextLoads() throws Exception {
    
         Task task =  taskService.createTaskQuery().taskId("591837").singleResult();
    
         
         List<ContractMtx> contractsMtx = contractMtxService.findAll(71780L).getContracts();
         
         Map<String, Object> variable = new HashMap<>();
         
         variable.put(PROCESS_INFORMATION_CONTRACTS_MATRIX, contractsMtx);
         
         runtimeService.setVariables(task.getProcessInstanceId(),variable );
         
         
         
         
         
    }
}
