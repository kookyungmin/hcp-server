package net.happykoo.hcp.common.web;

import java.time.Instant;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ResponseWrapper implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(
      @NonNull MethodParameter returnType,
      @NonNull Class<? extends HttpMessageConverter<?>> converterType
  ) {
    return true;
  }

  @Nullable
  @Override
  public Object beforeBodyWrite(
      @Nullable Object body,
      @NonNull MethodParameter returnType,
      @NonNull MediaType selectedContentType,
      @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
      @NonNull ServerHttpRequest request,
      @NonNull ServerHttpResponse response
  ) {
    if (body instanceof ErrorResponse) {
      return body;
    }

    int statusCode = HttpStatus.OK.value();

    if (response instanceof ServletServerHttpResponse) {
      statusCode = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
    }

    return new SuccessResponse<>(
        Instant.now(),
        statusCode,
        body,
        request.getURI().getPath()
    );
  }
}
