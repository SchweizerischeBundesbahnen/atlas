package ch.sbb.atlas.versioning.service;

import static ch.sbb.atlas.versioning.MergeBaseTest.MergeVersionableObject.Fields.anotherProperty;
import static ch.sbb.atlas.versioning.MergeBaseTest.MergeVersionableObject.Fields.oneToManyRelation;
import static ch.sbb.atlas.versioning.MergeBaseTest.MergeVersionableObject.Fields.property;
import static ch.sbb.atlas.versioning.MergeBaseTest.MergeVersionableObject.Fields.propertyToBeIgnored;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.MergeBaseTest;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VersionableMergeScenarioTest extends MergeBaseTest {

  protected final VersionableService versionableService = new VersionableServiceImpl();

  protected MergeVersionableObject versionableObject1;
  protected MergeVersionableObject versionableObject2;
  protected MergeVersionableObject versionableObject3;
  protected MergeVersionableObject versionableObject4;

  @BeforeEach
   void init() {
    versionableObject1 = MergeVersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .propertyToBeIgnored("Property to be ignored 1")
        .anotherProperty("prop1")
        .build();
    versionableObject2 = MergeVersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .property("Ciao2")
        .propertyToBeIgnored("Property to be ignored 2")
        .anotherProperty("prop2")
        .build();
    versionableObject3 = MergeVersionableObject
        .builder()
        .id(3L)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .property("Ciao3")
        .propertyToBeIgnored("Property to be ignored 3")
        .anotherProperty("prop3")
        .build();
    versionableObject4 = MergeVersionableObject
        .builder()
        .id(4L)
        .validFrom(LocalDate.of(2025, 1, 1))
        .validTo(LocalDate.of(2025, 12, 31))
        .property("Ciao4")
        .propertyToBeIgnored("Property to be ignored 4")
        .anotherProperty("prop4")
        .build();

  }

  /**
   * Merge zwei versionen
   *
   * NEU:                              |___________|
   * name=Ciao2
   * IST:      |-----------|-----------|-----------|-----------|
   * Version:        1          2          3           4
   * Änderung:  name=Ciao1 name=Ciao2  name=Ciao3  name=Ciao4
   *
   * RESULTAT: |----------|-----------------------|----------|
   * Version:        1               2               4
   * Änderung:  name=SBB1       name=SBB2        name=SBB4
   */
  @Test
   void scenarioMergeTwoVersions() {
    //given

    MergeVersionableObject editedVersion = MergeVersionableObject.builder()
                                                                 .property("Ciao2")
                                                                 .anotherProperty("prop2")
                                                                 .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject3,
        editedVersion,
        List.of(versionableObject1, versionableObject2, versionableObject3, versionableObject4));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(4);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao2");
    Property anotherPropertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyThirdVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyThirdVersionedObjectEntity.getValue()).isEqualTo("prop2");
    Property propertyToBeIgnoredThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredThirdVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject3.getPropertyToBeIgnored());
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
  }

  /**
   * Merge drei versionen
   *
   * NEU:                  |___________|
   * name=Ciao1
   * IST:      |-----------|-----------|----------|
   * Version:        1          2          3
   * Änderung:  name=Ciao1  name=Ciao2  name=Ciao1
   *
   * RESULTAT: |--------------------------------|
   * Version:                 1
   * Änderung:            name=Ciao1
   */
  @Test
   void scenarioMergeAllVersions() {
    //given
    versionableObject3.setProperty("Ciao1");
    versionableObject3.setAnotherProperty("prop1");

    MergeVersionableObject editedVersion = MergeVersionableObject.builder()
                                                                 .property("Ciao1")
                                                                 .anotherProperty("prop1")
                                                                 .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject2,
        editedVersion,
        List.of(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);
    assertThat(firstVersionedObject).isNotNull();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property anotherPropertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyThirdVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyThirdVersionedObjectEntity.getValue()).isEqualTo("prop1");
    Property propertyToBeIgnoredThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredThirdVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject3.getPropertyToBeIgnored());
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  /**
   * Merge über mehrere versionen
   *
   * NEU:                  |_______________________|
   * name=Ciao1
   * IST:      |-----------|-----------|-----------|-----------|-----------|
   * Version:        1          2          3          4         5
   * Änderung:  name=Ciao1  name=Ciao2  name=Ciao3  name=Ciao1  name=Ciao4
   *
   * RESULTAT: |----------------------------------------------|-----------|
   * Version:                 1                                 2
   * Änderung:            name=Ciao1                          name=Ciao4
   */
  @Test
   void scenarioMergeThroughMultipleVersions() {

    MergeVersionableObject versionableObject5 = MergeVersionableObject
        .builder()
        .id(4L)
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 12, 31))
        .property("Ciao5")
        .propertyToBeIgnored("Property to be ignored 5")
        .anotherProperty("prop5")
        .build();

    versionableObject4.setProperty("Ciao1");
    versionableObject4.setAnotherProperty("prop1");

    LocalDate editedValidFrom = LocalDate.of(2022, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2024, 12, 31);
    MergeVersionableObject editedVersion = MergeVersionableObject.builder()
                                                                 .validFrom(editedValidFrom)
                                                                 .validTo(editedValidTo)
                                                                 .property("Ciao1")
                                                                 .anotherProperty("prop1")
                                                                 .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject2,
        editedVersion,
        List.of(versionableObject1, versionableObject2, versionableObject3, versionableObject4,
            versionableObject5));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property anotherPropertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyFourthVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyFourthVersionedObjectEntity.getValue()).isEqualTo("prop1");
    Property propertyToBeIgnoredFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredFourthVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject4.getPropertyToBeIgnored());
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fifthVersionedObject).isNotNull();
    assertThat(fifthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(fifthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), property);
    assertThat(propertyFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyFifthVersionedObjectEntity.getValue()).isEqualTo("Ciao5");
    Property anotherPropertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyFifthVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyFifthVersionedObjectEntity.getValue()).isEqualTo("prop5");
    Property propertyToBeIgnoredFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredFifthVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject5.getPropertyToBeIgnored());
    Property oneToManyRelationFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationFifthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFifthVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  /**
   * Merge über mehrere versionen
   *
   * NEU:                 |_______________________|
   * name=Ciao1
   * IST:      |----------|-----------|-----------|  |-----------|-----------|
   * Version:        1          2         3              4         5
   * Änderung:  name=Ciao1  name=Ciao2  name=Ciao3     name=Ciao1  name=Ciao5
   *
   * RESULTAT: |---------------------------------|  |-----------|-----------|
   * Version:                 1                          2          3
   * Änderung:            name=Ciao1                 name=Ciao1  name=Ciao5
   */
  @Test
   void scenarioMergeThroughMultipleVersionsWithInterruption() {

    MergeVersionableObject versionableObject5 = MergeVersionableObject
        .builder()
        .id(4L)
        .validFrom(LocalDate.of(2026, 1, 1))
        .validTo(LocalDate.of(2026, 12, 31))
        .property("Ciao5")
        .propertyToBeIgnored("Property to be ignored 5")
        .anotherProperty("prop5")
        .build();

    versionableObject4.setProperty("Ciao1");
    versionableObject4.setAnotherProperty("prop1");
    versionableObject4.setValidFrom(LocalDate.of(2025, 2, 1));

    LocalDate editedValidFrom = LocalDate.of(2022, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2024, 12, 31);
    MergeVersionableObject editedVersion = MergeVersionableObject.builder()
                                                                 .validFrom(editedValidFrom)
                                                                 .validTo(editedValidTo)
                                                                 .property("Ciao1")
                                                                 .anotherProperty("prop1")
                                                                 .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject2,
        editedVersion,
        List.of(versionableObject1, versionableObject2, versionableObject3, versionableObject4,
            versionableObject5));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property anotherPropertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyThirdVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyThirdVersionedObjectEntity.getValue()).isEqualTo("prop1");
    Property propertyToBeIgnoredThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredThirdVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject3.getPropertyToBeIgnored());
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2025, 2, 1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property anotherPropertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyFourthVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyFourthVersionedObjectEntity.getValue()).isEqualTo("prop1");
    Property propertyToBeIgnoredFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredFourthVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject4.getPropertyToBeIgnored());
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fifthVersionedObject).isNotNull();
    assertThat(fifthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    assertThat(fifthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2026, 12, 31));
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), property);
    assertThat(propertyFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyFifthVersionedObjectEntity.getValue()).isEqualTo("Ciao5");
    Property anotherPropertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyFifthVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyFifthVersionedObjectEntity.getValue()).isEqualTo("prop5");
    Property propertyToBeIgnoredFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredFifthVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject5.getPropertyToBeIgnored());
    Property oneToManyRelationFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationFifthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFifthVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  /**
   * Merge mit ignored property
   * Änderung
   * |_______________________________|
   *     Version 1         Version 2
   * |-----------------|-------------|
   * Ergebnis: Versionen werden gemerged
   */
  @Test
   void scenarioMergeWithIgnoredProperty() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2020, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2023, 12, 31);

    MergeVersionableObject editedVersion = MergeVersionableObject.builder()
                                                                 .validFrom(editedValidFrom)
                                                                 .validTo(editedValidTo)
                                                                 .property("Ciao-Ciao")
                                                                 .anotherProperty("prop-prop")
                                                                 .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(2);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property anotherPropertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertySecondVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertySecondVersionedObjectEntity.getValue()).isEqualTo("prop-prop");
    Property propertyToBeIgnoredSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredSecondVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredSecondVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject2.getPropertyToBeIgnored());
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  /**
   * Upate in der mitte 2 Versionen und updated Version gemerged
   *
   * Änderung                     |_____________|
   * |------------------|------------------|
   * Version 1            Version 2
   *
   * Resultat        |-----------|-------------|-----------|
   * Version 1   Version 2     Version 3
   *
   * Ergebnis: Versionen werden gemerged
   */
  @Test
   void scenarioUpdateInDerMitteUndMerge() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2021, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2023, 6, 1);

    MergeVersionableObject editedVersion = MergeVersionableObject.builder()
                                                                 .validFrom(editedValidFrom)
                                                                 .validTo(editedValidTo)
                                                                 .property("Ciao-Ciao")
                                                                 .anotherProperty("prop-prop")
                                                                 .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(4);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2021, 5, 31));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property anotherPropertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyFirstVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyFirstVersionedObjectEntity.getValue()).isEqualTo("prop1");
    Property propertyToBeIgnoredFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredFirstVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject1.getPropertyToBeIgnored());
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.DELETE);

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2021, 6, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 6, 1));
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property anotherPropertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyThirdVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyThirdVersionedObjectEntity.getValue()).isEqualTo("prop-prop");
    Property propertyToBeIgnoredThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredThirdVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject2.getPropertyToBeIgnored());
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2023, 6, 2));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao2");
    Property anotherPropertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), anotherProperty);
    assertThat(anotherPropertyFourthVersionedObjectEntity).isNotNull();
    assertThat(anotherPropertyFourthVersionedObjectEntity.getValue()).isEqualTo("prop2");
    Property propertyToBeIgnoredFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), propertyToBeIgnored);
    assertThat(propertyToBeIgnoredFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyToBeIgnoredFourthVersionedObjectEntity.getValue()).isEqualTo(
        versionableObject2.getPropertyToBeIgnored());
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  private Property filterProperty(List<Property> properties, String fieldProperty) {
    return properties.stream().filter(property -> fieldProperty.equals(
                         property.getKey()))
                     .findFirst()
                     .orElse(null);
  }

}