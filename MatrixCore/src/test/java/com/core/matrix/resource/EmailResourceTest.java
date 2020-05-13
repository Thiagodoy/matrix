/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Email;
import com.core.matrix.model.Template;
import com.core.matrix.service.TemplateService;
import com.core.matrix.utils.Url;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 *
 * @author thiag
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class EmailResourceTest extends ResourceAbstractTest {

    private static Long idNotification;
    
   

    @Test
    @Order(1)
    public void save_a_notification() throws Exception {

        super.setUp();

        String uri = Url.URL_API_NOTIFICATION;

        Email notification = new Email();
        
        notification.setData("{data:data}");
        notification.setStatus(Email.EmailStatus.READY);
        Template template = new Template();
        template.setId(1L);
        notification.setTemplate(template);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(notification))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String content = mvcResult.getResponse().getContentAsString();
        idNotification = super.mapFromJson(content, Long.class);

        //assertEquals(template, request);
    }

    
     @Test
    @Order(2)
    public void get_a_notification_by_id() throws Exception {

        super.setUp();

        String uri = Url.URL_API_NOTIFICATION + "/" + idNotification;

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

    }
    
    @Test
    @Order(3)
    public void update_a_notification_by_id() throws Exception {

        super.setUp();

        String uri = Url.URL_API_NOTIFICATION ;
        
         Email notification = new Email();
        
        notification.setData("{update:update}");
        notification.setStatus(Email.EmailStatus.READY);
        notification.setId(idNotification);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri).content(mapToJson(notification))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

    }
    
   
    

   // @Test
   // @Order(4)
    public void delete_a_notification_by_id() throws Exception {

        super.setUp();

        String uri = Url.URL_API_NOTIFICATION + "/" + idNotification;

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

    }

}
