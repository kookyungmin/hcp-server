package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.instance.InstanceStatus;
import net.happykoo.hcp.domain.instance.InstanceStorage;
import net.happykoo.hcp.domain.instance.ServerInstance;

@Entity
@Table(name = "h_instance")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JpaInstanceEntity extends JpaTimeBaseEntity {

  @Id
  @Column(name = "instance_id")
  private UUID instanceId;

  @Column(name = "owner_id")
  private UUID ownerId;

  @Column(name = "name")
  private String name;

  @OneToMany(mappedBy = "instance", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<JpaInstanceTagEntity> tags;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image")
  private JpaInstanceImageEntity image;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "spec")
  private JpaInstanceSpecEntity spec;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "vpc")
  private JpaNetworkVpcEntity vpc;

  @Column(name = "storage_type")
  private String storageType;

  @Column(name = "storage_size")
  private int storageSize;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "status")
  private InstanceStatus status;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "public_ip")
  private String publicIp;

  @Column(name = "private_ip")
  private String privateIp;

  public static JpaInstanceEntity from(ServerInstance instanceInfo) {
    return new JpaInstanceEntity(
        instanceInfo.getInstanceId(),
        instanceInfo.getOwnerId(),
        instanceInfo.getName(),
        new HashSet<>(),
        null,
        null,
        null,
        instanceInfo.getStorage().getStorageType(),
        instanceInfo.getStorage().getStorageSize(),
        InstanceStatus.PROVISIONING,
        instanceInfo.getFailureReason(),
        instanceInfo.getPublicIp(),
        instanceInfo.getPrivateIp()
    );
  }

  public ServerInstance toDomain() {
    return new ServerInstance(
        instanceId,
        ownerId,
        name,
        tags.stream()
            .map(tag -> tag.getId().getTag())
            .collect(Collectors.toSet()),
        image.toDomain(),
        vpc.toDomain(),
        spec.toDomain(),
        new InstanceStorage(storageType, storageSize),
        status
    );
  }
}
