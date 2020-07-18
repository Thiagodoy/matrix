/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.dto.FileParsedDTO;
import com.core.matrix.io.BeanIO;
import com.core.matrix.service.MeansurementFileService;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.activiti.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author thiag
 */
//@SpringBootTest
public class FileValidation {
    
    


    @Autowired
    private MeansurementFileService service;

    private DelegateExecution delegateExecution;
    
    

    @Test
    public void execute() throws Exception {

        

       // final String attachmentId = de.getVariable(ATTACHMENT_ID, String.class);
       // final String userId = de.getVariable(USER_ID, String.class);

        try {

             
            InputStream stream = new FileInputStream(new File("exportacao_clacos0g0_235088v1.csv"));
            BeanIO reader = new BeanIO();
            Optional<FileParsedDTO> fileParsed = reader.<FileParsedDTO>parse(stream);
            
            if(!reader.getErrors().isEmpty()){
               // delegateExecution.setVariable(RESPONSE_RESULT, reader.getErrors());
               // delegateExecution.setVariable(RESPONSE_RESULT_MESSAGE, "Layout/registros estão inválidos!");
              //  delegateExecution.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
            }else if (fileParsed.isPresent()) {
              //  mountFile(fileParsed.get(), "111-teste", "thiagodoy@hotmail.com");
            }

        } catch (Exception e) {
            Logger.getLogger(MeansurementFileService.class.getName()).log(Level.SEVERE, "[execute]", e);            
            //de.setVariable(CONTROLE, RESPONSE_LAYOUT_INVALID);
        }
    }
}
