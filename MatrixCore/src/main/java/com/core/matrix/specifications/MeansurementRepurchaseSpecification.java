/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.MeansurementRepurchase;
import com.core.matrix.model.MeansurementRepurchase_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class MeansurementRepurchaseSpecification {

    public static Specification<MeansurementRepurchase> id(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementRepurchase_.id), id);
    }

    public static Specification<MeansurementRepurchase> processIntanceId(String id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementRepurchase_.processInstanceId), id);
    }

    public static Specification<MeansurementRepurchase> meansurementFileId(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementRepurchase_.meansurementFileId), id);
    }

}
