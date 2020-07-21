/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.model;

import static com.core.matrix.utils.Constants.TABLE_SEQUENCES;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author thiag
 */
@Entity
@Table(name = "mtx_produtos")
@Data
@EqualsAndHashCode
public class Product implements Serializable, Model<Product> {

    protected static final long serialVersionUID = 1919164886573746902L;
    
    @Id
    @Column(name = "id_produtos")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "wbc_codigo_perfilCCEE")
    protected Long wbcCodigoPerfilCCEE;
        
    @Column(name = "wbc_perfilCCEE")
    protected String wbcPerfilCCEE;

    @Column(name = "wbc_siglaCCEE")
    protected String wbcSiglaCCEE;
        
    @Column(name = "wbc_submercado")
    protected Long subMarket;

    @Column(name = "wbc_descricao_submercado")
    protected String subMarketDescription;
    
    @Column(name = "pld")
    protected Double pld;

    @Column(name = "spread_compra")
    protected Double spreadPurchase;

    @Column(name = "spread_venda")
    protected Double spreadSale;

    @Generated(GenerationTime.ALWAYS)
    @Column(name = "preco_compra")
    protected Double pricePurchase;

    @Generated(GenerationTime.ALWAYS)
    @Column(name = "preco_venda")
    protected Double priceSale; 

}
