package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.instance.Instance;

public interface ExecuteOrchestratorCommandPort {

  void executeProvisionInstanceCommand(Instance instance);
}
