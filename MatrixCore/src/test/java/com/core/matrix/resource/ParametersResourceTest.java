/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Parameters;
import com.core.matrix.utils.Url;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author thiag
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class ParametersResourceTest extends ResourceAbstractTest {

    private static String uri;
    private static Parameters request;

    @BeforeEach
    public void setup() {
        super.setUp();
    }

    @BeforeAll
    public static void config() {

        uri = Url.URL_API_PARAMETERS;
        request = new Parameters();        
        request.setDescription("Teste");
        request.setType(Parameters.ParameterType.TEXT);
        request.setKey("KEY_TESTE");
        request.setValue("TESTE");       
        
        
    }

    @Test
    @Order(1)
    public void createAParameters() throws Exception {

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(200, status);

        Long idProduct = this.mapFromJson(mvcResult.getResponse().getContentAsString(), Long.class);

        this.request.setId(idProduct);

    }

    @Test
    @Order(2)
    public void getAParameters() throws Exception {

        MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
        
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri+"/" + request.getId())
                .queryParams(values)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(200, status);

    }

    @Test
    @Order(3)
    public void update() throws Exception {

        request.setValue("VALUE");
        request.setKey("KEY_TESTE_VALUE");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri).content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri + "/" + request.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        Parameters response = this.mapFromJson(mvcResult.getResponse().getContentAsString(), Parameters.class);

        assertEquals(request.getValue(), response.getValue());
        assertEquals(request.getKey(), response.getKey());

    }

    @Test
    @Order(4)
    public void delete() throws Exception {

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri + "/" + request.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(200, status);

    }

}
