package net.happykoo.hcp.common.web.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import net.happykoo.hcp.common.web.security.Actor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class JwtProvider {

  private final JwtProperties jwtProperties;
  private final SecretKey secretKey;

  public JwtProvider(
      JwtProperties jwtProperties
  ) {
    this.jwtProperties = jwtProperties;
    this.secretKey = Keys.hmacShaKeyFor(
        jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
  }

  public String createAccessToken(Actor actor) {
    Claims claims = Jwts.claims()
        .add("actor", actor)
        .build();

    Date now = new Date();
    Date validateDate = new Date(now.getTime() + (jwtProperties.getExpireTime() * 1000L));
    return Jwts.builder()
        .claims(claims)
        .expiration(validateDate)
        .issuedAt(now)
        .signWith(secretKey)
        .compact();
  }

  public Actor parseAccessToken(String accessToken) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(accessToken)
        .getPayload()
        .get("actor", Actor.class);
  }
}
