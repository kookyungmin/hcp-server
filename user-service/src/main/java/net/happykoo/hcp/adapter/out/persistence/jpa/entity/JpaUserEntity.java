package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.common.persistence.jpa.entity.TimeBaseEntity;
import net.happykoo.hcp.domain.User;
import net.happykoo.hcp.domain.UserStatus;

@Entity
@Table(name = "h_user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JpaUserEntity extends TimeBaseEntity {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private UserStatus status;

  @Column(name = "master_user_id")
  private UUID masterUserId;

  @Column(name = "is_master", nullable = false)
  private boolean isMaster;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JpaUserPermissionEntity> permissions = new ArrayList<>();

  public static JpaUserEntity from(User user) {
    var entity = new JpaUserEntity(
        user.getId(),
        user.getDisplayName(),
        user.getStatus(),
        user.getMasterUserId(),
        user.isMaster(),
        null,
        new ArrayList<>()
    );
    user.getPermissions()
        .stream()
        .map(p -> new JpaUserPermissionEntity(user.getId(), p.value()))
        .forEach(entity::addPermission);
    return entity;
  }

  public void addPermission(JpaUserPermissionEntity permission) {
    permissions.add(permission);
    permission.setUser(this);
  }

  public void removePermission(JpaUserPermissionEntity permission) {
    permissions.remove(permission);
    permission.setUser(null);
  }
}
