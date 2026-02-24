package net.happykoo.hcp.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;

@Getter
public class User {

  private String id;
  private String displayName;
  private Set<UserRole> roles;
  private UserStatus status;

  public User(
      String id,
      String displayName
  ) {
    this.id = id;
    this.displayName = displayName;
    this.roles = new HashSet<>(List.of(UserRole.USER));
    this.status = UserStatus.ACTIVE;
  }

  public void addRole(UserRole role) {
    roles.add(role);
  }

  public void removeRole(UserRole role) {
    if (!roles.contains(role)) {
      throw new IllegalStateException("User does not have the role.");
    }

    if (roles.size() == 1) {
      throw new IllegalStateException("User must have at least one role.");
    }

    roles.remove(role);
  }
}
