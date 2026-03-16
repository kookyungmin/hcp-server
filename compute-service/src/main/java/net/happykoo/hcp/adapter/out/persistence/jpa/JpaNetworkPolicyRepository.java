package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.List;
import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaNetworkPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNetworkPolicyRepository extends JpaRepository<JpaNetworkPolicyEntity, Long> {

  List<JpaNetworkPolicyEntity> findAllByInstanceId(UUID instanceId);

  void deleteAllByInstanceId(UUID instanceId);

}
