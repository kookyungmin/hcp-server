package net.happykoo.hcp.adapter.in.orchestrator;

import static io.fabric8.kubernetes.client.utils.KubernetesResourceUtil.getResourceVersion;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.happykoo.hcp.application.port.in.WatchInstanceStatusUseCase;
import net.happykoo.hcp.common.annotation.EventInAdapter;
import net.happykoo.hcp.infrastructure.properties.K8sProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@EventInAdapter
@EnableConfigurationProperties(K8sProperties.class)
@RequiredArgsConstructor
public class K8sInformerEventHandler {

  private final K8sProperties k8sProperties;
  private final WatchInstanceStatusUseCase watchInstanceStatusUseCase;

  public ResourceEventHandler<Pod> podEventHandler() {
    return new ResourceEventHandler<>() {
      @Override
      public void onAdd(Pod obj) {
        handleEvent(obj);
      }

      @Override
      public void onUpdate(Pod oldObj, Pod newObj) {
        String oldReady = extractPodReady(oldObj);
        String newReady = extractPodReady(newObj);

        String oldReason = extractPodMainReason(oldObj);
        String newReason = extractPodMainReason(newObj);
        if (Objects.equals(oldReason, newReason) &&
            Objects.equals(oldReady, newReady)) {
          return;
        }
        handleEvent(oldObj, newObj);
      }

      @Override
      public void onDelete(Pod obj, boolean deletedFinalStateUnknown) {
        handleEvent(obj);
      }
    };
  }

  public ResourceEventHandler<Deployment> deploymentEventHandler() {
    return new ResourceEventHandler<>() {
      @Override
      public void onAdd(Deployment obj) {
        handleEvent(obj);
      }

      @Override
      public void onUpdate(Deployment oldObj, Deployment newObj) {
        Long oldGen = oldObj.getMetadata().getGeneration();
        Long newGen = newObj.getMetadata().getGeneration();

        if (Objects.equals(oldGen, newGen)) {
          return;
        }
        handleEvent(oldObj, newObj);
      }

      @Override
      public void onDelete(Deployment obj, boolean deletedFinalStateUnknown) {
        handleEvent(obj);
      }
    };
  }

  public ResourceEventHandler<PersistentVolumeClaim> pvcEventHandler() {
    return new ResourceEventHandler<>() {
      @Override
      public void onAdd(PersistentVolumeClaim obj) {
        handleEvent(obj);
      }

      @Override
      public void onUpdate(PersistentVolumeClaim oldObj, PersistentVolumeClaim newObj) {
        handleEvent(oldObj, newObj);
      }

      @Override
      public void onDelete(PersistentVolumeClaim obj, boolean deletedFinalStateUnknown) {
        handleEvent(obj);
      }
    };
  }

  public ResourceEventHandler<Service> serviceEventHandler() {
    return new ResourceEventHandler<>() {
      @Override
      public void onAdd(Service obj) {
        handleEvent(obj);
      }

      @Override
      public void onUpdate(Service oldObj, Service newObj) {
        handleEvent(oldObj, newObj);
      }

      @Override
      public void onDelete(Service obj, boolean deletedFinalStateUnknown) {
        handleEvent(obj);
      }
    };
  }

  private void handleEvent(HasMetadata oldResource, HasMetadata newOldResource) {
    String oldRv = getResourceVersion(oldResource);
    String newRv = getResourceVersion(newOldResource);

    if (Objects.equals(oldRv, newRv)) {
      return; // resync성 호출 무시
    }

    handleEvent(newOldResource);
  }

  private void handleEvent(HasMetadata resource) {
    String instanceId = Optional.ofNullable(resource.getMetadata().getLabels())
        .map(labels -> labels.get(k8sProperties.getInstanceLabel()))
        .orElse(null);

    if (StringUtils.isBlank(instanceId)) {
      return;
    }

    watchInstanceStatusUseCase.watchStatusAndSendEvent(UUID.fromString(instanceId));
  }

  private String extractPodReady(Pod pod) {
    if (pod.getStatus() == null || pod.getStatus().getContainerStatuses() == null) {
      return "UNKNOWN";
    }
    boolean ready = pod.getStatus().getContainerStatuses()
        .stream()
        .allMatch(cs -> Boolean.TRUE.equals(cs.getReady()));
    return ready ? "READY" : "NOT_READY";
  }

  private String extractPodMainReason(Pod pod) {
    if (pod.getStatus() == null || pod.getStatus().getContainerStatuses() == null) {
      return null;
    }
    return pod.getStatus().getContainerStatuses().stream()
        .map(cs -> {
          if (cs.getState() == null) {
            return null;
          }
          if (cs.getState().getWaiting() != null) {
            return cs.getState().getWaiting().getReason();
          }
          if (cs.getState().getTerminated() != null) {
            return cs.getState().getTerminated().getReason();
          }
          return null;
        })
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }
}
