/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Template;
import com.core.matrix.utils.Url;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
public class TemplateResourceTest extends ResourceAbstractTest {

    private static Long idTemplate;

    @Test
    @Order(1)
    public void save_a_template() throws Exception {

        super.setUp();

        String uri = Url.URL_API_TEMPLATE;

        Template request = new Template();
        request.setAttachments(":name,:email");
        request.setSubject("Esqueci a senha");
        request.setTemplate("<html>");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String content = mvcResult.getResponse().getContentAsString();
        idTemplate = super.mapFromJson(content, Long.class);

        //assertEquals(template, request);
    }

    
     @Test
    @Order(2)
    public void get_a_template_by_id() throws Exception {

        super.setUp();

        String uri = Url.URL_API_TEMPLATE + "/" + idTemplate;

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

    }
    
    @Test
    @Order(3)
    public void update_a_template_by_id() throws Exception {

        super.setUp();

        String uri = Url.URL_API_TEMPLATE ;
        
        Template request = new Template();
        request.setAttachments(":update,:update");
        request.setSubject("Esqueci a senha (UPdata)");
        request.setTemplate("<update>");
        request.setId(idTemplate);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

    }
    
   
    

    //@Test
    //@Order(4)
    public void delete_a_template_by_id() throws Exception {

        super.setUp();

        String uri = Url.URL_API_TEMPLATE + "/" + idTemplate;

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

    }

}
