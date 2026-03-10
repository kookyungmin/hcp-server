package net.happykoo.hcp.infrastructure.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "websocket.orchestrator-worker")
@NoArgsConstructor
@Getter
@Setter
public class OrchestratorWorkerWebSocketProperties {

  private String url;
}
