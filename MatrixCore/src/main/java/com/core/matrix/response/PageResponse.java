/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.response;


import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;

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
    
    
    public PageResponse(List<T> content, Long totalPages, Long totalElements, Long size, Long page ){
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;  
        this.page = page;   
    }
    

   

   
   
    
}
