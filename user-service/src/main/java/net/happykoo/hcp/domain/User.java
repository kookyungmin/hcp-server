package net.happykoo.hcp.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class User {

  private UUID id;
  private String displayName;
  private Set<PermissionCode> permissions; //RBAC
  private UserStatus status;

  public User(
      UUID id,
      String displayName,
      UserStatus status
  ) {
    this.id = id;
    this.displayName = displayName;
    this.permissions = new HashSet<>();
    this.status = status;
  }

  public void addPermission(String permission) {
    permissions.add(new PermissionCode(permission));
  }

  public void removePermission(String permission) {
    var permissionCode = new PermissionCode(permission);
    if (!permissions.contains(permissionCode)) {
      throw new IllegalStateException("User does not have the permission.");
    }

    permissions.remove(permissionCode);
  }
}
