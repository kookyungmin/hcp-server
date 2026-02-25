package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.common.persistence.jpa.entity.TimeBaseEntity;

@Entity
@Table(name = "h_user_permission")
@NoArgsConstructor
@Getter
@Setter
public class JpaUserPermissionEntity extends TimeBaseEntity {

  @EmbeddedId
  private JpaUserPermissionId id;

  public JpaUserPermissionEntity(
      UUID userId,
      String permissionCode
  ) {
    this.id = new JpaUserPermissionId(userId, permissionCode);
  }

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private JpaUserEntity user;

  void setUser(JpaUserEntity user) {
    this.user = user;
  }

}
