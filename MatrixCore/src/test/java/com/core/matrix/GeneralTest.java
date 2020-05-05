/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import com.core.matrix.model.MeansurementFileDetail;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author thiag
 */
public class GeneralTest {

    
    private static MeansurementFileDetail mfd;

    @BeforeAll
    public static void init(){
         mfd = new MeansurementFileDetail();
    }

     @Test
     public void parseValue(){         
         Double result = mfd.parseToDouble("1.800,643");
         Assert.assertEquals(1800.643, result, 0.00001);
     }
     
     
     @Test
     public void parseNoValue(){         
         Double result = mfd.parseToDouble("");
         Assert.assertEquals(0.0, result, 0.00001);
     }

     @Test
     public void parseNull(){         
         Double result = mfd.parseToDouble(null);
         Assert.assertEquals(0.0, result, 0.00001);
     }
     
     @Test
     public void parseValueCurrency(){         
         Double result = mfd.parseToDouble("R$ 1.800,643");
         Assert.assertEquals(1800.643, result, 0.00001);
     }
    
     
     @Test
     public void parseValueWithSpace(){         
         Double result = mfd.parseToDouble(" 1.800,643");
         Assert.assertEquals(1800.643, result, 0.00001);
     }
     
     
    
     
}
