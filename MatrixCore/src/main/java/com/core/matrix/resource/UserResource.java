/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Email;
import com.core.matrix.model.Template;
import com.core.matrix.request.UnblockUserRequest;
import com.core.matrix.request.UserDeleteRequest;
import com.core.matrix.request.UserInfoRequest;
import com.core.matrix.response.UserInfoResponse;
import com.core.matrix.service.TemplateService;
import com.core.matrix.specifications.TemplateSpecification;
import com.core.matrix.utils.Constants;
import com.core.matrix.utils.JwtTokenUtil;
import com.core.matrix.utils.ThreadPoolEmail;
import static com.core.matrix.utils.Url.URL_API_USER;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.service.UserActivitiService;
import com.core.matrix.workflow.specification.UserActivitiSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_USER)
public class UserResource {

    @Autowired
    private UserActivitiService service;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TemplateService templateService;

    @org.springframework.beans.factory.annotation.Value("${portal.url}")
    private String urlPortal;

    @Autowired
    private ThreadPoolEmail threadPoolEmail;

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public ResponseEntity getUserInfo(@RequestBody UserInfoRequest request) {
        try {
            List<UserInfoResponse> response = this.service.getUserInfo(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[getUserInfo]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get(
            @RequestParam(name = "searchValue", required = false) String searchValue,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {

            
            Specification spc = null;
            
            if(Optional.ofNullable(id).isPresent()){
                spc = UserActivitiSpecification.id(id);
            }else{
                spc = UserActivitiSpecification.filter(searchValue);
            }
            

            Page<UserActiviti> response = this.service.list(spc, PageRequest.of(page, size, Sort.by("firstName")));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }
    
    
     @RequestMapping(value="/checkEmail",method = RequestMethod.GET)
    public ResponseEntity checkEmail(
            @RequestParam(name = "email") String email) {
        try {
            return ResponseEntity.ok(this.service.checkEmail(email));
        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[get]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }
    

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody UserActiviti request) {
        try {

            this.service.save(request);
            return ResponseEntity.ok().build();

        } catch (ConstraintViolationException e) {
            org.jboss.logging.Logger.getLogger(ManagerResource.class.getName()).log(org.jboss.logging.Logger.Level.FATAL, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).body("Usuário " + request.getEmail() + " já cadastrado!");
        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity put(@RequestBody UserActiviti request) {
        try {

            this.service.update(request);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[put]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(value = "/unblock", method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody UnblockUserRequest request) {
        try {

            final UserActiviti userDetails = service.findById(request.getEmail());
            userDetails.setEnabled(true);
            this.service.save(userDetails);

            final String token = jwtTokenUtil.generateToken(userDetails);
            final String userName = ((UserActiviti) userDetails).getFirstName();

            Specification spc = TemplateSpecification.filter(null, null, null, Template.TemplateBusiness.FORGOT_PASSWORD);
            Template template = (Template) templateService.find(spc, Pageable.unpaged()).getContent().get(0);

            Map<String, String> data = new HashMap<String, String>();

            data.put(Constants.TEMPLATE_PARAM_LINK, urlPortal + "?token=" + token);
            data.put(Constants.TEMPLATE_PARAM_USER_EMAIL, request.getEmail());
            data.put(Constants.TEMPLATE_PARAM_USER_NAME, userName);

            String emailData = Utils.mapToString(data);

            Email email = new Email();
            email.setTemplate(template);
            email.setData(emailData);

            threadPoolEmail.submit(email);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[post]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity delete(@RequestBody UserDeleteRequest request) {
        try {

            this.service.delete(request);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            Logger.getLogger(UserResource.class.getName()).log(Level.SEVERE, "[delete]", e);
            return ResponseEntity.status(HttpStatus.resolve(500)).build();
        }
    }

}
