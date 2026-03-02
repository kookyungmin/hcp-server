package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.instance.ServerInstance;

public interface SaveInstanceInfoPort {

  void saveInstanceInfo(ServerInstance instanceInfo);
}
