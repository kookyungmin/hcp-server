package net.happykoo.hcp.application.port.out;

import java.util.Optional;
import java.util.UUID;
import net.happykoo.hcp.domain.idempotency.IdempotencyRequest;

public interface GetIdempotencyRequestPort {

  Optional<IdempotencyRequest> getRequestByKey(UUID ownerId, String idempotencyKey);

}
