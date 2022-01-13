package cz.zcu.fav.pia.sonet.repository;

import cz.zcu.fav.pia.sonet.entity.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlockEntityRepository extends JpaRepository<BlockEntity, UUID> {

    List<BlockEntity> findAllByUser1Username(String username);

    List<BlockEntity> findAllByUser2Username(String username);

    List<BlockEntity> findAllByUser1UsernameAndUser2Username(String username1, String username2);

}
