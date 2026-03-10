package net.happykoo.hcp.domain.terminal;

import com.fasterxml.jackson.annotation.JsonInclude;

//{"type":"ready"}
//{"type":"resize","cols": 120, "rows": 80 }
//{"type":"ping", "message": "p-1" }
//{"type":"close"}
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TerminalMessage(
    TerminalMessageType type,
    Integer cols,
    Integer rows,
    String message
) {

  public static TerminalMessage error(
      String errorMessage
  ) {
    return new TerminalMessage(TerminalMessageType.ERROR, null, null, errorMessage);
  }

  public static TerminalMessage ready() {
    return new TerminalMessage(TerminalMessageType.READY, null, null, null);
  }

  public static TerminalMessage close() {
    return new TerminalMessage(TerminalMessageType.CLOSE, null, null, null);
  }
}
