/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.matrix.resource;

import com.core.matrix.model.Email;
import com.core.matrix.model.Template;
import com.core.matrix.request.AuthRequest;
import com.core.matrix.request.ChangePassword;
import com.core.matrix.request.ForgotPasswordRequest;
import com.core.matrix.response.AuthResponse;
import com.core.matrix.service.AuthService;
import com.core.matrix.service.TemplateService;
import com.core.matrix.specifications.TemplateSpecification;
import com.core.matrix.utils.Constants;
import com.core.matrix.utils.JwtTokenUtil;
import com.core.matrix.utils.ThreadPoolEmail;
import static com.core.matrix.utils.Url.URL_API_AUTH;
import com.core.matrix.utils.Utils;
import com.core.matrix.workflow.model.AbilityActiviti;
import com.core.matrix.workflow.model.UserActiviti;
import com.core.matrix.workflow.model.UserInfoActiviti;
import com.core.matrix.workflow.repository.UserInfoRepository;
import com.core.matrix.workflow.service.AbilityService;
import com.core.matrix.workflow.service.UserActivitiService;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thiag
 */
@RestController
@RequestMapping(value = URL_API_AUTH)
public class AuthResource {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserInfoRepository infoRepository;

    @Autowired
    private UserActivitiService userActivitiService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AbilityService abilityService;

    @org.springframework.beans.factory.annotation.Value("${portal.url}")
    private String urlPortal;

    @Autowired
    private ThreadPoolEmail threadPoolEmail;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity auth(@RequestBody AuthRequest request) {

        try {
            authenticate(request.getUsername(), request.getPassword());
            final UserDetails userDetails = authService
                    .loadUserByUsername(request.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);

            UserActiviti user = (UserActiviti) userDetails;
            Optional<UserInfoActiviti> optional = user.getInfo()
                    .stream()
                    .filter(info -> info.getKey().equals(Constants.USER_INFO_ATTEMPTS))
                    .findFirst();

            if (optional.isPresent()) {
                UserInfoActiviti userInfo = optional.get();

                Long attempts = Long.parseLong(userInfo.getValue());

                if (attempts.compareTo(1L) > 0) {
                    userInfo.setValue("1");
                    infoRepository.save(userInfo);
                }

                List<UserInfoActiviti> list = user.getInfo()
                        .stream()
                        .filter(info -> !info.getKey().equals(Constants.USER_INFO_ATTEMPTS))
                        .collect(Collectors.toList());
                user.setInfo(list);

            } else {
                UserInfoActiviti userInfo = new UserInfoActiviti();
                userInfo.setUserId(user.getId());
                userInfo.setKey(Constants.USER_INFO_ATTEMPTS);
                userInfo.setValue("1");
                infoRepository.save(userInfo);
            }

            ((UserActiviti) userDetails).getGroups().stream().forEach(g -> {
                List<AbilityActiviti> abilityActivitis = abilityService.findByGroup(g.getGroupId());
                g.setAbilitys(abilityActivitis);
            });

            return ResponseEntity.ok(new AuthResponse(token, userDetails));

        } catch (Exception ex) {
            Logger.getLogger(AuthResource.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(ex.getMessage());
        }

    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity checkToken(HttpServletRequest request) {

        try {

            final String requestTokenHeader = request.getHeader("Authorization");
            String jwtToken = null;

            jwtToken = requestTokenHeader.substring(7);

            boolean isValid = jwtTokenUtil.isTokenExpired(jwtToken);

            if (!isValid) {
                return ResponseEntity.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception ex) {
            Logger.getLogger(AuthResource.class.getName()).log(Level.SEVERE, "[ checkToken ]", ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body("TOKEN_EXPIRED");
        }

    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {

            try {
                UserDetails userDetails = authService.loadUserByUsername(username);
                UserActiviti user = (UserActiviti) userDetails;
                Optional<UserInfoActiviti> optional = user.getInfo()
                        .stream()
                        .filter(info -> info.getKey().equals(Constants.USER_INFO_ATTEMPTS))
                        .findFirst();

                if (optional.isPresent()) {

                    UserInfoActiviti userInfo = optional.get();
                    Long attempts = Long.parseLong(userInfo.getValue());

                    attempts += 1;

                    userInfo.setValue(attempts.toString());
                    infoRepository.save(userInfo);

                    if (attempts > 3) {
                        user.setEnabled(false);
                        user.setBlockedForAttemps(true);
                        userActivitiService.save(user);
                        throw new Exception("USER_BLOCKED", e);
                    }
                } else {
                    UserInfoActiviti userInfo = new UserInfoActiviti();
                    userInfo.setUserId(user.getId());
                    userInfo.setKey(Constants.USER_INFO_ATTEMPTS);
                    userInfo.setValue("1");
                    infoRepository.save(userInfo);
                }

            } catch (UsernameNotFoundException ee) {
            }

            throw new Exception("INVALID_CREDENTIALS", e);

        }
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity changePassword(@RequestBody ChangePassword request, Principal principal) {
        try {

            UserActiviti user = (UserActiviti) authService.loadUserByUsername(principal.getName());

            user.setPassword(request.getNewPassword());
            userActivitiService.update(user);

            return ResponseEntity.ok().build();

        } catch (Exception ex) {
            Logger.getLogger(AuthResource.class.getName()).log(Level.SEVERE, "[ changePassword ]", ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ResponseEntity forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {

            final UserActiviti userDetails = (UserActiviti) authService.loadUserByUsername(request.getEmail());

            if (userDetails.isEnabled()) {

                final String token = jwtTokenUtil.generateToken(userDetails);
                final String userName = userDetails.getFirstName();

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
            }else{
                new Exception("USER_DISABLED");
            }

            return ResponseEntity.ok().build();

        } catch (Exception ex) {
            Logger.getLogger(AuthResource.class.getName()).log(Level.SEVERE, "[ forgotPassword ]", ex);
            return ResponseEntity.status(HttpStatus.resolve(500)).body(ex.getMessage());
        }
    }

}
