package com.core.matrix;

import static com.core.matrix.utils.Constants.LIST_ATTACHMENT_ID;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
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
        
        
         //Get task
     
        //Task task = taskService.createTaskQuery().taskId("187518").singleResult();
        
       
         
         taskService.complete("215009");
         
        //System.out.println("password-> " + new BCryptPasswordEncoder().encode("123456"));
    }

}
