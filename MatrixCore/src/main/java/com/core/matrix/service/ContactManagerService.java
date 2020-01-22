/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.service;

import com.core.matrix.model.ContactManager;
import com.core.matrix.model.Manager;
import com.core.matrix.repository.ContactManagerRepository;
import com.core.matrix.repository.ManagerRepository;
import com.core.matrix.request.ContactManagerRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class ContactManagerService {

    @Autowired
    private ContactManagerRepository repository;

    @Autowired
    private ManagerRepository managerRepository;

    @Transactional
    public Long save(ContactManagerRequest request) {
        ContactManager contactManager = new ContactManager(request);
        return this.repository.save(contactManager).getId();
    }

    @Transactional
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    public Page<ContactManager> find(Long managerId, PageRequest page) throws Exception {

        if (Optional.ofNullable(managerId).isPresent()) {
            Manager manager = this.managerRepository.findById(managerId).orElseThrow(() -> new Exception("not found Manager!"));
            return this.repository.findByManager(manager, page);
        }
        return this.repository.findAll(page);
    }

    @Transactional
    public void update(ContactManagerRequest request) throws Exception {

        ContactManager contactManager = this.repository.findById(request.getId()).orElseThrow(() -> new Exception("Not found ContactManager!"));

        if (Optional.ofNullable(request.getName()).isPresent() && !contactManager.getName().equals(request.getName())) {
            contactManager.setName(request.getName());
        }

        if (Optional.ofNullable(request.getEmail()).isPresent() && !contactManager.getEmail().equals(request.getEmail())) {
            contactManager.setEmail(request.getEmail());
        }

        if (Optional.ofNullable(request.getTelephone1()).isPresent() && !contactManager.getTelephone1().equals(request.getTelephone1())) {
            contactManager.setTelephone1(request.getTelephone1());
        }

        if (Optional.ofNullable(request.getTelephone2()).isPresent() && !contactManager.getTelephone2().equals(request.getTelephone2())) {
            contactManager.setTelephone1(request.getTelephone2());
        }

        if (Optional.ofNullable(request.getTelephone3()).isPresent() && !contactManager.getTelephone3().equals(request.getTelephone3())) {
            contactManager.setTelephone1(request.getTelephone3());
        }

        this.repository.save(contactManager);
    }

}
