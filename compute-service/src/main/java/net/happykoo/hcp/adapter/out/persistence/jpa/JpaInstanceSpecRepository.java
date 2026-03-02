package net.happykoo.hcp.adapter.out.persistence.jpa;

import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceSpecEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaInstanceSpecRepository extends JpaRepository<JpaInstanceSpecEntity, String> {

}
