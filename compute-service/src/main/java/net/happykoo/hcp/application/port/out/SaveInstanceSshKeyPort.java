package net.happykoo.hcp.application.port.out;

import java.util.UUID;
import net.happykoo.hcp.domain.instance.InstanceSshKey;

public interface SaveInstanceSshKeyPort {

  void removeInstanceSshKey(UUID instanceId);

  void saveInstanceSshKey(InstanceSshKey instanceSshKey);

}
