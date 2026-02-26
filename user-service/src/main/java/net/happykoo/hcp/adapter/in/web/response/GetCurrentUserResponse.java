package net.happykoo.hcp.adapter.in.web.response;

import java.time.Instant;
import java.util.List;

public record GetCurrentUserResponse(
    String userId,
    String displayName,
    String email,
    Instant passwordChangedAt,
    List<String> roles
) {

}
