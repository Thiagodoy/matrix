package com.core.matrix;

import static com.core.matrix.utils.Constants.LIST_ATTACHMENT_ID;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FlowAllTests {

    
    
    private final String TASK_ID = "936633";
    
    @Autowired
    private TaskService taskService;
    
    
    
    
    

    @Test
    void contextLoads() throws Exception {
        
        Task task = taskService.createTaskQuery().taskId(TASK_ID).singleResult();
        List<String> files = Arrays.asList(
                "exportacao_mantovani_243169_EXPK_1.csv",
                "exportacao_mantovani_243169_ROMAFILM_1.csv",
                "exportacao_mantovani_243169_ROMAPACK_1.csv",
                "exportacao_mantovani_243169_ROMAVIDA_1.csv");
               //"exportacao_mantovani_243169_VILAPACK_1.csv");
        
        List<String> attachmentsIDs = new ArrayList<>();
        
        for (String file : files) {
            File file1 = new File(file);
            InputStream ip = new FileInputStream(file1);
            
            Attachment attachment1 = taskService
                .createAttachment("application/vnd.ms-excel", null, task.getProcessInstanceId(), file1.getName(), "attachmentDescription", ip);
            
            attachmentsIDs.add(attachment1.getId());
        }
        
        
        
        

         
         Map<String,Object>parameters = new HashMap<>();
         
         parameters.put(LIST_ATTACHMENT_ID, attachmentsIDs);
         
         taskService.complete(task.getId(), parameters);
         
        
    }

}
