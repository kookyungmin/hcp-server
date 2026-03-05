package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.application.port.out.data.IdempotencyAcquireResult;
import net.happykoo.hcp.domain.idempotency.Idempotency;

public interface SaveIdempotencyPort {

  IdempotencyAcquireResult tryAcquireIdempotency(Idempotency idempotency);

  void saveIdempotency(Idempotency idempotency);
}
