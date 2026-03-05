package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.idempotency.Idempotency;
import net.happykoo.hcp.domain.idempotency.IdempotencyStatus;

@Entity
@Table(name = "h_idempotency")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JpaIdempotencyEntity extends JpaTimeBaseEntity {

  @Id
  @Column(name = "idempotency_key")
  private UUID idempotencyKey;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "status")
  private IdempotencyStatus status;

  @Column(name = "expired_at")
  private Instant expiredAt;

  public static JpaIdempotencyEntity from(Idempotency idempotency) {
    return new JpaIdempotencyEntity(
        idempotency.getIdempotencyKey(),
        idempotency.getStatus(),
        idempotency.getExpiredAt()
    );
  }
}
