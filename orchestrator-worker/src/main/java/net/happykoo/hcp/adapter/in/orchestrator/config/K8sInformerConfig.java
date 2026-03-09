package net.happykoo.hcp.adapter.in.orchestrator.config;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.adapter.in.orchestrator.K8sInformerEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class K8sInformerConfig {

  private final KubernetesClient kubernetesClient;
  private final K8sInformerEventHandler k8SInformerEventHandler;

  @Bean(initMethod = "startAllRegisteredInformers", destroyMethod = "stopAllRegisteredInformers")
  public SharedInformerFactory sharedInformerFactory() {
    SharedInformerFactory sharedInformerFactory = kubernetesClient.informers();

    SharedIndexInformer<Pod> podInformer = sharedInformerFactory.sharedIndexInformerFor(
        Pod.class, 10000L
    );
    SharedIndexInformer<Deployment> deploymentInformer = sharedInformerFactory.sharedIndexInformerFor(
        Deployment.class, 0L
    );
    SharedIndexInformer<PersistentVolumeClaim> pvcInformer = sharedInformerFactory.sharedIndexInformerFor(
        PersistentVolumeClaim.class, 0L
    );
    SharedIndexInformer<Service> serviceInformer = sharedInformerFactory.sharedIndexInformerFor(
        Service.class, 0L
    );

    podInformer.addEventHandler(k8SInformerEventHandler.podEventHandler());
    deploymentInformer.addEventHandler(k8SInformerEventHandler.deploymentEventHandler());
    pvcInformer.addEventHandler(k8SInformerEventHandler.pvcEventHandler());
    serviceInformer.addEventHandler(k8SInformerEventHandler.serviceEventHandler());

    return sharedInformerFactory;
  }
}
