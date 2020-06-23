/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class HeaderDTO implements Serializable{

    private static final long serialVersionUID = -8660786212320941769L;
    
    public String headeragent;
    public String headermeansurementPoint;
    public String headerdate;
    public String headerhour;
    public String headerenergyType;
    public String headergenerationActive;
    public String headerconsumptionActive;
    public String headergenerationReactivate;
    public String headerconsumptionReactivate;
    public String headerrange;
    public String headersituation;
    public String headerreason;
    public String headersourceCollection;
    public String headernotificationCollection;
    public String headerquality;
    public String headerorigem;
    
    
    
    
    
    public long countValuesNonNull(){
        
        return Arrays.asList(HeaderDTO.class.getDeclaredFields()).stream().map(field->{
            
            try {
                return (String) field.get(this);
            } catch (Exception ex) {
                return null;
            } 
        
        }).filter(Objects::nonNull).count();
    }

}
