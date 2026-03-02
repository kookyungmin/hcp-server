package net.happykoo.hcp.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User {

  private UUID id;
  private String displayName;
  private Set<PermissionCode> permissions; //RBAC
  private UUID masterUserId;
  private boolean isMaster;
  private UserStatus status;

  public User(
      UUID id,
      String displayName,
      UserStatus status,
      UUID masterUserId,
      boolean isMaster
  ) {
    this.id = id;
    this.displayName = displayName;
    this.permissions = new HashSet<>();
    this.status = status;
    this.masterUserId = masterUserId;
    this.isMaster = isMaster;
  }

  public static User createMasterUser(
      UUID id,
      String displayName,
      UserStatus status
  ) {
    return new User(id, displayName, status, null, true);
  }

  public void addPermissions(List<PermissionCode> permissions) {
    this.permissions.addAll(permissions);
  }

  public void addPermission(String permission) {
    permissions.add(new PermissionCode(permission));
  }

  public void removePermission(String permission) {
    var permissionCode = new PermissionCode(permission);
    if (!permissions.contains(permissionCode)) {
      throw new IllegalStateException("사용자에게 해당 권한이 없습니다.");
    }

    permissions.remove(permissionCode);
  }

  public boolean isActive() {
    return status == UserStatus.ACTIVE;
  }
}
