/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.dto;

import java.util.List;
import lombok.Data;

/**
 *
 * @author thiag
 */
@Data
public class FileParsedDTO {

    public List<InformationDTO> informations;
    public HeaderDTO header;         
    public List<FileDetailDTO> details;
    public String type;
}
