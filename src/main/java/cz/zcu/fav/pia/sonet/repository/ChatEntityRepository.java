package cz.zcu.fav.pia.sonet.repository;

import cz.zcu.fav.pia.sonet.entity.ChatEntity;
import cz.zcu.fav.pia.sonet.entity.FriendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatEntityRepository extends JpaRepository<ChatEntity, UUID> {

    List<ChatEntity> findAllByUser1Username(String username);

    List<ChatEntity> findAllByUser2Username(String username);

    List<ChatEntity> findAllByUser1UsernameAndUser2Username(String username1, String username2);


}
