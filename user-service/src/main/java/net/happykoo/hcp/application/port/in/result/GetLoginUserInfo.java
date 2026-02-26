package net.happykoo.hcp.application.port.in.result;

import java.time.Instant;
import java.util.UUID;

public record GetLoginUserInfo(
    UUID userId,
    String displayName,
    String email,
    Instant passwordChangedAt
) {

}
