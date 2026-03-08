package net.happykoo.hcp.application.port.in;

import java.util.UUID;

public interface WatchInstanceStatusUseCase {

  void watchStatusAndSendEvent(UUID instanceId);
}
