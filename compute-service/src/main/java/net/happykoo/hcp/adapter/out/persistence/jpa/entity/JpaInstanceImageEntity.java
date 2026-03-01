package net.happykoo.hcp.adapter.out.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.happykoo.hcp.domain.instance.InstanceImage;

@Entity
@Table(name = "h_instance_image")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JpaInstanceImageEntity extends JpaTimeBaseEntity {

  @Id
  @Column(name = "image_code")
  private String imageCode;

  @Column(name = "image_name")
  private String imageName;

  @Column(name = "image_description")
  private String imageDescription;

  @Column(name = "os_name")
  private String osName;

  @Column(name = "os_version")
  private String osVersion;

  public InstanceImage toDomain() {
    return new InstanceImage(
        imageCode,
        imageName,
        imageDescription,
        osName,
        osVersion
    );
  }
}
