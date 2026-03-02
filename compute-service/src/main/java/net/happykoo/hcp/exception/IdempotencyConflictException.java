package net.happykoo.hcp.exception;

public class IdempotencyConflictException extends RuntimeException {

  public IdempotencyConflictException() {
    super("Idempotency Conflict");
  }

}
