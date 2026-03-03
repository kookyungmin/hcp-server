package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.idempotency.IdempotencyCommandType;
import net.happykoo.hcp.domain.idempotency.IdempotencyRequest;

@Entity
@Table(name = "h_idempotency_request")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JpaIdempotencyRequestEntity extends JpaTimeBaseEntity {

  @EmbeddedId
  private JpaIdempotencyRequestId id;

  @Enumerated(value = EnumType.STRING)
  private IdempotencyCommandType commandType;

  private String requestHash;
  private String response;

  public static JpaIdempotencyRequestEntity from(IdempotencyRequest idempotencyRequest) {
    return new JpaIdempotencyRequestEntity(
        new JpaIdempotencyRequestId(idempotencyRequest.getOwnerId(),
            idempotencyRequest.getIdempotencyKey()),
        idempotencyRequest.getCommandType(),
        idempotencyRequest.getRequestHash(),
        idempotencyRequest.getResponse()
    );
  }

  public IdempotencyRequest toDomain() {
    return new IdempotencyRequest(
        id.getOwnerId(),
        id.getIdempotencyKey(),
        commandType,
        requestHash,
        response
    );
  }
}
