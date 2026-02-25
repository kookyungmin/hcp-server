package net.happykoo.hcp.adapter.out;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaPermissionRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaPermissionEntity;
import net.happykoo.hcp.application.port.out.GetPermissionPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.PermissionCode;

@PersistenceAdapter
@RequiredArgsConstructor
public class PermissionPersistenceAdapter implements GetPermissionPort {

  private final JpaPermissionRepository jpaPermissionRepository;

  @Override
  public List<PermissionCode> getAllPermissions() {
    return jpaPermissionRepository.findByIsActiveIsTrue()
        .stream()
        .map(JpaPermissionEntity::toDomain)
        .toList();
  }
}
