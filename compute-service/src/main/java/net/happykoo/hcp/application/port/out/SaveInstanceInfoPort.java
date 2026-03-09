package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.application.port.out.data.UpdateInstanceStatusData;
import net.happykoo.hcp.domain.instance.ServerInstance;

public interface SaveInstanceInfoPort {

  ServerInstance saveInstanceInfo(ServerInstance instanceInfo);

  void updateInstanceStatus(UpdateInstanceStatusData updateInstanceStatusData);
}
