package net.happykoo.hcp.adapter.out.persistence;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.GetNetworkPolicyPort;
import net.happykoo.hcp.application.port.out.SaveNetworkPolicyPort;
import net.happykoo.hcp.common.annotation.PersistenceAdapter;
import net.happykoo.hcp.domain.network.NetworkPolicy;

@PersistenceAdapter
@RequiredArgsConstructor
public class NetworkPolicyPersistenceAdapter implements
    SaveNetworkPolicyPort, GetNetworkPolicyPort {

  @Override
  public List<NetworkPolicy> findAllNetworkPolicies(UUID instanceId) {
    return List.of();
  }

  @Override
  public void saveAllNetworkPolicy(List<NetworkPolicy> networkPolicies) {

  }
}
