package net.happykoo.hcp.application.port.in.result;

public record LoginResult(
    String accessToken,
    String refreshToken
) {

}
