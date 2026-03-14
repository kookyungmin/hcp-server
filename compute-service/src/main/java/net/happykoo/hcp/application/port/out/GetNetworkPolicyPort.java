package net.happykoo.hcp.application.port.out;

import java.util.List;
import java.util.UUID;
import net.happykoo.hcp.domain.network.NetworkPolicy;

public interface GetNetworkPolicyPort {

  List<NetworkPolicy> findAllNetworkPolicies(UUID instanceId);

}
