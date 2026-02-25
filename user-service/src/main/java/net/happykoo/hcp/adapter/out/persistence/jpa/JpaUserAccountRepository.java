package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.Optional;
import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaUserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserAccountRepository extends JpaRepository<JpaUserAccountEntity, UUID> {

  Optional<JpaUserAccountEntity> findByEmail(String email);

}
