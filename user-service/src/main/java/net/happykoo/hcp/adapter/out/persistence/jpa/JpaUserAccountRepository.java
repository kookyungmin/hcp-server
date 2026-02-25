package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaUserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserAccountRepository extends JpaRepository<JpaUserAccountEntity, UUID> {

}
