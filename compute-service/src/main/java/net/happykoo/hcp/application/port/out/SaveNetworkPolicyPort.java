package net.happykoo.hcp.application.port.out;

import java.util.List;
import net.happykoo.hcp.domain.network.NetworkPolicy;

public interface SaveNetworkPolicyPort {

  void saveAllNetworkPolicy(List<NetworkPolicy> networkPolicies);

}
