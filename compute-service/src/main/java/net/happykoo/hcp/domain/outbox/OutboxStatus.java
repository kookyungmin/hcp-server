package net.happykoo.hcp.domain.outbox;

public enum OutboxStatus {
  PENDING,
  PROCESSING,
  SUCCESS,
  FAILED;
}
