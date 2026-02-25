package net.happykoo.hcp.adapter.in.web.advice;

import lombok.NonNull;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CommonResponseEntity<String> handleNoResourceFoundException(
      NoResourceFoundException ex,
      @NonNull WebRequest request
  ) {
    return CommonResponseEntity.error(
        ex.getMessage(),
        getRequestURI(request),
        HttpStatus.BAD_REQUEST
    );
  }

  private String getRequestURI(WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getRequestURI();
  }

}
