package net.happykoo.hcp.adapter.out.orchestrator.fabric;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class K8sFabricConfig {

  @Bean
  public KubernetesClient kubernetesClient() {
    Config config = Config.autoConfigure(null);
    return new KubernetesClientBuilder().withConfig(config).build();
  }

}
