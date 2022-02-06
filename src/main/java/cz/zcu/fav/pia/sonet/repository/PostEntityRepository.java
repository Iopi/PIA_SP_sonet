package cz.zcu.fav.pia.sonet.repository;

import cz.zcu.fav.pia.sonet.entity.PostEntity;
import cz.zcu.fav.pia.sonet.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, UUID> {

    List<PostEntity> findAllByUser1Username(String username);

}
