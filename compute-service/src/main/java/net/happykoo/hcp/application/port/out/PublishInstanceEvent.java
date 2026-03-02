package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.instance.ServerInstance;

public interface PublishInstanceEvent {

  void publishProvisionInstanceEvent(ServerInstance instanceInfo);

}
