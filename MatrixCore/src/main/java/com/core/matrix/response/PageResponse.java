/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;


import java.util.List;
import lombok.Data;

/**
 *
 * @author thiag
 */


@Data
public class PageResponse<T>  {    
    
    
    private List<T>content;   
    private Long totalPages;
    private Long totalElements;
    private Long size;
    private Long page;
    
    
    public PageResponse(List<T> content, Long totalElements, Long size, Long page ){
        this.content = content;
        this.totalPages = (totalElements == 0) || (size / (double)totalElements) < 1 ? 1 : (size / totalElements);
        this.totalElements = totalElements;
        this.size = size;  
        this.page = page;   
    }
    

   

   
   
    
}
