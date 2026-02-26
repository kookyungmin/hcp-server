package net.happykoo.hcp.application.port.out.data;

import java.time.Instant;
import java.util.UUID;

public record UserAccountView(
    UUID userId,
    String email,
    Instant passwordChangedAt
) {

}
