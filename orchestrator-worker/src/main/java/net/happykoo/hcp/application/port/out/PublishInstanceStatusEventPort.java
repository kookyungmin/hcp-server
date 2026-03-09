package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.application.port.out.data.InstanceStatusData;

public interface PublishInstanceStatusEventPort {

  void publishInstanceStatusEvent(InstanceStatusData instanceStatusData);
}
