package net.happykoo.hcp.adapter.out.orchestrator;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.LabelSelectorBuilder;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicy;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.ExecuteOrchestratorCommandPort;
import net.happykoo.hcp.common.annotation.OrchestratorAdapter;
import net.happykoo.hcp.domain.instance.Instance;
import net.happykoo.hcp.infrastructure.properties.K8sProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@OrchestratorAdapter
@RequiredArgsConstructor
@EnableConfigurationProperties(K8sProperties.class)
public class K8sOrchestrator implements ExecuteOrchestratorCommandPort {

  private final K8sProperties k8sProperties;
  private final KubernetesClient k8sClient;

  @Override
  public void executeProvisionInstanceCommand(Instance instance) {
    Namespace namespace = buildNamespace(instance);
    PersistentVolumeClaim pvc = buildPersistentVolumeClaim(instance);
    Deployment deployment = buildDeployment(instance);
    Service service = buildService(instance);
    NetworkPolicy networkPolicy = buildNetworkPolicy(instance);

    k8sClient.resource(namespace)
        .serverSideApply();
    k8sClient.resource(pvc)
        .inNamespace(namespace.getMetadata().getName())
        .serverSideApply();
    k8sClient.resource(deployment)
        .inNamespace(namespace.getMetadata().getName())
        .serverSideApply();
    k8sClient.resource(service)
        .inNamespace(namespace.getMetadata().getName())
        .serverSideApply();
    k8sClient.resource(networkPolicy)
        .inNamespace(namespace.getMetadata().getName())
        .serverSideApply();
  }

  private Namespace buildNamespace(Instance instance) {
    String instanceId = instance.getInstanceId().toString();
    return new NamespaceBuilder()
        .withNewMetadata()
        .withName(generateNamespaceName(instanceId))
        .addToLabels(k8sProperties.getInstanceLabel(), instanceId)
        .addToLabels(k8sProperties.getOwnerLabel(), instance.getOwnerId().toString())
        .endMetadata()
        .build();
  }

  private Deployment buildDeployment(Instance instance) {
    String instanceId = instance.getInstanceId().toString();
    return new DeploymentBuilder()
        .withNewMetadata()
        .withName(generateAppName(instanceId))
        .withNamespace(generateNamespaceName(instanceId))
        .addToLabels(k8sProperties.getAppKey(), generateAppName(instanceId))
        .addToLabels(k8sProperties.getInstanceLabel(), instanceId)
        .endMetadata()
        .withNewSpec()
        .withReplicas(1)
        .withNewSelector()
        .addToMatchLabels(k8sProperties.getAppKey(), generateAppName(instanceId))
        .endSelector()
        .withNewTemplate()
        .withNewMetadata()
        .addToLabels(k8sProperties.getAppKey(), generateAppName(instanceId))
        .addToLabels(k8sProperties.getInstanceLabel(), instanceId)
        .endMetadata()
        .withNewSpec()
        .withRuntimeClassName(k8sProperties.getPodRuntimeClassName())
        .addNewContainer()
        .withName(generateAppName(instanceId))
        .withImage(instance.getImageName())
        .withImagePullPolicy(k8sProperties.getContainerImagePullPolicy())
        .addNewPort()
        .withName("ssh")
        .withContainerPort(22)
        .withProtocol("TCP")
        .endPort()
        .withNewResources()
        .addToRequests("cpu", new Quantity(instance.getCpu()))
        .addToRequests("memory", new Quantity(instance.getMemory()))
        .addToLimits("cpu", new Quantity(instance.getCpu()))
        .addToLimits("memory", new Quantity(instance.getMemory()))
        .endResources()
        .addNewEnv()
        .withName("TOTAL_DISK_BYTES")
        .withValue(generateStorageByte(instance.getStorageSize()))
        .endEnv()
        .addNewVolumeMount()
        .withName("data")
        .withMountPath("/data")
        .endVolumeMount()
        .endContainer()
        .addNewVolume()
        .withName("data")
        .withNewPersistentVolumeClaim()
        .withClaimName(generatePersistentVolumeClaimName(instanceId))
        .endPersistentVolumeClaim()
        .endVolume()
        .endSpec()
        .endTemplate()
        .endSpec()
        .build();
  }

  private String generateStorageByte(long storageSize) {
    return String.valueOf(storageSize * 1024L * 1024L * 1024L);
  }

  private PersistentVolumeClaim buildPersistentVolumeClaim(Instance instance) {
    String instanceId = instance.getInstanceId().toString();
    return new PersistentVolumeClaimBuilder()
        .withNewMetadata()
        .withName(generatePersistentVolumeClaimName(instanceId))
        .withNamespace(generateNamespaceName(instanceId))
        .addToLabels(k8sProperties.getAppKey(), generateAppName(instanceId))
        .addToLabels(k8sProperties.getInstanceLabel(), instanceId)
        .endMetadata()
        .withNewSpec()
        .withAccessModes(k8sProperties.getPvcAccessMode())
        .withStorageClassName(k8sProperties.getStorageClassName())
        .withNewResources()
        .addToRequests("storage",
            new Quantity(generateStorageSize(instance.getStorageSize())))
        .endResources()
        .endSpec()
        .build();
  }

  private Service buildService(Instance instance) {
    String instanceId = instance.getInstanceId().toString();
    return new ServiceBuilder()
        .withNewMetadata()
        .withName(generateServiceName(instanceId))
        .withNamespace(generateNamespaceName(instanceId))
        .addToLabels(k8sProperties.getAppKey(), generateAppName(instanceId))
        .addToLabels(k8sProperties.getInstanceLabel(), instanceId)
        .endMetadata()
        .withNewSpec()
        .withType(k8sProperties.getServiceType())
        .withSelector(Map.of(k8sProperties.getAppKey(), generateAppName(instanceId)))
        .addNewPort()
        .withName("ssh")
        .withPort(22)
        .withTargetPort(new IntOrString(22))
        .withProtocol("TCP")
        .endPort()
        .endSpec()
        .build();
  }

  private NetworkPolicy buildNetworkPolicy(Instance instance) {
    //TODO: default network policy 에 따라 분기
    String instanceId = instance.getInstanceId().toString();
    return new NetworkPolicyBuilder()
        .withApiVersion("networking.k8s.io/v1")
        .withKind("NetworkPolicy")
        .withNewMetadata()
        .withName(generateDefaultNetworkPolicyName(instanceId))
        .withNamespace(generateNamespaceName(instanceId))
        .endMetadata()
        .withNewSpec()
        .withPodSelector(new LabelSelectorBuilder()
            .addToMatchLabels(k8sProperties.getAppKey(), generateAppName(instanceId))
            .build())
        .withPolicyTypes("Ingress")
        .withIngress(List.of())
        .endSpec()
        .build();
  }

  private String generateNamespaceName(String instanceId) {
    return generateName(instanceId, null);
  }

  private String generateAppName(String instanceId) {
    return generateName(instanceId, k8sProperties.getAppKey());
  }

  private String generateServiceName(String instanceId) {
    return generateName(instanceId, k8sProperties.getServiceKey());
  }

  private String generatePersistentVolumeClaimName(String instanceId) {
    return generateName(instanceId, k8sProperties.getPvcKey());
  }

  private String generateDefaultNetworkPolicyName(String instanceId) {
    return generateName(instanceId, "default-np");
  }

  private String generateName(String instanceId, String suffix) {
    StringJoiner sj = new StringJoiner("-");

    sj.add(k8sProperties.getPrefix());
    sj.add(instanceId.split("-")[0]);
    if (StringUtils.isNotBlank(suffix)) {
      sj.add(suffix);
    }

    return sj.toString();
  }

  private String generateStorageSize(int storageSize) {
    return storageSize + k8sProperties.getPvcStorageUnit();
  }
}
