package net.happykoo.hcp.adapter.out.persistence;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaInstanceSshKeyRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceSshKeyEntity;
import net.happykoo.hcp.application.port.out.GetInstanceSshKeyPort;
import net.happykoo.hcp.application.port.out.SaveInstanceSshKeyPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.common.web.exception.ResourceNotFoundException;
import net.happykoo.hcp.domain.instance.InstanceSshKey;

@PersistenceAdapter
@RequiredArgsConstructor
public class InstanceSshKeyPersistenceAdapter implements GetInstanceSshKeyPort,
    SaveInstanceSshKeyPort {

  private final JpaInstanceSshKeyRepository jpaInstanceSshKeyRepository;

  @Override
  public InstanceSshKey findInstanceSshKey(UUID instanceId) {
    return jpaInstanceSshKeyRepository.findById(instanceId)
        .map(JpaInstanceSshKeyEntity::toDomain)
        .orElseThrow(() -> new ResourceNotFoundException("SSH Key가 존재하지 않습니다."));
  }

  @Override
  public void removeInstanceSshKey(UUID instanceId) {
    jpaInstanceSshKeyRepository.deleteById(instanceId);
  }

  @Override
  public void saveInstanceSshKey(InstanceSshKey instanceSshKey) {
    var entity = JpaInstanceSshKeyEntity.from(instanceSshKey);
    jpaInstanceSshKeyRepository.save(entity);
  }
}
