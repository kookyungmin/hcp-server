package net.happykoo.hcp.application.port.in;

import net.happykoo.hcp.application.port.in.command.FindInstanceSshKeyCommand;
import net.happykoo.hcp.domain.instance.InstanceSshKey;

public interface FindInstanceSshKeyUseCase {

  InstanceSshKey findInstanceSshKey(FindInstanceSshKeyCommand command);

}
