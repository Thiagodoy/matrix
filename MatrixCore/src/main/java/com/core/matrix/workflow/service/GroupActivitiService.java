/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.workflow.model.GroupActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.repository.GroupRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.activiti.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author thiag
 */
@Service
public class GroupActivitiService {

    @Autowired
    private GroupRepository repository;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private UserActivitiService userActivitiService;

    @Transactional
    public void save(GroupActiviti group) {
        this.repository.save(group);
    }

    @Transactional
    public void delete(String id) {
        this.repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<GroupActiviti> listAll(Specification spc, Pageable page) {
        return this.repository.findAll(spc, page);
    }

    @Transactional(readOnly = true)
    public GroupActiviti findById(String id) throws Exception {
        return this.repository.findById(id).orElseThrow(() -> new Exception("NOT_FOUND_GROUP"));
    }

    @Transactional(readOnly = true)
    public List<GroupActiviti> listAllCache() {
        return this.repository.findAll();
    }

    @Transactional(readOnly = true, transactionManager = "transactionManager")
    public List<GroupActiviti> listByTask(String id) {
        return this.repository.findByTaskId(id);
    }

    @Transactional(readOnly = true)
    public List<String> getGroupByTaskOrProcessDefId(String taskId, String processDefId) {
        return this.identityService.createNativeGroupQuery().sql("SELECT \n"
                + "    aim.GROUP_ID_ AS ID_\n"
                + "FROM\n"
                + "    activiti.ACT_RU_IDENTITYLINK ari\n"
                + "        LEFT JOIN\n"
                + "    activiti.ACT_ID_MEMBERSHIP aim ON ari.GROUP_ID_ = aim.GROUP_ID_\n"
                + "        LEFT JOIN\n"
                + "    activiti.ACT_ID_USER u ON aim.USER_ID_ = u.ID_\n"
                + "WHERE\n"
                + "    ari.TYPE_ = 'candidate'\n"
                + "        AND (ari.TASK_ID_ = '" + taskId + "'\n"
                + "        OR ari.PROC_DEF_ID_ = '" + processDefId + "')")
                .list()
                .stream()
                .map(g-> g.getId())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserActiviti> getUsersByIdGroup(String id) {

        return this.identityService
                .createUserQuery()
                .memberOfGroup(id)
                .list()
                .stream()
                .map(u -> getUser(u.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private UserActiviti getUser(String email) {

        try {
            return userActivitiService.findById(email);
        } catch (Exception e) {
            return null;
        }
    }

}
