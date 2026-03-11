package net.happykoo.hcp.adapter.out.orchestrator.openapi;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import java.io.IOException;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class K8sClientConfig {

  @Bean
  public ApiClient apiClient() throws IOException {
    String serviceHost = System.getenv("KUBERNETES_SERVICE_HOST");
    String servicePort = System.getenv("KUBERNETES_SERVICE_PORT");

    ApiClient client;
    if (serviceHost != null && servicePort != null) {
      client = ClientBuilder.cluster().build();
    } else {
      client = Config.defaultClient();
    }
    return client;
  }

}
