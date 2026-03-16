package net.happykoo.hcp.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.out.persistence.jpa.JpaNetworkPolicyRepository;
import net.happykoo.hcp.adapter.out.persistence.jpa.entity.JpaNetworkPolicyEntity;
import net.happykoo.hcp.application.port.out.GetNetworkPolicyPort;
import net.happykoo.hcp.application.port.out.SaveNetworkPolicyPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.network.NetworkPolicy;

@PersistenceAdapter
@RequiredArgsConstructor
public class NetworkPolicyPersistenceAdapter implements
    SaveNetworkPolicyPort, GetNetworkPolicyPort {

  private final JpaNetworkPolicyRepository jpaNetworkPolicyRepository;

  @Override
  public List<NetworkPolicy> findAllNetworkPolicies(UUID instanceId) {
    return jpaNetworkPolicyRepository.findAllByInstanceId(instanceId)
        .stream()
        .map(JpaNetworkPolicyEntity::toDomain)
        .toList();
  }

  @Override
  public void saveAllNetworkPolicy(List<NetworkPolicy> networkPolicies) {
    var entities = networkPolicies.stream()
        .map(JpaNetworkPolicyEntity::from)
        .toList();
    jpaNetworkPolicyRepository.saveAll(entities);
  }

  @Override
  public void removeAllNetworkPolicyByInstanceId(UUID instanceId) {
    jpaNetworkPolicyRepository.deleteAllByInstanceId(instanceId);
  }
}
