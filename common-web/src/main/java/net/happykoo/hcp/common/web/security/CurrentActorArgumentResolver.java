package net.happykoo.hcp.common.web.security;

import net.happykoo.hcp.common.web.annotation.CurrentActor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentActorArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(
      @NonNull MethodParameter parameter
  ) {
    return parameter.hasParameterAnnotation(CurrentActor.class)
        && parameter.getParameterType().isAssignableFrom(Actor.class);
  }

  @Nullable
  @Override
  public Object resolveArgument(
      @NonNull MethodParameter parameter,
      @Nullable ModelAndViewContainer mavContainer,
      @NonNull NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory
  ) {
    var authenticationToken = (UsernamePasswordAuthenticationToken) (SecurityContextHolder
        .getContext()
        .getAuthentication());

    var userId = (String) authenticationToken.getPrincipal();
    var scopes = authenticationToken.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    return new Actor(
        userId,
        scopes
    );
  }
}
