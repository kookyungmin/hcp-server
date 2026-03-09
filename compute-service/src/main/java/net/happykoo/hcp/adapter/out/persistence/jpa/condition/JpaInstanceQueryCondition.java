package net.happykoo.hcp.adapter.out.persistence.jpa.condition;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class JpaInstanceQueryCondition {

  private UUID ownerId;
  private String searchKeyword;
}
