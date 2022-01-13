package cz.zcu.fav.pia.sonet.repository;

import cz.zcu.fav.pia.sonet.entity.RoleEntity;
import cz.zcu.fav.pia.sonet.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID> {

    RoleEntity findRoleEntityByCode(String code);

}
