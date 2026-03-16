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
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.networking.v1.IPBlockBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicy;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyEgressRule;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyEgressRuleBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyIngressRule;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyIngressRuleBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyPeerBuilder;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyPort;
import io.fabric8.kubernetes.api.model.networking.v1.NetworkPolicyPortBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.happykoo.hcp.application.port.out.ExecuteOrchestratorCommandPort;
import net.happykoo.hcp.application.port.out.data.InstanceStatusData;
import net.happykoo.hcp.application.port.out.data.PodData;
import net.happykoo.hcp.common.annotation.OrchestratorAdapter;
import net.happykoo.hcp.common.web.exception.ResourceNotFoundException;
import net.happykoo.hcp.domain.instance.Instance;
import net.happykoo.hcp.domain.instance.InstanceNetworkPolicy;
import net.happykoo.hcp.domain.instance.NetworkPolicyType;
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
    NetworkPolicy networkPolicy = buildNetworkPolicy(
        instance.getInstanceId(),
        List.of(),
        List.of());

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

  @Override
  public void executeUpdateNetworkPolicyCommand(
      UUID instanceId,
      List<InstanceNetworkPolicy> networkPolicies
  ) {
    updateNetworkPolicy(instanceId, networkPolicies);
    patchServiceForNetworkPolicy(instanceId, networkPolicies);
  }

  private void patchServiceForNetworkPolicy(
      UUID instanceId,
      List<InstanceNetworkPolicy> networkPolicies
  ) {
    var instanceIdString = instanceId.toString();
    var namespace = generateNamespaceName(instanceIdString);
    var serviceName = generateServiceName(instanceIdString);
    var ports = buildServicePorts(networkPolicies);

    var portsJson = ports.stream()
        .map(p -> """
            {
              "name": "%s",
              "port": %d,
              "targetPort": %s,
              "protocol": "%s"
            }
            """.formatted(
            p.getName(),
            p.getPort(),
            p.getTargetPort().getIntVal(),
            p.getProtocol()
        ))
        .collect(Collectors.joining(","));

    var patched = """
        {
          "spec": {
            "ports": [%s]
          }
        }
        """.formatted(portsJson);

    k8sClient.services()
        .inNamespace(namespace)
        .withName(serviceName)
        .patch(patched);
  }

  private void updateNetworkPolicy(
      UUID instanceId,
      List<InstanceNetworkPolicy> networkPolicies
  ) {
    var instanceIdString = instanceId.toString();
    var ingressRules = networkPolicies.stream()
        .filter(p -> p.getType() == NetworkPolicyType.INGRESS)
        .map(this::generateNetworkPolicyIngressRule)
        .toList();

    var egressRules = networkPolicies.stream()
        .filter(p -> p.getType() == NetworkPolicyType.EGRESS)
        .map(this::generateNetworkPolicyEgressRule)
        .toList();

    var newNetworkPolicy = buildNetworkPolicy(
        instanceId,
        ingressRules,
        egressRules
    );

    //기존 network policy 삭제 후 저장
    k8sClient.network().v1()
        .networkPolicies()
        .inNamespace(generateNamespaceName(instanceIdString))
        .withName(generateNetworkPolicyName(instanceIdString))
        .delete();
    k8sClient.resource(newNetworkPolicy)
        .inNamespace(generateNamespaceName(instanceIdString))
        .serverSideApply();
  }

  private NetworkPolicyEgressRule generateNetworkPolicyEgressRule(
      InstanceNetworkPolicy instanceNetworkPolicy
  ) {
    var peer = new NetworkPolicyPeerBuilder()
        .withIpBlock(new IPBlockBuilder()
            .withCidr(instanceNetworkPolicy.getIpCidr())
            .build())
        .build();

    var ports = parseNetworkPolicyPort(instanceNetworkPolicy.getPort());

    var builder = new NetworkPolicyEgressRuleBuilder()
        .withTo(peer);

    if (!ports.isEmpty()) {
      builder.withPorts(ports);
    }

    return builder.build();
  }

  private NetworkPolicyIngressRule generateNetworkPolicyIngressRule(
      InstanceNetworkPolicy instanceNetworkPolicy
  ) {
    var peer = new NetworkPolicyPeerBuilder()
        .withIpBlock(new IPBlockBuilder()
            .withCidr(instanceNetworkPolicy.getIpCidr())
            .build())
        .build();

    var ports = parseNetworkPolicyPort(instanceNetworkPolicy.getPort());

    var builder = new NetworkPolicyIngressRuleBuilder()
        .withFrom(peer);

    if (!ports.isEmpty()) {
      builder.withPorts(ports);
    }

    return builder.build();
  }

  private List<NetworkPolicyPort> parseNetworkPolicyPort(String portSpec) {
    if (StringUtils.isBlank(portSpec)) {
      return Collections.emptyList();
    }
    var trimmed = portSpec.trim();

    if (trimmed.contains("-")) {
      String[] parts = trimmed.split("-", 2);
      int start = Integer.parseInt(parts[0].trim());
      int end = Integer.parseInt(parts[1].trim());

      validatePortRange(start, end);

      return List.of(
          new NetworkPolicyPortBuilder()
              .withProtocol("TCP")
              .withPort(new IntOrString(start))
              .withEndPort(end)
              .build()
      );
    }

    var port = Integer.parseInt(trimmed);
    validatePort(port);

    return List.of(
        new NetworkPolicyPortBuilder()
            .withProtocol("TCP")
            .withPort(new IntOrString(port))
            .build()
    );
  }

  private void executeScaleUpOrDownDeployment(Instance instance) {
    var instanceId = instance.getInstanceId().toString();
    var patch = String.format("""
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
    var instanceId = instance.getInstanceId().toString();
    var patch = String.format("""
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
          getPublicIp(pod),
          getPrivateIp(service),
          getServicePortMessage(service)
      );
    }

    return InstanceStatusData.processing(instanceId);
  }

  private String getServicePortMessage(Service service) {
    return Optional.ofNullable(service.getSpec())
        .map(ServiceSpec::getPorts)
        .map(ports -> ports.stream()
            .map(port -> String.format("%s:%s►%s", port.getName(),
                port.getPort(),
                port.getNodePort()))
            .collect(Collectors.joining(" ")))
        .orElse(null);
  }

  private String getPublicIp(Pod pod) {
    return Optional.ofNullable(pod.getSpec().getNodeName())
        .map(nodeName -> k8sClient.nodes().withName(nodeName).get())
        .map(Node::getStatus)
        .flatMap(status -> status.getAddresses()
            .stream()
            .filter(a -> "InternalIP".equals(a.getType()))
            .map(NodeAddress::getAddress)
            .findFirst())
        .orElse(null);
  }

  private String getPrivateIp(Service service) {
    return Optional.ofNullable(service.getSpec())
        .map(ServiceSpec::getClusterIP)
        .orElse(null);
  }

  private Namespace buildNamespace(Instance instance) {
    var instanceId = instance.getInstanceId().toString();
    return new NamespaceBuilder()
        .withNewMetadata()
        .withName(generateNamespaceName(instanceId))
        .addToLabels(k8sProperties.getInstanceLabel(), instanceId)
        .addToLabels(k8sProperties.getOwnerLabel(), instance.getOwnerId().toString())
        .endMetadata()
        .build();
  }

  private Deployment buildDeployment(Instance instance) {
    var instanceId = instance.getInstanceId().toString();
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
    var instanceId = instance.getInstanceId().toString();
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
    var instanceId = instance.getInstanceId().toString();
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
        .endSpec()
        .build();
  }

  private NetworkPolicy buildNetworkPolicy(
      UUID instanceId,
      List<NetworkPolicyIngressRule> ingressRules,
      List<NetworkPolicyEgressRule> egressRules
  ) {
    var instanceIdString = instanceId.toString();
    var builder = new NetworkPolicyBuilder()
        .withApiVersion("networking.k8s.io/v1")
        .withKind("NetworkPolicy")
        .withNewMetadata()
        .withName(generateNetworkPolicyName(instanceIdString))
        .withNamespace(generateNamespaceName(instanceIdString))
        .endMetadata()
        .withNewSpec()
        .withPodSelector(new LabelSelectorBuilder()
            .addToMatchLabels(k8sProperties.getAppKey(),
                generateAppName(instanceIdString))
            .build())
        .withPolicyTypes(resolvePolicyTypes(ingressRules, egressRules))
        .withIngress(ingressRules)
        .endSpec();

    if (!egressRules.isEmpty()) {
      builder.editSpec()
          .withEgress(egressRules)
          .endSpec();
    }

    return builder.build();
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

  private String generateNetworkPolicyName(String instanceId) {
    return generateName(instanceId, k8sProperties.getNetworkPolicyKey());
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

  private List<String> resolvePolicyTypes(
      List<NetworkPolicyIngressRule> ingressRules,
      List<NetworkPolicyEgressRule> egressRules
  ) {
    List<String> types = new ArrayList<>();
    types.add("Ingress");
    if (!egressRules.isEmpty()) {
      types.add("Egress");
    }
    return types;
  }

  private List<ServicePort> buildServicePorts(List<InstanceNetworkPolicy> networkPolicies) {
    return networkPolicies.stream()
        .filter(np -> np.getType() == NetworkPolicyType.INGRESS)
        .flatMap(np -> toServicePort(np).stream())
        .toList();
  }

  private List<ServicePort> toServicePort(InstanceNetworkPolicy networkPolicy) {
    var portSpec = networkPolicy.getPort();
    if (StringUtils.isBlank(portSpec)) {
      return Collections.emptyList();
    }

    List<ServicePort> ports = new ArrayList<>();

    if (portSpec.contains("-")) {
      String[] parts = portSpec.split("-", 2);
      int start = Integer.parseInt(parts[0].trim());
      int end = Integer.parseInt(parts[1].trim());

      validatePortRange(start, end);

      for (int p = start; p <= end; p++) {
        ports.add(new ServicePortBuilder()
            .withName(networkPolicy.getPolicyName() + "-" + p)
            .withPort(p)
            .withTargetPort(new IntOrString(p))
            .withProtocol("TCP")
            .build());
      }

      return ports;
    }

    int port = Integer.parseInt(portSpec);
    validatePort(port);

    ports.add(new ServicePortBuilder()
        .withName(networkPolicy.getPolicyName())
        .withPort(port)
        .withTargetPort(new IntOrString(port))
        .withProtocol("TCP")
        .build());

    return ports;
  }

  private void validatePortRange(int start, int end) {
    if (start < 1 || end > 65535 || end < start) {
      throw new IllegalArgumentException("Invalid NetworkPolicy port range");
    }
  }

  private void validatePort(int port) {
    if (port < 1 || port > 65535) {
      throw new IllegalArgumentException("Invalid NetworkPolicy port");
    }
  }
}
