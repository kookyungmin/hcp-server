package net.happykoo.hcp.adapter.in.web.resolver;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class IdempotencyKeyArgumentResolver implements HandlerMethodArgumentResolver {

  private final static String HEADER_NAME = "X-Idempotency-Key";

  @Override
  public boolean supportsParameter(
      @NonNull MethodParameter parameter
  ) {
    return parameter.hasParameterAnnotation(IdempotencyKey.class)
        && parameter.getParameterType().equals(String.class);
  }

  @Nullable
  @Override
  public Object resolveArgument(
      @NonNull MethodParameter parameter,
      @Nullable ModelAndViewContainer mavContainer,
      @NonNull NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory
  ) {
    String idempotencyKey = webRequest.getHeader(HEADER_NAME);

    if (StringUtils.isBlank(idempotencyKey)) {
      throw new IllegalArgumentException("Idempotency-Key 헤더는 필수입니다.");
    }

    return idempotencyKey;
  }
}
