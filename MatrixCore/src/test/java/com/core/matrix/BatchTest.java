/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 *
 * @author thiag
 */
public class BatchTest {

    @Test
    public void generateFile() {

        List<String> teste = new ArrayList();

        teste.add("SPCNCRENTR101 (L)");
        teste.add("SPCNCRENTR101");
        teste.add("SPCNCRENTR101");
        teste.add("SPCNCRENTR101");
        
        
        boolean result = teste.stream().anyMatch(d-> d.contains("(L)"));   
        
        System.out.println(" Resultado -> " + result);

    }
}
