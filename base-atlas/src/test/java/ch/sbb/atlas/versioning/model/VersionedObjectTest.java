package ch.sbb.atlas.versioning.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.exception.VersioningException;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class VersionedObjectTest {

  @Test
   void shouldThrowVersioningExceptionWhenActionIsNewAndEntityIdIsNotNull() {
    //given
    LocalDate validFrom = LocalDate.of(2021, 1, 1);
    LocalDate validTo = LocalDate.of(2022, 1, 1);

    Entity entity = Entity.builder().id(1L).properties(Collections.emptyList()).build();
    //when
    try {
      VersionedObject.buildVersionedObject(validFrom, validTo, entity, VersioningAction.NEW);
    } catch (VersioningException versioningException) {
      //then
      assertThat(versioningException).isNotNull();
    }
  }

  @Test
   void shouldThrowVersioningExceptionWhenValidFromIsBiggerThenValidTo() {
    //given
    LocalDate validFrom = LocalDate.of(2021, 1, 1);
    LocalDate validTo = LocalDate.of(2020, 1, 1);

    Entity entity = Entity.builder().id(1L).properties(Collections.emptyList()).build();
    //when
    try {
      VersionedObject.buildVersionedObject(validFrom, validTo, entity, VersioningAction.UPDATE);
    } catch (VersioningException versioningException) {
      //then
      assertThat(versioningException).isNotNull();
    }
  }

}