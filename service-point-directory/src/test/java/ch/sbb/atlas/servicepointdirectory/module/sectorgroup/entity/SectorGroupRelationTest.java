package ch.sbb.atlas.servicepointdirectory.module.sectorgroup.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.module.sectorgroup.model.SectorGroupRelationId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SectorGroupRelationTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void shouldAllowRelation() {
    SectorGroupRelationId id = new SectorGroupRelationId("group", "sector");

    SectorGroupRelation entity = SectorGroupRelation.builder()
        .sectorGroupRelationId(id)
        .build();

    Set<ConstraintViolation<SectorGroupRelation>> constraintViolations = validator.validate(entity);
    assertThat(constraintViolations).isEmpty();

  }
}
