package net.happykoo.hcp.adapter.in.web.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.web.resolver.IdempotencyKeyArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final IdempotencyKeyArgumentResolver idempotencyKeyArgumentResolver;

  @Override
  public void addArgumentResolvers(
      @NonNull List<HandlerMethodArgumentResolver> resolvers
  ) {
    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    resolvers.add(idempotencyKeyArgumentResolver);
  }
}
