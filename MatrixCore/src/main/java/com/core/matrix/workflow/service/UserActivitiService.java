/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.workflow.service;

import com.core.matrix.model.Email;
import com.core.matrix.model.Template;
import com.core.matrix.request.UserDeleteRequest;
import com.core.matrix.request.UserInfoRequest;
import com.core.matrix.response.UserInfoResponse;
import com.core.matrix.service.TemplateService;
import com.core.matrix.specifications.TemplateSpecification;
import com.core.matrix.utils.Constants;
import com.core.matrix.utils.ThreadPoolEmail;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.GroupActiviti;
import com.core.matrix.workflow.model.GroupMemberActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.model.UserInfoActiviti;
import com.core.matrix.workflow.repository.UserRepository;
import com.core.matrix.workflow.specification.UserActivitiSpecification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
public class UserActivitiService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private ThreadPoolEmail threadPoolEmail;

    @Autowired
    private TemplateService templateService;

    @Transactional
    public void save(UserActiviti user) throws Exception {

        Optional<UserActiviti> optUser = repository.findById(user.getEmail());

        if (optUser.isPresent()) {
            throw new Exception("USER_ALREADY_EXISTS_ON_BASE");
        }

        String passwordTemp = user.getPassword();
        user.setPassword(Utils.encodePassword(user.getPassword()));
        Optional<GroupMemberActiviti> opt = user.getGroups().stream().findFirst();
        String profile = opt.isPresent() ? opt.get().getGroupId() : "without-profile";
        user.setProfile(profile);

        this.repository.save(user);

        user.setPassword(passwordTemp);

        this.sendEmailWelCome(user);

    }

    private void sendEmailWelCome(UserActiviti user) throws Exception {

        Specification spc = TemplateSpecification.filter(null, null, null, Template.TemplateBusiness.WELCOME_USER);
        Template template = (Template) templateService.find(spc, Pageable.unpaged()).getContent().get(0);

        Map<String, String> data = new HashMap<String, String>();

        data.put(Constants.TEMPLATE_PARAM_USER_EMAIL, user.getEmail());
        data.put(Constants.TEMPLATE_PARAM_USER_NAME, user.getFirstName());
        data.put(Constants.TEMPLATE_PARAM_USER_PASSWORD, user.getPassword());

        String emailData = Utils.mapToString(data);

        Email email = new Email();
        email.setTemplate(template);
        email.setData(emailData);

        try {
            threadPoolEmail.submit(email);
        } catch (Throwable e) {
            Logger.getLogger(ThreadPoolEmail.class.getName()).log(Level.SEVERE, "Erro ao enviar email para o usuário ->" + user.getEmail());
        }

    }

    @Transactional
    public void update(UserActiviti update) throws Exception {
        UserActiviti entity = this.findById(update.getId());
        boolean encrypt = false;

        if (Optional.ofNullable(update.getPassword()).isPresent() && !update.getPassword().equals(entity.getPassword())) {
            encrypt = true;
        }

        entity.update(update);

        if (encrypt) {
            entity.setPassword(Utils.encodePassword(entity.getPassword()));
        }

        
        //workaround
        update.getInfo().forEach(info -> {
            Optional<UserInfoActiviti> optModel = entity.getInfo().stream().filter(mm -> mm.getId().equals(info.getId())).findFirst();
            if (optModel.isPresent()) {
                optModel.get().update(info);
            } else {
                entity.getInfo().add(info);
            }
        });

        entity.getInfo().forEach(m -> {
            Optional<UserInfoActiviti> optModel = update.getInfo().stream().filter(mm -> mm.getId().equals(m.getId())).findFirst();
            if (!optModel.isPresent()) {
                 entity.getInfo().remove(m);
            }
        });
        
        
         update.getGroups().forEach(group -> {
            Optional<GroupMemberActiviti> optModel = entity.getGroups().stream().filter(mm -> mm.getId().equals(group.getId())).findFirst();
            if (optModel.isPresent()) {
                optModel.get().update(group);
            } else {
                entity.getGroups().add(group);
            }
        });

        List<GroupMemberActiviti> temp = new ArrayList<>();
        entity.getGroups().forEach(m -> {
            Optional<GroupMemberActiviti> optModel = update.getGroups().stream().filter(mm -> mm.getId().equals(m.getId())).findFirst();
            if (!optModel.isPresent()) {
                 temp.add(m);
            }
        });

        temp.forEach(g->{
        
            entity.getGroups().remove(g);
        
        });
        

        repository.save(entity);

    }

    @Transactional
    public void delete(UserDeleteRequest request) {

        UserActiviti user = repository.findById(request.getEmail()).get();
        this.repository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserActiviti findById(String id) throws Exception {
        return this.repository.
                findById(id)
                .orElseThrow(() -> new Exception("User not found."));
    }

    public List<UserInfoResponse> getUserInfo(UserInfoRequest request) {
        return this.repository.findAllById(request.getUsers()).parallelStream().map(u -> new UserInfoResponse(u)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserActiviti> list(Specification spc, Pageable page) {
        return repository.findAll(spc, page);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public Page<UserActiviti> list(String firstName, String lastName, String email, String profile, Pageable page) {
        List<Specification<UserActiviti>> predicates = new ArrayList<>();

        if (Optional.ofNullable(firstName).isPresent()) {
            predicates.add(UserActivitiSpecification.firstName(firstName));
        }

        if (Optional.ofNullable(lastName).isPresent()) {
            predicates.add(UserActivitiSpecification.lastName(lastName));
        }

        if (Optional.ofNullable(email).isPresent()) {
            predicates.add(UserActivitiSpecification.email(email));
        }

        if (Optional.ofNullable(profile).isPresent()) {
            predicates.add(UserActivitiSpecification.profile(profile));
        }

        Specification<UserActiviti> specification = predicates.stream().reduce((a, b) -> a.and(b)).orElse(null);

        return repository.findAll(specification, page);
    }

    public GroupActiviti getProfile() {
        return null;
    }

    @Transactional(readOnly = true)
    public boolean checkEmail(String email) {
        return this.repository.findById(email).isPresent();
    }

}
