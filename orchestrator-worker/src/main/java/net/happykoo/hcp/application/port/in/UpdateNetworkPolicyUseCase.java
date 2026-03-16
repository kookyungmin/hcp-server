package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.UpdateNetworkPolicyCommand;

public interface UpdateNetworkPolicyUseCase {

  void updateNetworkPolicy(UpdateNetworkPolicyCommand command);

}
