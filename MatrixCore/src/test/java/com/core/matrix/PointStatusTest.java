/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.service.MeansurementPointStatusService;
import com.core.matrix.utils.PointStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author thiag
 */
@SpringBootTest
public class PointStatusTest {

    @Autowired
    private MeansurementPointStatusService pointStatusService;

    @Test
    public void generateAllPoints() {

        pointStatusService.createPointStatus(7L, 2020L);

        MeansurementPointStatus status = pointStatusService.getPoint("ALFKFRENTR101");
        status.setStatus(PointStatus.READ);
        status.forceUpdate();

        
        status = pointStatusService.getPoint("THIAGO");
        

    }
}
