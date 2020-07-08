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
import com.core.matrix.utils.Constants;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_NUMBER_PROCESS;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_USER_EMAIL;
import static com.core.matrix.utils.Constants.TEMPLATE_PARAM_USER_NAME;
import com.core.matrix.utils.ThreadPoolEmail;

/**
 *
 * @author thiag
 */
@SpringBootTest
public class SendEmailTest {

    @Autowired
    private EmailFactory emailFactory;

    @Autowired
    private ThreadPoolEmail threadPoolEmail;

    // @Test
    public void teste() throws Exception {

        Email email = emailFactory.createEmailTemplate(Template.TemplateBusiness.BILLING_WHITOUT_REPURCHASE);
        email.setParameter(TEMPLATE_PARAM_USER_NAME, "Thiago");
        email.setParameter(TEMPLATE_PARAM_NUMBER_PROCESS, "XXXXX");
        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, "thiagodoy@hotmail.com;luis.maisnet@gmail.com;mauricelio.lauand@bandtec.com.br;aloysio.carvalho@bandtec.com.br");

        threadPoolEmail.submit(email);

        email = emailFactory.createEmailTemplate(Template.TemplateBusiness.GROUP_TASK_PENDING);
        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, "thiagodoy@hotmail.com;luis.maisnet@gmail.com;mauricelio.lauand@bandtec.com.br;aloysio.carvalho@bandtec.com.br");
        email.setParameter(Constants.TEMPLATE_PARAM_USER_NAME, "Thiago");
        email.setParameter(Constants.TEMPLATE_PARAM_TASK_NAME, "ANALISAR RECOMPRA");
        email.setParameter(Constants.TEMPLATE_PARAM_PROCESS_NAME, "ANÁLISE E CÁLCULO DA MEDIÇÃO");
        email.setParameter(Constants.TEMPLATE_PARAM_NUMBER_PROCESS, "21343243");
        email.setParameter(Constants.TEMPLATE_PARAM_TASK_CREATE_DATE, "20/12/2020");
        email.setParameter(Constants.TEMPLATE_PARAM_GROUP_NAME, "Gestor");

        threadPoolEmail.submit(email);

        email = emailFactory.createEmailTemplate(Template.TemplateBusiness.FINISHED_UPLOAD_LOTE_FILE);

        email.setParameter(TEMPLATE_PARAM_USER_NAME, "Thiago");
        email.setParameter(TEMPLATE_PARAM_NUMBER_PROCESS, "23432");
        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, "thiagodoy@hotmail.com;luis.maisnet@gmail.com;mauricelio.lauand@bandtec.com.br;aloysio.carvalho@bandtec.com.br");

        threadPoolEmail.submit(email);

        email = emailFactory.createEmailTemplate(Template.TemplateBusiness.PROCESS_ERROR);
        email.setParameter(Constants.TEMPLATE_PARAM_CONTRACT, "23434");
        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, "thiagodoy@hotmail.com;luis.maisnet@gmail.com;mauricelio.lauand@bandtec.com.br;aloysio.carvalho@bandtec.com.br");

        threadPoolEmail.submit(email);

        email = emailFactory.createEmailTemplate(Template.TemplateBusiness.WELCOME_USER);

        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, "thiagodoy@hotmail.com;luis.maisnet@gmail.com;mauricelio.lauand@bandtec.com.br;aloysio.carvalho@bandtec.com.br");
        email.setParameter(Constants.TEMPLATE_PARAM_USER_NAME, "Fulano");
        email.setParameter(Constants.TEMPLATE_PARAM_USER_PASSWORD, "123456");

        threadPoolEmail.submit(email);

        email = emailFactory.createEmailTemplate(Template.TemplateBusiness.FORGOT_PASSWORD);

        email.setParameter(Constants.TEMPLATE_PARAM_LINK, "?token=");
        email.setParameter(TEMPLATE_PARAM_USER_EMAIL, "thiagodoy@hotmail.com;luis.maisnet@gmail.com;mauricelio.lauand@bandtec.com.br;aloysio.carvalho@bandtec.com.br");
        email.setParameter(Constants.TEMPLATE_PARAM_USER_NAME, "Fulano");

        threadPoolEmail.submit(email);

        Thread.sleep(15000);

    }

    @Test
    public void testUnbillingContract() throws InterruptedException {

        Email email = emailFactory.createEmailTemplate(Template.TemplateBusiness.PROCESS_BILLING_ERROR);

        email.setParameter(Constants.TEMPLATE_PARAM_USER_EMAIL, "thiagodoy@hotmail.com");
        email.setParameter(Constants.TEMPLATE_PARAM_CONTRACT, "<span>Contrato 9999 - Nestle</span></br><span>Contrato 8888 - BRIDGESTONE DO BRASIL-0001-15</span></br>");

        threadPoolEmail.submit(email);

        Thread.sleep(15000);

    }

}
