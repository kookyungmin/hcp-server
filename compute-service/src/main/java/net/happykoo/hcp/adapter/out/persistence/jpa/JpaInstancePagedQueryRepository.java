package net.happykoo.hcp.adapter.out.persistence.jpa;

import net.happykoo.hcp.adapter.out.persistence.jpa.condition.JpaInstanceQueryCondition;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaInstancePagedQueryRepository {

  Page<JpaInstanceEntity> findPagedInstance(JpaInstanceQueryCondition condition, Pageable pageable);

}
