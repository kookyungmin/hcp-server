package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.List;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPermissionRepository extends JpaRepository<JpaPermissionEntity, String> {

  List<JpaPermissionEntity> findByIsActiveIsTrue();

}
