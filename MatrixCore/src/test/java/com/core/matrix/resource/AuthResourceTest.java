/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.request.AuthRequest;
import com.core.matrix.request.ForgotPasswordRequest;
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
public class AuthResourceTest extends ResourceAbstractTest {
   
    
    @Test
    @Order(1)
    public void forgotPassword() throws Exception {

        super.setUp();

        String uri = Url.URL_API_AUTH + "/forgotPassword";

        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("thiagodoy@hotmail.com");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();  
        
        assertEquals(200, status);
        
        
        
        

    }

    @Test
    @Order(2)
    public void authOk() throws Exception {

        super.setUp();

        String uri = Url.URL_API_AUTH;

        AuthRequest request = new AuthRequest();
        request.setUsername("testeSenha@testeSenha.com");
        request.setPassword("123456");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

    }

    @Test
    @Order(3)
    public void authBlocked() throws Exception {

        super.setUp();

        for (int i = 0; i < 2; i++) {

            String uri = Url.URL_API_AUTH;

            AuthRequest request = new AuthRequest();
            request.setUsername("testeSenha@testeSenha.com");
            request.setPassword("12345");

            mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        }

        String uri = Url.URL_API_AUTH;

        AuthRequest request = new AuthRequest();
        request.setUsername("testeSenha@testeSenha.com");
        request.setPassword("12345");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(500, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals("USER_BLOCKED", content);

    }

    @Test
    @Order(4)
    public void authDisabled() throws Exception {

        super.setUp();

        String uri = Url.URL_API_AUTH;

        AuthRequest request = new AuthRequest();
        request.setUsername("testeSenha@testeSenha.com");
        request.setPassword("12345");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(500, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals("USER_DISABLED", content);

    }

}
