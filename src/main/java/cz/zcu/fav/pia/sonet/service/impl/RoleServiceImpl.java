package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.domain.RoleEnum;
import cz.zcu.fav.pia.sonet.entity.RoleEntity;
import cz.zcu.fav.pia.sonet.repository.RoleEntityRepository;
import cz.zcu.fav.pia.sonet.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.EnumSet;


@Service("roleService")
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleEntityRepository roleEntityRepository;

    @PostConstruct
    private void setup() {

        if (roleEntityRepository.count() == 0) {
            EnumSet.allOf(RoleEnum.class).forEach(this::addRole);
        }
        else {
            if (this.roleEntityRepository.findRoleEntityByCode(RoleEnum.ADMIN.getCode()) == null) {
                addRole(RoleEnum.ADMIN);
            }

            if (this.roleEntityRepository.findRoleEntityByCode(RoleEnum.USER.getCode()) == null) {
                addRole(RoleEnum.USER);
            }
        }

    }

    public void addRole(RoleEnum roleEnum) {
        RoleEntity roleEntity = new RoleEntity(roleEnum.getCode(), roleEnum.getName());
        this.roleEntityRepository.save(roleEntity);
    }

}
