package net.happykoo.hcp.adapter.out.persistence.jpa;

import java.util.UUID;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaOutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOutboxEventRepository extends JpaRepository<JpaOutboxEventEntity, UUID> {

}
