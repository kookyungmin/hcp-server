package net.happykoo.hcp.domain.instance;

public enum DefaultNetworkPolicy {
  ALLOW_ALL,
  ALLOW_SAME_VPC,
  DENY_ALL;

  public static DefaultNetworkPolicy fromString(String value) {
    return valueOf(value.toUpperCase());
  }
}
