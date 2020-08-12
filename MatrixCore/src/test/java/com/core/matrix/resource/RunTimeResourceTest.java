/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.request.AuthRequest;
import com.core.matrix.response.AuthResponse;
import com.core.matrix.response.PageResponse;
import com.core.matrix.response.TaskResponse;
import com.core.matrix.utils.Url;
import com.fasterxml.jackson.core.type.TypeReference;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 *
 * @author thiag
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class RunTimeResourceTest extends ResourceAbstractTest {
   
    
    private static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBhZG1pbi5jb20iLCJleHAiOjE1OTcyNjExNDksImlhdCI6MTU5NzI0MzE0OX0.vpxMtQDXENh6FNtRCQbYOCSsNoMGtBoFa7rO8sitdIr8-cBxFTbF1xRtyaZtf8uORldDkxdobg0lqF9lUyiBwA";
    
    
    public void auth() throws Exception {

        super.setUp();

        String uri = Url.URL_API_AUTH;

        AuthRequest request = new AuthRequest();
        request.setUsername("admin@admin.com");
        request.setPassword("123456");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        
        AuthResponse response = this.mapFromJson(mvcResult.getResponse().getContentAsString(), AuthResponse.class);
        
        RunTimeResourceTest.token = response.getToken();
    }
    
    
    
    @Test
    @Order(1)
    public void getMyTask() throws Exception {

        super.setUp();

        String uri = Url.URL_API_RUNTIME + "/getMyTask";    
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + RunTimeResourceTest.token);
        
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .headers(headers)
                .queryParam("searchValue", "2264749")
                .queryParam("page", "0")
                .queryParam("size", "10")
                .contentType(MediaType.APPLICATION_JSON)                
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();          
        assertEquals(200, status);
        
        TypeReference<PageResponse<TaskResponse>> ref = new TypeReference<PageResponse<TaskResponse>>(){};
        
        PageResponse<TaskResponse> response = this.mapFromJson(mvcResult.getResponse().getContentAsString(), ref);
        
        assertEquals(1, response.getContent().size());
    }  

}
