package net.happykoo.hcp.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserIdentityType {
  PASSWORD("P"),
  OAUTH2("O");
  private final String code;
}
