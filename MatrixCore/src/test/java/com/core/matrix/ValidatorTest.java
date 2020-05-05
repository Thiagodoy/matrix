/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.validator.Validator;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;

/**
 *
 * @author thiag
 */
public class ValidatorTest {

    private static Validator validator;

    public ValidatorTest() {
        validator = new Validator();
        validator.setErrors(new ArrayList<>());
    }

    @Test
    public void validateMeansurementPointSuccess() {

        validator.validateMeansurementPoint(1L, "SPTOY-ENTR101");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);

    }

    @Test
    public void validateMeansurementPointError() {
        validator.validateMeansurementPoint(1L, null);
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(false, result);

    }

    @Test
    public void validateDateSuccess() {

        validator.validateDate(0l, "2020-02-23");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);

    }

    @Test
    public void validateDateError() {

        validator.validateDate(0l, "2020-02-");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(false, result);

    }

    @Test
    public void validateHourSuccess() {

        validator.validateHour(0l, "2");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);
    }

    @Test
    public void validateHourError() {

        validator.validateHour(0l, "");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(false, result);
    }

    @Test
    public void consumptionActiveOk() {

        validator.validateConsumptionActive(0l, "200,34");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);
    }

    @Test
    public void consumptionActiveThousandOk() {

        validator.validateConsumptionActive(0l, "1.800,643");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);
    }

    @Test
    public void consumptionActiveThousand2Ok() {

        validator.validateConsumptionActive(0l, "1.800");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);
    }
    
     @Test
    public void consumptionActive2Ok() {

        validator.validateConsumptionActive(0l, "1800");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);
    }

    @Test
    public void consumptionActiveError() {

        validator.validateConsumptionActive(0l, "a55,98");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(false, result);
    }

    @Test
    public void energyTypeSuccess() {

        validator.validateEnergyType(0l, "ABC");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);
    }

    @Test
    public void energyTypeError() {

        validator.validateEnergyType(0l, "");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(false, result);
    }

    @Test
    public void sourceCollectionSuccess() {

        validator.validateSourceCollection(0l, "ABC");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(true, result);
    }

    @Test
    public void sourceCollectionError() {

        validator.validateSourceCollection(0l, "");
        boolean result = validator.getErrors().isEmpty();
        validator.getErrors().clear();
        Assert.assertEquals(false, result);
    }

}
