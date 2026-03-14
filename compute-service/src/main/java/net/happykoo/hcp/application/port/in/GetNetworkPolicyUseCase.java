package net.happykoo.hcp.application.port.in;

import java.util.List;
import net.happykoo.hcp.application.port.in.command.GetNetworkPolicyCommand;
import net.happykoo.hcp.domain.network.NetworkPolicy;

public interface GetNetworkPolicyUseCase {

  List<NetworkPolicy> getNetworkPolicy(GetNetworkPolicyCommand command);
}
