package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.PermissionCode;

@Entity
@Table(name = "h_permission")
@Getter
@Setter
@NoArgsConstructor
public class JpaPermissionEntity {

  @Id
  @Column(name = "permission_code", nullable = false)
  private String permissionCode;

  @Column(name = "description")
  private String description;

  @Column(name = "is_active")
  private boolean isActive;

  @Column(name = "is_default")
  private boolean isDefault;

  public PermissionCode toDomain() {
    return new PermissionCode(permissionCode);
  }
}
