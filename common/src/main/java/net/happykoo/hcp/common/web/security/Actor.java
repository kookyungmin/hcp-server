package net.happykoo.hcp.common.web.security;

import java.util.List;

public record Actor(
    String userId,
    List<String> scopes
) {

}
