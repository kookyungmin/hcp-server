package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInstanceRepository extends JpaRepository<JpaInstanceEntity, UUID> {

  @EntityGraph(attributePaths = {"tags", "image", "spec", "vpc"})
  Optional<JpaInstanceEntity> findWithAllByInstanceId(UUID instanceId);

  boolean existsByInstanceIdAndOwnerId(UUID instanceId, UUID ownerId);
}
