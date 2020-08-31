/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.dto.SubMarketDTO;
import com.core.matrix.model.Product;
import com.core.matrix.service.ProductService;
import static com.core.matrix.utils.Url.URL_API_PRODUCT;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_PRODUCT)
public class ProductResource extends Resource<Product, ProductService> {

    public ProductResource(ProductService service) {
        super(service);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "subMarket", required = false) Long subMarket,
            @RequestParam(name = "wbcCodigoPerfilCCEE", required = false) Long wbcCodigoPerfilCCEE,
            @RequestParam(name = "wbcPerfilCCEE", required = false) String wbcPerfilCCEE,
            @RequestParam(name = "subMarketDescription", required = false) String subMarketDescription,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        try {
            Page response = this.getService().find(subMarket, wbcCodigoPerfilCCEE, wbcPerfilCCEE, subMarketDescription, PageRequest.of(page, size, Sort.by("wbcPerfilCCEE")));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(ProductResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }
    
    @RequestMapping(value = "/allSubMarkets", method = RequestMethod.GET)
    public ResponseEntity getAllSubMarkets(){
        try {
            List<SubMarketDTO> response = this.getService().getAllSubMarkets();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(ProductResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(e.getMessage());
        }
    }

}
