package net.happykoo.hcp.adapter.in.websocket.config;

import static net.happykoo.hcp.adapter.in.web.auth.PermissionCode.INSTANCE_EXECUTE;

import net.happykoo.hcp.common.web.security.CommonAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSocketSecurityConfig {

  @Bean
  @Order(10)
  SecurityFilterChain wsSecurityFilterChain(
      HttpSecurity http,
      CommonAuthenticationFilter commonAuthenticationFilter,
      AccessDeniedHandler accessDeniedHandler,
      AuthenticationEntryPoint authenticationEntryPoint
  ) throws Exception {
    http
        .securityMatcher("/ws/**")
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .addFilterBefore(commonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/ws/instances/**").hasAuthority(INSTANCE_EXECUTE)
                .anyRequest()
                .denyAll()
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
}
