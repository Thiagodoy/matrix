/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.request;

import java.util.Map;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class StartProcessRequest {

    private String key;
    private Map<String, Object> variables;

}
