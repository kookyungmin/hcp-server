package net.happykoo.hcp.domain.terminal;

import java.util.List;
import java.util.UUID;

public record TerminalUserContext(
    UUID userId,
    List<String> roles
) {

}
