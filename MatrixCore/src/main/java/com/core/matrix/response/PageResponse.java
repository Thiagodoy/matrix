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
public class PageResponse<T> {

    private List<T> content;
    private Long totalPages;
    private Long totalElements;
    private Long size;
    private Long page;
    private boolean first;
    private boolean last;
    private Long number;

    public PageResponse(List<T> content, Long totalElements, Long sizePerPage, Long page) {
        this.content = content;
        this.totalPages = (long) Math.ceil((totalElements / sizePerPage.doubleValue()));
        this.totalElements = totalElements;
        this.size = sizePerPage;
        this.page = page;
        this.first = page == 0;
        this.last = (this.totalPages - 1) == page;
        this.number = page;
    }

}
