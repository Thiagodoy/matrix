/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.MeansurementFileAuthority;
import com.core.matrix.model.MeansurementFileAuthority_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class MeansurementFileAuthoritySpecification {

    public static Specification<MeansurementFileAuthority> authority(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementFileAuthority_.authority), name);
    }

    public static Specification<MeansurementFileAuthority> user(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementFileAuthority_.user), name);
    }

    public static Specification<MeansurementFileAuthority> userName(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(MeansurementFileAuthority_.userName)), "%" + name.toUpperCase() + "%");
    }

    public static Specification<MeansurementFileAuthority> processIntanceId(String id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementFileAuthority_.processInstanceId), id);
    }
}
