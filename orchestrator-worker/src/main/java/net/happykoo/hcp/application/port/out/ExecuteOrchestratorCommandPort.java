package net.happykoo.hcp.application.port.out;

import java.util.UUID;
import net.happykoo.hcp.application.port.out.data.InstanceStatusData;
import net.happykoo.hcp.domain.instance.Instance;

public interface ExecuteOrchestratorCommandPort {

  void executeProvisionInstanceCommand(Instance instance);

  InstanceStatusData executeGetInstanceStatusCommand(UUID instanceId);
}
