package net.happykoo.hcp.common.web.security;

import static net.happykoo.hcp.common.web.security.SecurityHeaderNames.X_ROLES;
import static net.happykoo.hcp.common.web.security.SecurityHeaderNames.X_USER_ID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class CommonAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    var userId = request.getHeader(X_USER_ID);

    if (StringUtils.isNotBlank(userId)) {
      var authorities = parseRoles(request.getHeader(X_ROLES));
      var authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    filterChain.doFilter(request, response);
  }

  private List<SimpleGrantedAuthority> parseRoles(String rolesHeader) {
    if (StringUtils.isBlank(rolesHeader)) {
      return List.of();
    }
    return Arrays.stream(rolesHeader.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(SimpleGrantedAuthority::new)
        .toList();
  }
}
