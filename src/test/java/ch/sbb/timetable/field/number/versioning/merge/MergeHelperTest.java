package ch.sbb.timetable.field.number.versioning.merge;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import ch.sbb.timetable.field.number.versioning.service.VersionableServiceBaseTest;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MergeHelperTest extends VersionableServiceBaseTest {

  @Test
  public void shouldMergeTwoIdenticalSequentialVersionedObject() {
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
    assertThat(result).isNotEmpty();
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result.size()).isEqualTo(2);

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
  public void shouldNotMergeTwoIdenticalNotSequentialVersionedObject() {
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
    assertThat(result).isNotEmpty();
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result.size()).isEqualTo(2);

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
  public void shouldNotMergeTwoNotIdenticalButSequentialVersionedObject() {
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
    assertThat(result).isNotEmpty();
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result.size()).isEqualTo(2);

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
  public void shouldNotMergeTwoNotIdenticalAntNotSequentialVersionedObject() {
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
    assertThat(result).isNotEmpty();
    result.sort(Comparator.comparing(VersionedObject::getValidFrom));
    assertThat(result.size()).isEqualTo(2);

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
  public void shouldReturnTrueWhenVersionsAreSequential() {
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
    boolean result = MergeHelper.areVersionsSequential(current, next);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenVersionsAreSequential() {
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
    boolean result = MergeHelper.areVersionsSequential(current, next);

    //then
    assertThat(result).isFalse();
  }

}