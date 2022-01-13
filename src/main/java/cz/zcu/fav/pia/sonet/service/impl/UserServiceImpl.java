package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.configuration.Constants;
import cz.zcu.fav.pia.sonet.domain.RoleEnum;
import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.domain.UserInfoDomain;
import cz.zcu.fav.pia.sonet.entity.RoleEntity;
import cz.zcu.fav.pia.sonet.entity.UserEntity;
import cz.zcu.fav.pia.sonet.repository.RoleEntityRepository;
import cz.zcu.fav.pia.sonet.repository.UserEntityRepository;
import cz.zcu.fav.pia.sonet.service.UserService;
import cz.zcu.fav.pia.sonet.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;


@Service("userService")
@RequiredArgsConstructor
@Slf4j
@DependsOn("roleService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final RoleEntityRepository roleEntityRepository;

    private final PasswordEncoder encoder;

    @PostConstruct
    private void setup() {
        if (!userEntityRepository.existsUserEntitiesByRolesEquals(roleEntityRepository.findRoleEntityByCode(RoleEnum.ADMIN.getCode()))) {
            log.info("No admin present, creating init users...");

            addUser(
                    Constants.INIT_ADMIN_USERNAME,
                    encoder.encode(Constants.INIT_ADMIN_PASSWORD),
                    "AdminFirstName",
                    "AdminLastName",
                    Constants.INIT_ROLE_ADMIN.getCode(),
                    Constants.INIT_ROLE_USER.getCode()
            );

            addUser(
                    Constants.INIT_USER_USERNAME,
                    encoder.encode(Constants.INIT_USER_PASSWORD),
                    "UserFirstName",
                    "UserLastName",
                    Constants.INIT_ROLE_USER.getCode()
            );

            log.info("Admin created with username '" + Constants.INIT_ADMIN_USERNAME + "' and password '" + Constants.INIT_ADMIN_PASSWORD + "'");

        }
    }

    @Override
    public boolean addUser(String username, String password, String firstName,
                           String lastName, String... roles) {

        if (userEntityRepository.findUserEntityByUsername(username) != null) {
            log.error("User already exists!");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "User already exists!", null));

            return false;
        }

        UserEntity userEntity = new UserEntity(
                username,
                password,
                firstName,
                lastName);

        for (String currentRole : roles) {
            RoleEntity currentRoleEntity = roleEntityRepository.findRoleEntityByCode(currentRole);

            if (currentRoleEntity != null) {
                userEntity.getRoles().add(currentRoleEntity);
                log.info("Role " + currentRole + " has been set to new user " + username);
            }
            else {
                log.error("Role " + currentRole + "does not exist!");
            }
        }

        try {
            userEntityRepository.save(userEntity);
        } catch (RuntimeException e) {
            log.error("Wrong format of email address.");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong format of email address.", null));

            return false;
        }

        log.info("User successfully created.");

        return true;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        UserEntity userEntity = userEntityRepository.findUserEntityByUsername(username);

        if (userEntity == null || userEntity.isDeleted()) {
            log.info("User " + username + " not found or has been deleted.");
            throw new UsernameNotFoundException("Invalid user!");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (RoleEntity currentRole : userEntity.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(Utils.toSpringRole(currentRole)));
        }

        return new UserDomain(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                authorities);
    }

    @Override
    public UserInfoDomain updateUser(String username, UserInfoDomain userInfoDomain) {
        UserEntity userEntity = userEntityRepository.findUserEntityByUsername(username);

        userEntity.setFirstName(userInfoDomain.getFirstName());
        userEntity.setLastName(userInfoDomain.getLastName());

        userEntityRepository.save(userEntity);

        userEntity = userEntityRepository.findUserEntityByUsername(username);

        return new UserInfoDomain(userEntity.getFirstName(), userEntity.getLastName());
    }

}
