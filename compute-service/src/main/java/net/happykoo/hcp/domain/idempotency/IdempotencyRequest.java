package net.happykoo.hcp.domain.idempotency;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IdempotencyRequest {

  private UUID ownerId;
  private String idempotencyKey;
  private IdempotencyCommandType commandType;
  private String requestHash; //같은 키로 다른 body 데이터가 오면 409 에러
  private String response;
}
