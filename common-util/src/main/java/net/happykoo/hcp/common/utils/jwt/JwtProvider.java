package net.happykoo.hcp.common.utils.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;


public class JwtProvider {

  private final JwtProperties jwtProperties;
  private final SecretKey secretKey;

  public JwtProvider(
      JwtProperties jwtProperties
  ) {
    if (jwtProperties == null
        || jwtProperties.secretKey() == null) {
      throw new IllegalStateException(
          "Secret key is not set.");
    }
    this.jwtProperties = jwtProperties;
    this.secretKey = Keys.hmacShaKeyFor(
        jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(List<JwtClaim> claims, long expireSeconds) {
    var claimsBuilder = Jwts.claims();

    claims.forEach(claim -> claimsBuilder.add(claim.key(), claim.value()));

    var now = new Date();
    var validateDate = new Date(now.getTime() + (expireSeconds * 1000L));
    return Jwts.builder()
        .claims(claimsBuilder.build())
        .expiration(validateDate)
        .issuedAt(now)
        .signWith(secretKey)
        .compact();
  }

  public String parseToken(String accessToken, String claimKey) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(accessToken)
        .getPayload()
        .get(claimKey, String.class);
  }
}
