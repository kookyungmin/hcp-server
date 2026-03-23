package net.happykoo.hcp.application.port.out;

import java.util.UUID;

public interface TryLockPort {

  void tryLockInstance(UUID instanceId, Runnable runnable);
}
