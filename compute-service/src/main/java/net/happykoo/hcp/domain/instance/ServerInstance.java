package net.happykoo.hcp.domain.instance;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.happykoo.hcp.domain.network.NetworkVpc;

@Getter
@AllArgsConstructor
public class ServerInstance {

  private UUID instanceId;
  private UUID ownerId;
  private String name;
  private Set<String> tags;
  private InstanceImage image;
  private NetworkVpc vpc;
  private InstanceSpec spec;
  private InstanceStorage storage;
  private InstanceStatus status;
  private String failureReason;
  private String publicIp;
  private String privateIp;

  public ServerInstance(
      UUID instanceId,
      UUID ownerId,
      String name,
      Set<String> tags,
      InstanceImage image,
      NetworkVpc vpc,
      InstanceSpec spec,
      InstanceStorage storage,
      InstanceStatus status
  ) {
    this(
        instanceId,
        ownerId,
        name,
        tags,
        image,
        vpc,
        spec,
        storage,
        status,
        null,
        null,
        null
    );
  }

  public String getImageName() {
    return Optional.ofNullable(image)
        .map(InstanceImage::getImageName)
        .orElse(null);
  }

  public String getOsName() {
    return Optional.ofNullable(image)
        .map(InstanceImage::getOsName)
        .orElse(null);
  }

  public String getOsVersion() {
    return Optional.ofNullable(image)
        .map(InstanceImage::getOsVersion)
        .orElse(null);
  }

  public String getVpcName() {
    return Optional.ofNullable(vpc)
        .map(NetworkVpc::getName)
        .orElse(null);
  }

  public String getDefaultEgressPolicy() {
    return Optional.ofNullable(vpc)
        .map(NetworkVpc::getDefaultEgressPolicy)
        .map(Enum::name)
        .orElse(null);
  }

  public String getDefaultIngressPolicy() {
    return Optional.ofNullable(vpc)
        .map(NetworkVpc::getDefaultIngressPolicy)
        .map(Enum::name)
        .orElse(null);
  }

  public String getCidrBlock() {
    return Optional.ofNullable(vpc)
        .map(NetworkVpc::getCidrBlock)
        .orElse(null);
  }

  public String getCpu() {
    return Optional.ofNullable(spec)
        .map(InstanceSpec::getCpu)
        .orElse(null);
  }

  public String getMemory() {
    return Optional.ofNullable(spec)
        .map(InstanceSpec::getMemory)
        .orElse(null);
  }

  public String getStorageType() {
    return Optional.ofNullable(storage)
        .map(InstanceStorage::getStorageType)
        .orElse(null);
  }

  public int getStorageSize() {
    return Optional.ofNullable(storage)
        .map(InstanceStorage::getStorageSize)
        .orElse(0);
  }
}
