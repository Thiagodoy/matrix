/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.request.UserDeleteRequest;
import com.core.matrix.utils.Url;
import com.core.matrix.workflow.model.GroupMemberActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.model.UserInfoActiviti;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class UserResourceTest extends ResourceAbstractTest {
   
    
    @Test
    @Order(1)
    public void createAUser() throws Exception {

        super.setUp();

        String uri = Url.URL_API_USER ;

        UserActiviti request = new UserActiviti();
        request.setId("thiagodoy@hotmail.com");
        request.setEmail("thiagodoy@hotmail.com");
        request.setFirstName("Thiago");
        request.setLastName("Godoy");
        request.setPassword("123456");
        
        Set<GroupMemberActiviti> group = new HashSet<>();        
        GroupMemberActiviti groupActiviti = new GroupMemberActiviti();
        groupActiviti.setGroupId("profile-pós-venda");  
        groupActiviti.setUserId("thiagodoy@hotmail.com");
        group.add(groupActiviti);
        
        request.setGroups(group);
        
        List<UserInfoActiviti> infoActivitis = new ArrayList<>();
        UserInfoActiviti info = new UserInfoActiviti();
        info.setKey("teste");
        info.setUserId("thiagodoy@hotmail.com");
        infoActivitis.add(info);
        
        request.setInfo(infoActivitis);
        
        request.setEnabled(true);
        request.setReceiveEmail(true);
        request.setReceiveNotification(true);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();  
        
        assertEquals(200, status);

    }
    
    
    @Test
    @Order(2)
    public void listAUser() throws Exception {

        super.setUp();

        String uri = Url.URL_API_USER;

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).queryParam("searchValue", "thiagodoy@")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();  
        
        assertEquals(200, status);

    }
    
    @Test
    @Order(3)
    public void update() throws Exception {

        super.setUp();

        String uri = Url.URL_API_USER;

        
        UserActiviti request = new UserActiviti();
        request.setId("thiagodoy@hotmail.com");
        request.setEmail("thiagodoy@hotmail.com");
        request.setFirstName("Thiago Henrique de");
        
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();  
        
        assertEquals(200, status);       

    }

    @Test
    @Order(4)
    public void delete() throws Exception {

        super.setUp();

        String uri = Url.URL_API_USER;

        
        UserDeleteRequest request = new UserDeleteRequest();
        request.setEmail("thiagodoy@hotmail.com");
        
        
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();  
        
        assertEquals(200, status);       

    }
    
    
}
