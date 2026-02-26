package net.happykoo.hcp.common.web.security;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CommonWebMvcConfig implements WebMvcConfigurer {

  private final CurrentActorArgumentResolver currentActorArgumentResolver;

  @Override
  public void addArgumentResolvers(
      @NonNull List<HandlerMethodArgumentResolver> resolvers
  ) {
    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    resolvers.add(currentActorArgumentResolver);
  }
}
