package ch.sbb.atlas.versioning.merge;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import ch.sbb.atlas.versioning.service.VersionableServiceBaseTest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class MergeHelperTest extends VersionableServiceBaseTest {

  @Test
  void shouldMergeTwoIdenticalSequentialVersionedObject() {
    //given

    Property prop = Property.builder().key("prop").value("ciao").build();
    Entity entity = Entity.builder().properties(List.of(prop)).id(1L).build();

    VersionedObject first = VersionedObject.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .entity(entity)
        .build();

    VersionedObject second = VersionedObject.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .entity(entity)
        .build();
    //when
    List<VersionedObject> result = MergeHelper.mergeVersionedObject(
        Arrays.asList(first, second));
    //then
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result).hasSize(2);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));

    VersionedObject secondVersionedObject = result.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));

  }

  @Test
  void shouldMergeTwoIdenticalSequentialVersionedObjectFirstNewSecondUpdate() {
    //given
    Property prop = Property.builder().key("prop").value("ciao").build();

    VersionedObject first = VersionedObject.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .entity(
            Entity.builder().properties(List.of(prop)).build())
        .build();

    VersionedObject second = VersionedObject.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .entity(Entity.builder()
            .properties(List.of(prop))
            .id(1L)
            .build())
        .build();
    //when
    List<VersionedObject> result = MergeHelper.mergeVersionedObject(
        Arrays.asList(first, second));
    //then
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result).hasSize(2);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));

    VersionedObject secondVersionedObject = result.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));
  }

  @Test
  void shouldNotMergeTwoIdenticalNotSequentialVersionedObject() {
    //given

    Property prop = Property.builder().key("prop").value("ciao").build();
    Entity entity = Entity.builder().properties(List.of(prop)).id(1L).build();

    VersionedObject first = VersionedObject.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .entity(entity)
        .action(VersioningAction.NOT_TOUCHED)
        .build();

    VersionedObject second = VersionedObject.builder()
        .validFrom(LocalDate.of(2001, 1, 2))
        .validTo(LocalDate.of(2001, 12, 31))
        .entity(entity)
        .action(VersioningAction.NOT_TOUCHED)
        .build();
    //when
    List<VersionedObject> result = MergeHelper.mergeVersionedObject(
        Arrays.asList(first, second));
    //then
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result).hasSize(2);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));

    VersionedObject secondVersionedObject = result.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 2));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));

  }

  @Test
  void shouldNotMergeTwoNotIdenticalButSequentialVersionedObject() {
    //given

    Property prop = Property.builder().key("prop").value("ciao").build();
    Entity entity = Entity.builder().properties(List.of(prop)).id(1L).build();

    VersionedObject first = VersionedObject.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .entity(entity)
        .action(VersioningAction.NOT_TOUCHED)
        .build();

    Property prop1 = Property.builder().key("prop").value("ciao ciao").build();
    Entity entity1 = Entity.builder().properties(List.of(prop1)).id(1L).build();
    VersionedObject second = VersionedObject.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .entity(entity1)
        .action(VersioningAction.NOT_TOUCHED)
        .build();
    //when
    List<VersionedObject> result = MergeHelper.mergeVersionedObject(
        Arrays.asList(first, second));
    //then
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result).hasSize(2);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));

    VersionedObject secondVersionedObject = result.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));

  }

  @Test
  void shouldNotMergeTwoNotIdenticalAntNotSequentialVersionedObject() {
    //given

    Property prop = Property.builder().key("prop").value("ciao").build();
    Entity entity = Entity.builder().properties(List.of(prop)).id(1L).build();

    VersionedObject first = VersionedObject.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .entity(entity)
        .action(VersioningAction.NOT_TOUCHED)
        .build();

    Property prop1 = Property.builder().key("prop").value("ciao ciao").build();
    Entity entity1 = Entity.builder().properties(List.of(prop1)).id(1L).build();
    VersionedObject second = VersionedObject.builder()
        .validFrom(LocalDate.of(2001, 1, 2))
        .validTo(LocalDate.of(2001, 12, 31))
        .entity(entity1)
        .action(VersioningAction.NOT_TOUCHED)
        .build();
    //when
    List<VersionedObject> result = MergeHelper.mergeVersionedObject(
        Arrays.asList(first, second));
    //then
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result).hasSize(2);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));

    VersionedObject secondVersionedObject = result.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 2));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));

  }

  @Test
  void shouldReturnTrueWhenVersionsAreSequential() {
    //given
    VersionedObject current = VersionedObject.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();

    VersionedObject next = VersionedObject.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();
    //when
    boolean result = MergeHelper.areVersionedObjectsSequential(current, next);

    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnFalseWhenVersionsAreSequential() {
    //given
    VersionedObject current = VersionedObject.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();

    VersionedObject next = VersionedObject.builder()
        .validFrom(LocalDate.of(2001, 1, 2))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();
    //when
    boolean result = MergeHelper.areVersionedObjectsSequential(current, next);

    //then
    assertThat(result).isFalse();
  }

}