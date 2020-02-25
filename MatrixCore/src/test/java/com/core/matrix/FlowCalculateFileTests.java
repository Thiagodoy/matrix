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
class FlowCalculateFileTests {

    
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private TaskService taskService;
    
    

    @Test
    void contextLoads() throws Exception {
        
        
     //Get task
     
        Task task = taskService.createTaskQuery().taskId("355114").singleResult();
        
        File file1 = new File("exportacao_mantovani_226591_EMBALATEC MOGI_1.csv");
        
        InputStream ip = new FileInputStream(file1);
        
         Attachment attachment1 = taskService
                .createAttachment("application/vnd.ms-excel", null, task.getProcessInstanceId(), file1.getName(), "attachmentDescription", ip);
         
         
         
         File file2 = new File("exportacao_mantovani_226591_EMBALATEC SALTO_1.csv");
        
        InputStream ip2 = new FileInputStream(file2);
        
         Attachment attachment2 = taskService
                .createAttachment("application/vnd.ms-excel", null, task.getProcessInstanceId(), file2.getName(), "attachmentDescription", ip2);
         
         
         
         
         File file3 = new File("exportacao_mantovani_226591_EMBALATEC_1.csv");
        
        InputStream ip3 = new FileInputStream(file3);
        
         Attachment attachment3 = taskService
                .createAttachment("application/vnd.ms-excel", null, task.getProcessInstanceId(), file3.getName(), "attachmentDescription", ip3);
         
         
        
         
         Map<String,Object>parameters = new HashMap<>();
         
         parameters.put(LIST_ATTACHMENT_ID, Arrays.asList(attachment1.getId(),attachment2.getId(),attachment3.getId()));
         
         taskService.complete(task.getId(), parameters);
         
        //System.out.println("password-> " + new BCryptPasswordEncoder().encode("123456"));
    }

}
