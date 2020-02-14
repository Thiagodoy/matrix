package com.core.matrix;

import com.core.matrix.workflow.task.BillingContractsTask;
import org.activiti.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MatrixApplicationTests {

    
    
    @Autowired
    private RuntimeService runtimeService;
    

    @Test
    void contextLoads() throws Exception {
        
        
        runtimeService.startProcessInstanceByMessage("p131");
        
     
        //System.out.println("password-> " + new BCryptPasswordEncoder().encode("123456"));
    }

}
