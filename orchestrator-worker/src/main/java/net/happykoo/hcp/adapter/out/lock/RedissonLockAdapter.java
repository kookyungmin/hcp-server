package net.happykoo.hcp.adapter.out.lock;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.TryLockPort;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedissonLockAdapter implements TryLockPort {

  private final RedissonClient redissonClient;

  @Override
  public void tryLockInstance(
      UUID instanceId,
      Runnable runnable
  ) {
    var lock = redissonClient.getLock("lock:instance:" + instanceId);
    try {
      var available = lock.tryLock(5, 10, TimeUnit.SECONDS);
      if (!available) {
        throw new IllegalStateException("현재 인스턴스는 작업 중입니다.");
      }
      runnable.run();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }
}
