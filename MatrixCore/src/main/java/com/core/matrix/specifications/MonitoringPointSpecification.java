/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.MonitoringPoint;
import com.core.matrix.model.MonitoringPoint_;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class MonitoringPointSpecification {

    public static Specification<MonitoringPoint> find(Long month, Long year, String status) {

        List<Specification> predicatives = new ArrayList<>();

        if (Optional.ofNullable(month).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MonitoringPoint_.month), month));
        }

        if (Optional.ofNullable(year).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MonitoringPoint_.year), year));
        }

        if (Optional.ofNullable(status).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MonitoringPoint_.status), status));
        }

        Specification<MonitoringPoint> spc = predicatives.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return spc;
    }

}
