package net.happykoo.hcp.infrastructure.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "k8s")
@Getter
@Setter
public class K8sProperties {

  private String prefix;
  private String appKey;
  private String pvcKey;
  private String serviceKey;
  private String serviceType;
  private String pvcAccessMode;
  private String pvcStorageUnit;
  private String containerImagePullPolicy;
  private String instanceLabel;
  private String ownerLabel;
  private String storageClassName;
  private String podRuntimeClassName;
  private String userName;

}
