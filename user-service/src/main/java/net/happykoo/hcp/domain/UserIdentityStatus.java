package net.happykoo.hcp.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserIdentityStatus {
  ACTIVE("A"),
  INACTIVE("I");

  private final String code;
}
