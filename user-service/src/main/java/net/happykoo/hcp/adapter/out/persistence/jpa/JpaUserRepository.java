package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

  @Query("SELECT DISTINCT u "
      + "FROM JpaUserEntity u "
      + "LEFT OUTER JOIN FETCH JpaUserPermissionEntity p "
      + "ON u.userId = p.id.userId "
      + "WHERE u.userId = :userId ")
  Optional<JpaUserEntity> findByUserId(UUID userId);

}
