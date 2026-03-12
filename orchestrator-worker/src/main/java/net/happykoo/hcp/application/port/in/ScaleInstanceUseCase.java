package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.ScaleInstanceCommand;

public interface ScaleInstanceUseCase {

  void scaleInstance(ScaleInstanceCommand command);
}
