package net.happykoo.hcp.application.port.in.command;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public record FindPagedInstanceCommand(
    UUID ownerId,
    String searchKeyword,
    Pageable pageable
) {

}
