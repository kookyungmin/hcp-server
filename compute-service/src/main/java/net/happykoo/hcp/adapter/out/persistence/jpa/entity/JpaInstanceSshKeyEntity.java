package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.instance.InstanceSshKey;

@Entity
@Table(name = "h_instance_ssh_key")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JpaInstanceSshKeyEntity extends JpaTimeBaseEntity {

  @Id
  @Column(name = "instance_id")
  private UUID instanceId;

  @Column(name = "ssh_key_name")
  private String sshKeyName;

  @Column(name = "ssh_key")
  private String sshKey;

  public static JpaInstanceSshKeyEntity from(
      InstanceSshKey sshKey
  ) {
    return new JpaInstanceSshKeyEntity(
        sshKey.getInstanceId(),
        sshKey.getName(),
        sshKey.getKey()
    );
  }

  public InstanceSshKey toDomain() {
    return new InstanceSshKey(
        instanceId,
        sshKeyName,
        sshKey
    );
  }

}
