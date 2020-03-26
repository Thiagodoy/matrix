/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.AuthorityApproval;
import com.core.matrix.model.AuthorityApproval_;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class AuthorityApprovalSpecification {

    public static Specification<AuthorityApproval> id(Long id) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(AuthorityApproval_.id), id);
    }

    public static Specification<AuthorityApproval> authority(String authority) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(AuthorityApproval_.authority), authority);
    }

}
