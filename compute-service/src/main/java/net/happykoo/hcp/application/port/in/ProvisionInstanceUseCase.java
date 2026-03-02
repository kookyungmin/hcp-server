package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.ProvisionInstanceCommand;

public interface ProvisionInstanceUseCase {

  void provisionInstance(
      ProvisionInstanceCommand command
  );
}
