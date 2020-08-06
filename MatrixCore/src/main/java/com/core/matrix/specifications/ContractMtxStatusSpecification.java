/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.specifications;

import com.core.matrix.model.ContractMtxStatus;
import com.core.matrix.model.ContractMtxStatus_;
import com.core.matrix.utils.ContractStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author thiag
 */
public class ContractMtxStatusSpecification {

    public static Specification<ContractMtxStatus> find(String status, Long contract, Long month, Long year) {

        List<Specification> predicatives = new ArrayList<>();

        if (Optional.ofNullable(contract).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ContractMtxStatus_.wbcContract), contract));
        }

        if (Optional.ofNullable(status).isPresent()) {

            ContractStatus st = ContractStatus.valueOf(status);

            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ContractMtxStatus_.status), st));
        }

        if (Optional.ofNullable(year).isPresent()) {
            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ContractMtxStatus_.year), year));
        }

        if (Optional.ofNullable(month).isPresent()) {

            predicatives.add((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ContractMtxStatus_.month), month));
        }

        Specification<ContractMtxStatus> spc = predicatives.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return spc;
    }
}
