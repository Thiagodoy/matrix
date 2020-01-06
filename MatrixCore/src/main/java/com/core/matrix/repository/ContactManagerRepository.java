/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.repository;

import com.core.matrix.model.ContactManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author thiag
 */
@Repository
public interface ContactManagerRepository extends JpaRepository<ContactManager, Long> {
    
}
