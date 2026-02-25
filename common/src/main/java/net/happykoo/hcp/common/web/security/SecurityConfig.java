package net.happykoo.hcp.common.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

  @Bean
  @ConditionalOnMissingBean(SecurityFilterChain.class)
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      SecurityProperties securityProperties,
      AccessDeniedHandler accessDeniedHandler,
      AuthenticationEntryPoint authenticationEntryPoint
  ) throws Exception {
    //TODO: X-USER-ID, X-ROLES 검증 필터
    http.authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers(Optional.ofNullable(securityProperties.getAllowedApiPaths())
                    .map(list -> list.toArray(String[]::new))
                    .orElse(new String[0]))
                .permitAll()
                .anyRequest()
                .authenticated()
        )
        .formLogin(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors ->
            cors.configurationSource(configurationSource(securityProperties.getAllowedOrigins()))
        )
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .exceptionHandling(ex ->
            ex.accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
        );

    return http.build();
  }

  @Bean
  @ConditionalOnMissingBean(AccessDeniedHandler.class)
  AccessDeniedHandler accessDeniedHandler(
      ObjectMapper objectMapper
  ) {
    //인증 O 인가 X (403 FOR_BIDDEN)
    return (request, response, accessDeniedException) -> {
      writeResponse(request, response, objectMapper, HttpStatus.FORBIDDEN,
          "Access Denied.");
    };
  }

  @Bean
  @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
  AuthenticationEntryPoint authenticationEntryPoint(
      ObjectMapper objectMapper
  ) {
    //인증 X API 접근 (401 UnAuthorized)
    return (request, response, authException) -> {
      writeResponse(request, response, objectMapper, HttpStatus.UNAUTHORIZED,
          "Unauthorized.");
    };
  }

  private CorsConfigurationSource configurationSource(List<String> allowedOrigins) {
    return request -> {
      CorsConfiguration corsConfiguration = new CorsConfiguration();

      corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
      corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
      corsConfiguration.setAllowCredentials(true);
      corsConfiguration.setAllowedOrigins(allowedOrigins);

      return corsConfiguration;
    };
  }

  private void writeResponse(
      HttpServletRequest request,
      HttpServletResponse response,
      ObjectMapper objectMapper,
      HttpStatus status,
      String message
  ) throws IOException {
    var res = CommonResponseEntity.error(message, request.getRequestURI(), status);

    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(status.value());
    objectMapper.writeValue(response.getWriter(), res);
  }
}
