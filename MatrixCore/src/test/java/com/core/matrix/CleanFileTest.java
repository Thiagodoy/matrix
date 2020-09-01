package com.core.matrix;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CleanFileTest {

    
    
    @Autowired
    private RuntimeService runtimeService;
    
     @Autowired
    private TaskService taskService;

    @Test
    void contextLoads() throws Exception {
        
        String f = "$2a$10$S8ReIKY9N1YlLBKMJhXrN.wOHkyN6ewMcvkjGnzal1DWy2r4IW1mW";
         //Get task
     
        //Task task = taskService.createTaskQuery().taskId("187518").singleResult();
        
       
         
         taskService.complete("215009");
         
        //System.out.println("password-> " + new BCryptPasswordEncoder().encode("123456"));
    }

}
