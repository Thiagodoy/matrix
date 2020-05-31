package com.core.matrix;

import com.core.matrix.service.MeansurementFileResultService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileResultTest {

    
    
   
    
     @Autowired
    private MeansurementFileResultService resultService;

    @Test
    void contextLoads() throws Exception {
        
        
        
        resultService.getResult("1214477");
        
        
    }

}
