package net.happykoo.hcp.domain.terminal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TerminalDomainTest {

  @Test
  @DisplayName("TerminalSession :: 사용자 컨텍스트에서 userId/role 문자열을 추출")
  void terminalSessionTest1() {
    UUID userId = UUID.randomUUID();
    TerminalSession session = new TerminalSession(
        "session-1",
        UUID.randomUUID(),
        new TerminalUserContext(userId, List.of("ROLE_USER", "ROLE_ADMIN"))
    );

    assertEquals(userId, session.getUserId());
    assertEquals("ROLE_USER,ROLE_ADMIN", session.getJoinedRoles());
  }

  @Test
  @DisplayName("TerminalSession :: 사용자 컨텍스트가 없으면 null 반환")
  void terminalSessionTest2() {
    TerminalSession session = new TerminalSession("session-1", UUID.randomUUID(), null);

    assertNull(session.getUserId());
    assertNull(session.getJoinedRoles());
  }

  @Test
  @DisplayName("TerminalMessage :: 편의 팩토리가 적절한 type/message 를 생성")
  void terminalMessageTest1() {
    assertEquals(TerminalMessageType.ERROR, TerminalMessage.error("boom").type());
    assertEquals("boom", TerminalMessage.error("boom").message());
    assertEquals(TerminalMessageType.READY, TerminalMessage.ready().type());
    assertEquals(TerminalMessageType.CLOSE, TerminalMessage.close().type());
  }
}
