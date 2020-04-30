/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.dto.MeansurementFileResultStatusDTO;
import com.core.matrix.service.ReportService;
import com.core.matrix.utils.ReportConstants;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 *
 * @author thiag
 */
public class ReportTest {

    @Test
    public void generateHeaderFUll() {

        final String[] header = {"ANO", "MES", "CÓD. CCEE", "CONTRATO", "PONTO MEDIÇÃO", "STATUS", "MONTANTE"};

        List<MeansurementFileResultStatusDTO> list = new ArrayList<>();
        list.add(new MeansurementFileResultStatusDTO());

        String[] value = new ReportService().mountHeader(list, ReportConstants.ReportType.FULL);

        Assert.assertArrayEquals("Validação do Header", header, value);

    }

    @Test
    public void generateHeaderSHORT() {

        final String[] header = {"ANO", "MES", "CÓD. CCEE", "CONTRATO", "MONTANTE"};

        List<MeansurementFileResultStatusDTO> list = new ArrayList<>();
        list.add(new MeansurementFileResultStatusDTO());

        String[] value = new ReportService().mountHeader(list, ReportConstants.ReportType.SHORT);

        Assert.assertArrayEquals("Validação do Header", header, value);

    }

    @Test
    public void getFieldsFull() {

        List<Field> fields = new ReportService().getField(MeansurementFileResultStatusDTO.class, ReportConstants.ReportType.FULL);

        Assert.assertEquals("Quantidade de campos", 7, fields.size());

    }

    @Test
    public void getFieldsSHORT() {

        List<Field> fields = new ReportService().getField(MeansurementFileResultStatusDTO.class, ReportConstants.ReportType.SHORT);

        Assert.assertEquals("Quantidade de campos", 5, fields.size());

    }

    @Test
    public void getData() throws IllegalArgumentException, IllegalAccessException {

        List<Field> fields = new ReportService().getField(MeansurementFileResultStatusDTO.class, ReportConstants.ReportType.FULL);

        MeansurementFileResultStatusDTO dat = new MeansurementFileResultStatusDTO();

        dat.setMonth(10L);
        dat.setYear(2020L);
        dat.setScde("");
        dat.setMount(20d);
        dat.setStatus("TESTE");
        dat.setWbcContract(99999L);
        dat.setWbcMeansurementPoint("XXXXXXXXXXX");
        
        Object[] data = new ReportService().getData(fields, dat);

       // Assert.assertEquals("Quantidade de campos", 5, fields.size());

    }

    @Test
    public void generateDataFULL() {

        final String[] header = {"ANO", "MES", "CÓD. CCEE", "CONTRATO", "MONTANTE"};

        List<MeansurementFileResultStatusDTO> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            MeansurementFileResultStatusDTO dat = new MeansurementFileResultStatusDTO();

            dat.setMonth(10L);
            dat.setYear(2020L);
            dat.setScde("");
            dat.setMount(20d);
            dat.setStatus("TESTE");
            dat.setWbcContract(99999L);
            dat.setWbcMeansurementPoint("XXXXXXXXXXX");
            list.add(dat);
        }

        String[] value = new ReportService().mountHeader(list, ReportConstants.ReportType.SHORT);

        Assert.assertArrayEquals("Validação do Header", header, value);

    }

}
