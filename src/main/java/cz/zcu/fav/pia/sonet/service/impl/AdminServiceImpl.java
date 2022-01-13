package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.domain.RoleEnum;
import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.entity.RoleEntity;
import cz.zcu.fav.pia.sonet.entity.UserEntity;
import cz.zcu.fav.pia.sonet.repository.RoleEntityRepository;
import cz.zcu.fav.pia.sonet.repository.UserEntityRepository;
import cz.zcu.fav.pia.sonet.service.AdminService;
import cz.zcu.fav.pia.sonet.service.UserService;
import cz.zcu.fav.pia.sonet.util.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;

@Service("adminService")
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestScope
public class AdminServiceImpl implements AdminService {

    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;

    private final UserService userService;

    private List<UserDomain> userDomainList;
    private UserDomain user;

    private String newPassword1;
    private String newPassword2;

    private final PasswordEncoder encoder;

    public List<UserDomain> getUserDomainList() {
        if (this.userDomainList == null) {
            this.userDomainList = getUsers();
        }

        return userDomainList;
    }

    private List<UserDomain> getUsers() {
        List<UserDomain> userDomainList = new LinkedList<>();

        for (UserEntity userEntity : this.userEntityRepository.findAll()) {
            if (!userEntity.isDeleted()) {

                Set<GrantedAuthority> authorities = new HashSet<>();

                for (RoleEntity currentRole : userEntity.getRoles()) {
                    authorities.add(new SimpleGrantedAuthority(Utils.toSpringRole(currentRole)));
                }

                userDomainList.add(new UserDomain(
                        userEntity.getId(),
                        userEntity.getUsername(),
                        userEntity.getPassword(),
                        userEntity.getFirstName(),
                        userEntity.getLastName(),
                        authorities
                ));
            }
        }

        return Collections.unmodifiableList(userDomainList);
    }

    public void createUser() {
        this.user = UserDomain.builder().build();
    }

    public UserDomain getUser() {
        if (this.user == null) {
            this.user = new UserDomain();
        }

        return this.user;
    }

    public boolean isHasUsername() {
        if (this.user == null) {
            return false;
        }

        if (StringUtils.hasText(this.user.getUsername())) {
            return true;
        }

        return false;
    }

    public void grantAdmin() {
        if (this.user == null) {
            return;
        }

        UserEntity userEntity = userEntityRepository.findUserEntityByUsername(user.getUsername());
        RoleEntity roleEntity = roleEntityRepository.findRoleEntityByCode(RoleEnum.ADMIN.getCode());

        if (user.isAdmin()) {
            userEntity.getRoles().remove(roleEntity);
        }
        else {
            userEntity.getRoles().add(roleEntity);
        }
        userEntityRepository.save(userEntity);

        userDomainList = null;
    }

    public boolean isHasFirstName() {
        if (this.user == null) {
            return false;
        }

        if (StringUtils.hasText(this.user.getFirstName())) {
            return true;
        }

        return false;
    }

    public boolean isHasLastName() {
        if (this.user == null) {
            return false;
        }

        if (StringUtils.hasText(this.user.getLastName())) {
            return true;
        }

        return false;
    }

    private boolean setPasswordInternal() {
        if (!Objects.equals(newPassword1, newPassword2)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "New passwords don't match!", null));
            return false;
        }

        if (!StringUtils.hasText(newPassword1)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "New password cannot be empty!", null));
            return false;
        }

        String encoded = encoder.encode(newPassword1);

        this.user.setPassword(encoded);

        return true;
    }

    public void saveUser() {
        if (this.user == null) {
            return;
        }

        if (!StringUtils.hasText(this.user.getUsername())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email must not be empty!", null));
            return;
        }

        if (!StringUtils.hasText(this.user.getFirstName())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "First name must not be empty!", null));
            return;
        }

        if (!StringUtils.hasText(this.user.getLastName())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Last name must not be empty!", null));
            return;
        }

        if (this.setPasswordInternal()) {
            addUser(user);
        }
    }

    private boolean addUser(UserDomain userDomain) {
        UserEntity userEntity = userEntityRepository.findUserEntityByUsername(userDomain.getUsername());

        if (userEntity == null) {
            userService.addUser(
                    userDomain.getUsername(),
                    userDomain.getPassword(),
                    userDomain.getFirstName(),
                    userDomain.getLastName(),
                    RoleEnum.USER.getCode()
            );
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User successfully created!", null));
        }
        else {
            userEntity.setFirstName(userDomain.getFirstName());
            userEntity.setLastName(userDomain.getLastName());
            userEntity.setPassword(userDomain.getPassword());
            userEntityRepository.save(userEntity);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Password successfully changed!", null));
        }

        return true;
    }

}
