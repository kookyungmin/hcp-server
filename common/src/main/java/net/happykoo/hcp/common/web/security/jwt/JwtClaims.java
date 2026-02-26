package net.happykoo.hcp.common.web.security.jwt;

import java.util.List;

public record JwtClaims(
    String userId,
    List<String> scopes
) {

}
