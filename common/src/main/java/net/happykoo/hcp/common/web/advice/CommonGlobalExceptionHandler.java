package net.happykoo.hcp.common.web.advice;

import lombok.NonNull;
import net.happykoo.hcp.common.web.response.CommonResponseEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public CommonResponseEntity<String> handleNoResourceFoundException(
      NoResourceFoundException ex,
      @NonNull WebRequest request
  ) {
    return CommonResponseEntity.error(
        ex.getMessage(),
        getRequestURI(request),
        HttpStatus.NOT_FOUND
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CommonResponseEntity<String> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull WebRequest request
  ) {
    return CommonResponseEntity.error(
        ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage(),
        getRequestURI(request),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public CommonResponseEntity<String> handleMethodForbidden(
      AuthorizationDeniedException ex,
      @NonNull WebRequest request
  ) {
    return CommonResponseEntity.error(
        ex.getMessage(),
        getRequestURI(request),
        HttpStatus.FORBIDDEN
    );
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public final CommonResponseEntity<String> handleAllExceptions(
      Exception ex,
      WebRequest request
  ) {
    return CommonResponseEntity.error(
        ex.getMessage(),
        getRequestURI(request),
        HttpStatus.INTERNAL_SERVER_ERROR
    );
  }

  private String getRequestURI(WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getRequestURI();
  }
}
