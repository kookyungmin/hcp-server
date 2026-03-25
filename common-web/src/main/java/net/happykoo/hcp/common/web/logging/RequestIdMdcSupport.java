package net.happykoo.hcp.common.web.logging;

import static net.happykoo.hcp.common.web.security.SecurityHeaderNames.MDC_REQUEST_ID;
import static net.happykoo.hcp.common.web.security.SecurityHeaderNames.X_REQUEST_ID;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

public final class RequestIdMdcSupport {

  private RequestIdMdcSupport() {
  }

  public static String get() {
    return MDC.get(MDC_REQUEST_ID);
  }

  public static String bindHttpRequest(HttpServletRequest request) {
    return bind(request.getHeader(X_REQUEST_ID));
  }

  public static String bind(String requestId) {
    var previous = get();

    if (StringUtils.isBlank(requestId)) {
      MDC.remove(MDC_REQUEST_ID);
      return previous;
    }

    MDC.put(MDC_REQUEST_ID, requestId);
    return previous;
  }

  public static void restore(String previous) {
    if (StringUtils.isBlank(previous)) {
      MDC.remove(MDC_REQUEST_ID);
      return;
    }

    MDC.put(MDC_REQUEST_ID, previous);
  }
}
