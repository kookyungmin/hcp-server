package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<JpaUserEntity, UUID> {

}
