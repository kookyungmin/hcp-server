package net.happykoo.hcp.domain;

public record Permission(
    PermissionCode code,
    String description
) {

  public Permission(
      String code,
      String description
  ) {
    this(new PermissionCode(code), description);
  }
}

