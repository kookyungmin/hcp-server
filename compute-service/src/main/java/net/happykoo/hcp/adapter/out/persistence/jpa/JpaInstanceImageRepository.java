package net.happykoo.hcp.adapter.out.persistence.jpa;

import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInstanceImageRepository extends JpaRepository<JpaInstanceImageEntity, String> {

}
