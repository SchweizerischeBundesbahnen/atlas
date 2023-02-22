package ch.sbb.atlas.model.entity;

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
class DummyEntity {

  @Id
  private Long id;

  @BusinessIdGeneration(valueGenerator = SboidGenerator.class)
  private String sboid;
}
