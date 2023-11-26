package ch.sbb.atlas.versioning.service;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject.Relation;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class VersionableServiceApplyVersioningTest extends VersionableServiceBaseTest {

  @Mock
  private Consumer<VersionableObject> save;

  @Mock
  private LongConsumer deleteById;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
   void shouldConvertToEntityAndCallSaveOnUpdate() {
    //given
    Relation relation1 = Relation.builder().id(1L).value("value1").build();
    Relation relation2 = Relation.builder().id(2L).value("value2").build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .property("Ciao1")
        .oneToManyRelation(List.of(relation1, relation2))
        .build();

    VersionableObject editedVersion = VersionableObject.builder().property("Ciao-Ciao").build();
    Relation editedRelation = Relation.builder().id(3L).value("value-3-changed").build();
    editedVersion.setOneToManyRelation(List.of(editedRelation));

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject2,
        editedVersion, Arrays.asList(versionableObject1, versionableObject2));
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).collect(toList());

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);

    versionableService.applyVersioning(VersionableObject.class, result, save, deleteById);

    verify(save).accept(any());
    verifyNoInteractions(deleteById);
  }

  @Test
   void shouldConvertToEntityAndCallSaveOnNew() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2019, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2019, 6, 1);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .property("Ciao-Ciao")
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);

    versionableService.applyVersioning(VersionableObject.class, result, save, deleteById);

    verify(save).accept(any());
    verifyNoInteractions(deleteById);
  }

  @Test
   void shouldConvertToEntityAndCallSaveOnUpdateAndDelete() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2022, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2023, 12, 31);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject3));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);

    versionableService.applyVersioning(VersionableObject.class, result, save, deleteById);

    InOrder inOrder = Mockito.inOrder(deleteById, save);
    inOrder.verify(deleteById).accept(firstVersionedObject.getEntity().getId());
    inOrder.verify(save).accept(any());

  }
}