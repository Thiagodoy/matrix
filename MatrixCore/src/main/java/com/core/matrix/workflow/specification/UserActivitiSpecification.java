package com.core.matrix.workflow.specification;

import com.core.matrix.workflow.model.UserActiviti_;
import com.core.matrix.workflow.model.UserActiviti;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class UserActivitiSpecification {

    public static Specification<UserActiviti> filter(String searchValue) {

        List<Specification> predicatives = new ArrayList<>();

        predicatives.add(UserActivitiSpecification.firstName(searchValue));
        predicatives.add(UserActivitiSpecification.lastName(searchValue));
        predicatives.add(UserActivitiSpecification.email(searchValue));
        predicatives.add(UserActivitiSpecification.profile(searchValue));

        return predicatives.stream().reduce((a, b) -> a.or(b)).orElse(null);

    }

    public static Specification<UserActiviti> firstName(String firstName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(UserActiviti_.firstName)), "%" + firstName.toUpperCase().trim() + "%");
    }

    public static Specification<UserActiviti> lastName(String lastName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(UserActiviti_.lastName)), "%" + lastName.toUpperCase().trim() + "%");
    }

    public static Specification<UserActiviti> email(String email) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(UserActiviti_.email), "%" + email.trim() + "%");
    }

    public static Specification<UserActiviti> profile(String profile) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(UserActiviti_.profile), "%" + profile.trim() + "%");
    }
}
