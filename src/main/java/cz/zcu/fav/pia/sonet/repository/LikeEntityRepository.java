package cz.zcu.fav.pia.sonet.repository;

import cz.zcu.fav.pia.sonet.entity.BlockEntity;
import cz.zcu.fav.pia.sonet.entity.ChatEntity;
import cz.zcu.fav.pia.sonet.entity.LikeEntity;
import cz.zcu.fav.pia.sonet.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, UUID> {

    List<LikeEntity> findAllByPostId(UUID uuid);

    List<LikeEntity> findAllByUser1UsernameAndPostId(String username1, UUID uuid);


}
