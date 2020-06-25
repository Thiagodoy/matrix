/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.core.matrix.factory.EmailFactory;
import com.core.matrix.model.Email;
import com.core.matrix.model.Template;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_NUMBER_PROCESS;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_USER_EMAIL;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_USER_NAME;
import com.core.matrix.utils.ThreadPoolEmail;

/**
 *
 * @author thiag
 */
@SpringBootTest
public class SendEmailTest{

    @Autowired
    private EmailFactory emailFactory;
    
    @Autowired
    private ThreadPoolEmail threadPoolEmail;
    
   
    
    @Test    
    public void teste() throws Exception {
        
        Email email = emailFactory.createEmailTemplate(Template.TemplateBusiness.REPURCHASE_WHITOUT_RATEIO);
        email.setParameter(TEMPLATE_PARAM_USER_NAME, "Thiago");
        email.setParameter(TEMPLATE_PARAM_NUMBER_PROCESS, "XXXXX");
        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, "thiagodoy@hotmail.com");

        threadPoolEmail.submit(email);
        
        
        Thread.sleep(10000);
        
    }

    
    

}
