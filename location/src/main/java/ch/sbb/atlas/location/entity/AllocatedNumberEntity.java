package ch.sbb.atlas.location.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "service_point_sloid_allocated")
public class AllocatedNumberEntity {

  @Id
  private int number;

}
