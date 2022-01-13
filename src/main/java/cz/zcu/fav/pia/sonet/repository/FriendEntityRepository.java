package cz.zcu.fav.pia.sonet.repository;

import cz.zcu.fav.pia.sonet.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendEntityRepository extends JpaRepository<FriendEntity, UUID> {

    List<FriendEntity> findAllByUser1Username(String username);

    List<FriendEntity> findAllByUser1UsernameAndUser2Username(String username1, String username2);
}
