/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.MeansurementRepurchase;
import com.core.matrix.model.Price;
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
public class MeansurementFileRepurchaseResourceTest extends ResourceAbstractTest {

    private static String uri;
    private static MeansurementRepurchase request;

    @BeforeEach
    public void setup() {
        super.setUp();
    }

    @BeforeAll
    public static void config() {

        uri = Url.URL_API_REPURCHASE;
        request = new MeansurementRepurchase();
        request.setMeansurementFileId(9999L);
        request.setPld(12D); 
        request.setNotionalHired(999D);
        request.setProcessInstanceId("9999");      
        
        
    }

    @Test
    @Order(1)
    public void createAProduct() throws Exception {

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
    public void getAProduct() throws Exception {

        MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
        values.add("processInstanceId", this.request.getProcessInstanceId().toString());
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .queryParams(values)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(200, status);

    }

    @Test
    @Order(3)
    public void update() throws Exception {

        request.setPld(0d);
        request.setNotionalHired(0D);

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

        MeansurementRepurchase response = this.mapFromJson(mvcResult.getResponse().getContentAsString(), MeansurementRepurchase.class);

        assertEquals(request.getPld(), response.getPld());
        assertEquals(request.getNotionalHired(), response.getNotionalHired());

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
