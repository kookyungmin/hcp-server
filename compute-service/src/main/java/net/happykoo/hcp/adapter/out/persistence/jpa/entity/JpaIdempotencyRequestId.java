package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JpaIdempotencyRequestId implements Serializable {

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Column(name = "idempotency_key", nullable = false)
  private String idempotencyKey;
}
