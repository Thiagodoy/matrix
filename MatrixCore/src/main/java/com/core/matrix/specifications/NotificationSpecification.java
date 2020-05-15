/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.Email;
import com.core.matrix.model.Email_;
import com.core.matrix.model.Notification;
import com.core.matrix.model.Notification_;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class NotificationSpecification {

    public static Specification<Notification> isRead() {

        List<Specification> predicatives = new ArrayList<>();

        predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(Notification_.createdAt), LocalDateTime.now()));
        predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(Notification_.isRead), true));

        Specification<Notification> spc = predicatives.stream().reduce((a, b) -> a.and(b)).orElse(null);
        return spc;
    }

}
