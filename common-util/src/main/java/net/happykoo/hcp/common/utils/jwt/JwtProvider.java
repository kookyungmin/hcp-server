package net.happykoo.hcp.common.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;


public class JwtProvider<T> {

  private final static String CLAIM_KEY = "claims";

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

  public String generateToken(T payload, long expireSeconds) {
    Claims claims = Jwts.claims()
        .add(CLAIM_KEY, payload)
        .build();

    Date now = new Date();
    Date validateDate = new Date(now.getTime() + (expireSeconds * 1000L));
    return Jwts.builder()
        .claims(claims)
        .expiration(validateDate)
        .issuedAt(now)
        .signWith(secretKey)
        .compact();
  }

  public T parseToken(String accessToken, Class<T> payloadClass) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(accessToken)
        .getPayload()
        .get(CLAIM_KEY, payloadClass);
  }
}
