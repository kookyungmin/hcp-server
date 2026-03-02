package net.happykoo.hcp.adapter.out.persistence.jpa;

import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaNetworkVpcEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNetworkVpcRepository extends JpaRepository<JpaNetworkVpcEntity, String> {

}
