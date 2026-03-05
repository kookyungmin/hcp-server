package net.happykoo.hcp.domain.idempotency;

public enum IdempotencyStatus {
  PROCESSING,
  SUCCESS,
  FAILED;
}
