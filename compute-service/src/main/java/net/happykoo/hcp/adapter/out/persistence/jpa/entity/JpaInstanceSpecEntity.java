package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.instance.InstanceSpec;

@Entity
@Table(name = "h_instance_spec")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JpaInstanceSpecEntity extends JpaTimeBaseEntity {

  @Id
  @Column(name = "spec_code")
  private String specCode;

  @Column(name = "spec_name", nullable = false)
  private String specName;

  @Column(name = "spec_description")
  private String specDescription;

  @Column(name = "cpu", nullable = false)
  private String cpu;

  @Column(name = "memory", nullable = false)
  private String memory;

  public static JpaInstanceSpecEntity from(InstanceSpec spec) {
    return new JpaInstanceSpecEntity(
        spec.getSpecCode(),
        spec.getSpecName(),
        spec.getSpecDescription(),
        spec.getCpu(),
        spec.getMemory()
    );
  }

  public InstanceSpec toDomain() {
    return new InstanceSpec(
        specCode,
        specName,
        specDescription,
        cpu,
        memory
    );
  }
}
