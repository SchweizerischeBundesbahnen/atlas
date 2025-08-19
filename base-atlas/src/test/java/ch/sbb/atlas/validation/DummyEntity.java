package ch.sbb.atlas.validation;

import ch.sbb.atlas.model.Identifiable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "id_generator_entity")
class DummyEntity implements Identifiable {

  @Id
  private Long id;

}
