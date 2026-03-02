package net.happykoo.hcp.application.port.out;

import net.happykoo.hcp.domain.idempotency.IdempotencyRequest;

public interface SaveIdempotencyRequestPort {

  void saveIdempotencyRequest(IdempotencyRequest idempotencyRequest);
}
