package net.happykoo.hcp.adapter.out.orchestrator;

import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.ExecuteOrchestratorCommandPort;
import net.happykoo.hcp.common.annotation.OrchestratorAdapter;
import net.happykoo.hcp.domain.instance.Instance;

@OrchestratorAdapter
@RequiredArgsConstructor
public class K8sOrchestrator implements ExecuteOrchestratorCommandPort {

  @Override
  public void executeProvisionInstanceCommand(Instance instance) {

  }
}
