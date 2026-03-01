package net.happykoo.hcp.domain;

import java.util.UUID;

public class IdempotencyRequests {

  private UUID ownerId;
  private UUID idempotencyKey;
  private String commandType;
  private String requestHash; //같은 키로 다른 body 데이터가 오면 409 에러
  private String response;
}
