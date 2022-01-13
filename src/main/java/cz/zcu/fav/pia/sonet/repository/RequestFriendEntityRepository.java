package cz.zcu.fav.pia.sonet.repository;

import cz.zcu.fav.pia.sonet.entity.RequestFriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestFriendEntityRepository extends JpaRepository<RequestFriendEntity, UUID> {

    List<RequestFriendEntity> findAllByUser1Username(String username);

    List<RequestFriendEntity> findAllByUser2Username(String username);

    List<RequestFriendEntity> findAllByUser1UsernameAndUser2Username(String username1, String username2);
}
