/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author thiag
 */
@Data
@EqualsAndHashCode(of = {"details","type","informations"})
public class FileParsedDTO implements Serializable{

    private static final long serialVersionUID = 5296134700280186728L;
    
    public List<InformationDTO> informations;
    public HeaderDTO header;         
    public List<FileDetailDTO> details;
    public String type;
}
