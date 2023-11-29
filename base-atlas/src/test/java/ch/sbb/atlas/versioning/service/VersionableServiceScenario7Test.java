package ch.sbb.atlas.versioning.service;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.versioning.exception.DateValidationException;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersionableServiceScenario7Test extends VersionableServiceBaseTest {

  /**
   * Szenario 7a: Neue Version in der Zukunft, die letzte Version nur berührt
   * <p>
   * NEU:                       |________________________________ IST:      |----------------| Version:           1
   * <p>
   * RESULTAT: |----------------|________________________________     NEUE VERSION EINGEFÜGT Version:          1
   * 2
   */
  @Test
   void scenario7a() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2025, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2025, 12, 31);

    VersionableObject editedVersion = VersionableObject.builder()
        .property("Ciao-Ciao")
        .validFrom(editedValidFrom)
        .validTo(editedValidTo)
        .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject3,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).hasSize(4);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  /**
   * Szenario 7b: Neue Version in der Vergangenheit, die nächste Version nur berührt
   * <p>
   * NEU:      |________________________________| IST:                                       |----------------| Version:
   * 1
   * <p>
   * RESULTAT: |________________________________|----------------|     NEUE VERSION EINGEFÜGT Version:                 2
   * 1
   */
  @Test
   void scenario7b() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2019, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2019, 12, 31);

    VersionableObject editedVersion = VersionableObject.builder()
        .property("Ciao-Ciao")
        .validFrom(editedValidFrom)
        .validTo(editedValidTo)
        .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).hasSize(4);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2019, 12, 31));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();

  }

  /**
   * Szenario 7c: Neue Version in der Zukunft, die letzte Version nicht überschneidet
   * <p>
   * NEU:                             |________________________________ IST:      |----------------| Version:           1
   * <p>
   * RESULTAT: |----------------|     |________________________________     NEUE VERSION EINGEFÜGT Version:          1
   * 2
   */
  @Test
   void scenario7c() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2025, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2025, 12, 31);

    VersionableObject editedVersion = VersionableObject.builder()
        .property("Ciao-Ciao")
        .validFrom(editedValidFrom)
        .validTo(editedValidTo)
        .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).hasSize(4);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  /**
   * Szenario 7d: Neue Version in der Vergangenheit, die nächste Version nicht überschneidet
   * <p>
   * NEU:      |________________________________| IST:                                            |----------------| Version:
   * 1
   * <p>
   * RESULTAT: |________________________________|    |----------------|     NEUE VERSION EINGEFÜGT Version:                 2
   * 1
   */
  @Test
   void scenario7d() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2019, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2019, 5, 31);

    VersionableObject editedVersion = VersionableObject.builder()
        .property("Ciao-Ciao")
        .validFrom(editedValidFrom)
        .validTo(editedValidTo)
        .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).hasSize(4);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2019, 5, 31));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();

  }

  /**
   * ValidFrom is bigger than validTo: expected exception
   */
  @Test
   void scenarioValidFromBiggerThenValidTo() {
    //given
    versionableObject1.setValidFrom(LocalDate.of(2020, 12, 12));
    versionableObject1.setValidTo(LocalDate.of(2029, 12, 8));
    LocalDate editedValidFrom = LocalDate.of(2029, 12, 9);

    VersionableObject editedVersion = VersionableObject.builder()
        .property("Ciao-Ciao")
        .validFrom(editedValidFrom)
        .validTo(versionableObject1.getValidTo())
        .build();

    //when
    assertThatThrownBy(() -> {
      versionableService.versioningObjects(
          versionableObject1,
          editedVersion,
          Arrays.asList(versionableObject1, versionableObject2, versionableObject3));
      //then
    }).isInstanceOf(DateValidationException.class)
        .hasMessageContaining("Edited ValidFrom 2029-12-09 is bigger than edited ValidTo 2029-12-08");

  }

}
