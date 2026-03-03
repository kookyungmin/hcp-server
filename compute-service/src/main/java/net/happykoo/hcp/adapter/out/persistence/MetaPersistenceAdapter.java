package net.happykoo.hcp.adapter.out.persistence;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaInstanceImageRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaInstanceSpecRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaNetworkVpcRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceImageEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaInstanceSpecEntity;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaNetworkVpcEntity;
import net.happykoo.hcp.application.port.out.GetInstanceImagePort;
import net.happykoo.hcp.application.port.out.GetInstanceSpecPort;
import net.happykoo.hcp.application.port.out.GetNetworkVpcPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.instance.InstanceImage;
import net.happykoo.hcp.domain.instance.InstanceSpec;
import net.happykoo.hcp.domain.network.NetworkVpc;

@PersistenceAdapter
@RequiredArgsConstructor
public class MetaPersistenceAdapter implements GetInstanceImagePort,
    GetInstanceSpecPort, GetNetworkVpcPort {

  private final JpaInstanceImageRepository jpaInstanceImageRepository;
  private final JpaInstanceSpecRepository jpaInstanceSpecRepository;
  private final JpaNetworkVpcRepository jpaNetworkVpcRepository;

  @Override
  public List<InstanceImage> findAllInstanceImage() {
    return jpaInstanceImageRepository.findAll()
        .stream()
        .map(JpaInstanceImageEntity::toDomain)
        .toList();
  }

  @Override
  public List<InstanceSpec> findAllInstanceSpec() {
    return jpaInstanceSpecRepository.findAll()
        .stream()
        .map(JpaInstanceSpecEntity::toDomain)
        .toList();
  }

  @Override
  public List<NetworkVpc> findAllNetworkVpc() {
    return jpaNetworkVpcRepository.findAll()
        .stream()
        .map(JpaNetworkVpcEntity::toDomain)
        .toList();
  }
}
