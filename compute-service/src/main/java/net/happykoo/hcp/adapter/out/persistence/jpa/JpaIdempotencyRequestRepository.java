package net.happykoo.hcp.adapter.out.persistence.jpa;

import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaIdempotencyRequestEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaIdempotencyRequestId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIdempotencyRequestRepository extends
    JpaRepository<JpaIdempotencyRequestEntity, JpaIdempotencyRequestId> {

}
