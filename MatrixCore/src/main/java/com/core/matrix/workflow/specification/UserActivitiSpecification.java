package com.core.matrix.workflow.specification;
import com.core.matrix.workflow.model.UserActiviti_;
import com.core.matrix.workflow.model.UserActiviti;

import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class UserActivitiSpecification {

    public static Specification<UserActiviti> firstName(String firstName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(UserActiviti_.firstName)), firstName.toUpperCase() + "%");
    }

    public static Specification<UserActiviti> lastName(String lastName) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get(UserActiviti_.lastName)), lastName.toUpperCase() + "%");
    }

    public static Specification<UserActiviti> email(String email) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(UserActiviti_.email), email + "%");
    }
    
     public static Specification<UserActiviti> profile(String profile) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(root.get(UserActiviti_.profile), profile + "%");
    }
}
