package net.happykoo.hcp.adapter.out.orchestrator.fabric;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.LabelSelectorBuilder;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicy;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.ExecuteOrchestratorCommandPort;
import net.happykoo.hcp.application.port.out.data.InstanceStatusData;
import net.happykoo.hcp.application.port.out.data.PodData;
import net.happykoo.hcp.common.annotation.OrchestratorAdapter;
import net.happykoo.hcp.common.web.exception.ResourceNotFoundException;
import net.happykoo.hcp.domain.instance.Instance;
import net.happykoo.hcp.infrastructure.properties.K8sProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@OrchestratorAdapter
@RequiredArgsConstructor
@EnableConfigurationProperties(K8sProperties.class)
public class K8sFabricClientAdapter implements ExecuteOrchestratorCommandPort {

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

  @Override
  public InstanceStatusData executeGetInstanceStatusCommand(UUID instanceId) {
    String instanceIdString = instanceId.toString();
    Map<String, String> labels = Map.of(k8sProperties.getInstanceLabel(), instanceIdString);
    String namespace = generateNamespaceName(instanceIdString);

    Deployment deployment = findDeployment(namespace, labels);
    Pod pod = findPodByLabels(namespace, labels);
    PersistentVolumeClaim pvc = findPvcByLabels(namespace, labels);
    Service service = findServiceByLabels(namespace, labels);

    return resolveInstanceStatus(
        instanceIdString,
        deployment,
        pod,
        pvc,
        service
    );
  }

  @Override
  public PodData executeGetPodInfoCommand(UUID instanceId) {
    String instanceIdString = instanceId.toString();

    Map<String, String> labels = Map.of(k8sProperties.getInstanceLabel(), instanceIdString);
    Pod pod = findPodByLabels(generateNamespaceName(instanceIdString), labels);

    if (pod == null) {
      throw new ResourceNotFoundException("해당 Pod이 존재하지 않습니다.");
    }

    return new PodData(
        pod.getMetadata().getNamespace(),
        pod.getMetadata().getName()
    );
  }

  @Override
  public void executeStopInstanceCommand(UUID instanceId) {
    k8sClient.apps()
        .deployments()
        .inNamespace(generateNamespaceName(instanceId.toString()))
        .withName(generateAppName(instanceId.toString()))
        .scale(0);
  }

  @Override
  public void executeRestartInstanceCommand(UUID instanceId) {
    k8sClient.apps()
        .deployments()
        .inNamespace(generateNamespaceName(instanceId.toString()))
        .withName(generateAppName(instanceId.toString()))
        .scale(1);

  }

  @Override
  public void executeTerminateInstanceCommand(UUID instanceId) {
    k8sClient.namespaces()
        .withName(generateNamespaceName(instanceId.toString()))
        .delete();
  }

  @Override
  public void executeScaleInstanceCommand(Instance instance) {
    executeStopInstanceCommand(instance.getInstanceId());
    //TODO: storage PVC 재생성 -> Pod(PVC 2개 연결) 에서 데이터 복사 -> Pod(PVC 1개로 변경) -> PVC 1개 죽이고
//    executeScaleUpStorage(instance);
    executeScaleUpOrDownDeployment(instance);
    executeRestartInstanceCommand(instance.getInstanceId());
  }

  private void executeScaleUpOrDownDeployment(Instance instance) {
    String instanceId = instance.getInstanceId().toString();
    String patch = String.format("""
            {
              "spec": {
                "template": {
                  "spec": {
                    "containers": [
                      {
                        "name": "%s",
                        "resources": {
                          "requests": {
                            "cpu": "%s",
                            "memory": "%s"
                          },
                          "limits": {
                            "cpu": "%s",
                            "memory": "%s"
                          }
                        },
                        "env": [
                            {
                              "name": "TOTAL_DISK_BYTES",
                              "value": "%s"
                            },
                            {
                              "name": "RESERVED_BYTES",
                              "value": "0"
                            }
                        ]
                      }
                    ]
                  }
                }
              }
            }
            """, generateAppName(instanceId), instance.getCpu(), instance.getMemory(),
        instance.getCpu(), instance.getMemory(),
        generateStorageByte(instance.getStorageSize()));

    k8sClient.apps()
        .deployments()
        .inNamespace(generateNamespaceName(instanceId))
        .withName(generateAppName(instanceId))
        .patch(patch);
  }

  private void executeScaleUpStorage(Instance instance) {
    String instanceId = instance.getInstanceId().toString();
    String patch = String.format("""
            {
              "spec": {
                "resources": {
                  "requests": {
                    "storage": "%sGi"
                  }
                }
              }
           }
        """, instance.getStorageSize());

    k8sClient.persistentVolumeClaims()
        .inNamespace(generateNamespaceName(instanceId))
        .withName(generatePersistentVolumeClaimName(instanceId))
        .patch(patch);
  }

  private Deployment findDeployment(String namespace, Map<String, String> labels) {
    return k8sClient.apps().deployments()
        .inNamespace(namespace)
        .withLabels(labels)
        .list()
        .getItems()
        .stream()
        .findFirst()
        .orElse(null);
  }

  private Pod findPodByLabels(String namespace, Map<String, String> labels) {
    return k8sClient.pods()
        .inNamespace(namespace)
        .withLabels(labels)
        .list()
        .getItems()
        .stream()
        .findFirst()
        .orElse(null);
  }

  private PersistentVolumeClaim findPvcByLabels(String namespace, Map<String, String> labels) {
    return k8sClient.persistentVolumeClaims()
        .inNamespace(namespace)
        .withLabels(labels)
        .list()
        .getItems()
        .stream()
        .findFirst()
        .orElse(null);
  }

  private Service findServiceByLabels(String namespace, Map<String, String> labels) {
    return k8sClient.services()
        .inNamespace(namespace)
        .withLabels(labels)
        .list()
        .getItems()
        .stream()
        .findFirst()
        .orElse(null);
  }

  private InstanceStatusData resolveInstanceStatus(
      String instanceId,
      Deployment deployment,
      Pod pod,
      PersistentVolumeClaim pvc,
      Service service
  ) {
    if (deployment == null &&
        pod == null &&
        pvc == null &&
        service == null) {
      return InstanceStatusData.deleted(instanceId);
    }

    //Pod 로 실패 여부 판단
    if (pod != null &&
        pod.getStatus() != null &&
        pod.getStatus().getContainerStatuses() != null) {
      for (var cs : pod.getStatus().getContainerStatuses()) {
        if (cs.getState() != null && cs.getState().getWaiting() != null) {
          String reason = cs.getState().getWaiting().getReason();
          String message = cs.getState().getWaiting().getMessage();

          if ("ImagePullBackOff".equals(reason)
              || "ErrImagePull".equals(reason)
              || "CrashLoopBackOff".equals(reason)
              || "CreateContainerConfigError".equals(reason)) {
            return InstanceStatusData.failed(
                instanceId,
                message
            );
          }
        }
      }
    }

    boolean pvcBound = Optional.ofNullable(pvc)
        .map(PersistentVolumeClaim::getStatus)
        .map(s -> "Bound".equalsIgnoreCase(s.getPhase()))
        .orElse(false);

    boolean podReady = Optional.ofNullable(pod)
        .map(Pod::getStatus)
        .map(PodStatus::getContainerStatuses)
        .map(s -> s.stream()
            .allMatch(c -> Boolean.TRUE.equals(c.getReady())))
        .orElse(false) && pod.getStatus().getPhase().equals("Running");
    boolean serviceReady = service != null;
    int deploymentReplicas = Optional.ofNullable(deployment)
        .map(Deployment::getStatus)
        .map(DeploymentStatus::getAvailableReplicas)
        .orElse(0);

    if (!podReady && deploymentReplicas == 0) {
      return InstanceStatusData.stopped(instanceId);
    }

    if (podReady && pvcBound && serviceReady && deploymentReplicas > 0) {
      return InstanceStatusData.success(
          instanceId,
          getPublicIp(pod, service),
          getPrivateIp(service)
      );
    }

    return InstanceStatusData.processing(instanceId);
  }

  private String getPublicIp(Pod pod, Service service) {
    String nodeIp = Optional.ofNullable(pod.getSpec().getNodeName())
        .map(nodeName -> k8sClient.nodes().withName(nodeName).get())
        .map(Node::getStatus)
        .flatMap(status -> status.getAddresses()
            .stream()
            .filter(a -> "InternalIP".equals(a.getType()))
            .map(NodeAddress::getAddress)
            .findFirst())
        .orElse(null);

    Integer nodePort = service.getSpec()
        .getPorts()
        .getFirst()
        .getNodePort();

    return nodeIp + ":" + nodePort;
  }

  private String getPrivateIp(Service service) {
    return Optional.ofNullable(service.getSpec())
        .map(ServiceSpec::getClusterIP)
        .orElse(null);
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
        .withSelector(
            Map.of(k8sProperties.getAppKey(), generateAppName(instanceId)))
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
            .addToMatchLabels(k8sProperties.getAppKey(),
                generateAppName(instanceId))
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
