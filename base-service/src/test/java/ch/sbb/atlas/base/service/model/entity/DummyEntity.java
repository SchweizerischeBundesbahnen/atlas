package ch.sbb.atlas.base.service.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity(name = "id_generator_entity")
class DummyEntity {

  @Id
  private Long id;

  @GeneratorType(type = SboidGenerator.class, when = GenerationTime.INSERT)
  private String sboid;
}
