package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceSshKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInstanceSshKeyRepository extends
    JpaRepository<JpaInstanceSshKeyEntity, UUID> {

}
