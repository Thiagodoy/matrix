/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.MeansurementPointStatus;
import com.core.matrix.model.MeansurementPointStatus_;
import com.core.matrix.utils.PointStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class MeansurementPointStatusSpecification {

    public static Specification<MeansurementPointStatus> find(String status, String point, Long month, Long year) {

        List<Specification> predicatives = new ArrayList<>();

        if (Optional.ofNullable(point).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(MeansurementPointStatus_.point)), "%" + point.toUpperCase() + "%"));
        }

        if (Optional.ofNullable(status).isPresent()) {

            PointStatus st = PointStatus.valueOf(status);

            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementPointStatus_.status), st));
        }

        if (Optional.ofNullable(year).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementPointStatus_.year), year));
        }

        if (Optional.ofNullable(month).isPresent()) {

            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MeansurementPointStatus_.month), month));
        }

        Specification<MeansurementPointStatus> spc = predicatives.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return spc;
    }
}
