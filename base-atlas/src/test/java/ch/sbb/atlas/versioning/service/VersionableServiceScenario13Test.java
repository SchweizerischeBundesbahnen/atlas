package ch.sbb.atlas.versioning.service;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject.Fields;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersionableServiceScenario13Test extends VersionableServiceBaseTest {

  /**
   * Scenario 13a: Update startet auf einer Version und endet in der nächsten Version
   *
   * Änderung                          |_____|
   * |-----------------|-------------|
   * Version 1       Version 2
   *
   * Ergebnis:       |-----------------|_____|-------|
   * 1          3      2
   */
  @Test
   void scenario13a() {
    //given
    LocalDate editedValidFrom = versionableObject2.getValidFrom();
    LocalDate editedValidTo = LocalDate.of(2022, 6, 1);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .property("Ciao-Ciao")
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2));

    //then
    assertThat(result).hasSize(3);
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
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(editedValidTo.plusDays(1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao2");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();
  }

  /**
   * Scenario 13b: Update startet auf einer Version und endet in der nächsten Version
   *
   * Änderung                  |_____|
   * |-----------------|-------------|
   * Version 1       Version 2
   *
   * Ergebnis:     |-----------|_____|-------------|
   * 1          3        2
   */
  @Test
   void scenario13b() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2020, 6, 1);
    LocalDate editedValidTo = versionableObject1.getValidTo();

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .property("Ciao-Ciao")
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2));

    //then
    assertThat(result).hasSize(3);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(editedValidFrom.minusDays(1));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao2");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();
  }

  /**
   * Scenario 13c: Update startet auf einer Version und endet in der nächsten Version
   *
   * NEU:               |_____________|
   * IST:      |--------|-------|---------|----------|
   * Version:      1       2           3          4
   *
   * RESULTAT: |--------|_______|_____|---|----------|
   * Version:      1        2     5     3      4
   */
  @Test
   void scenario13c() {
    //given
    LocalDate editedValidFrom = versionableObject2.getValidFrom();
    LocalDate editedValidTo = versionableObject3.getValidFrom().plusMonths(2);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .property("Ciao-Ciao")
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2, versionableObject3, versionableObject4));

    //then
    assertThat(result).hasSize(5);
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
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(versionableObject3.getValidFrom());
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(editedValidTo.plusDays(1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(versionableObject3.getValidTo());
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao3");
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fifthVersionedObject).isNotNull();
    assertThat(fifthVersionedObject.getValidFrom()).isEqualTo(versionableObject4.getValidFrom());
    assertThat(fifthVersionedObject.getValidTo()).isEqualTo(versionableObject4.getValidTo());
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyFifthVersionedObjectEntity.getValue()).isEqualTo("Ciao4");
    Property oneToManyRelationFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFifthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFifthVersionedObjectEntity.getOneToMany()).isEmpty();
  }

  /**
   * Scenario 13c: Update startet auf einer Version und endet in einer späteren Version
   *
   * NEU:               |______________________|
   * IST:      |--------|-------|---------|----------|
   * Version:      1        2        3          4
   *
   * RESULTAT: |--------|_______|_________|____|-----|
   * Version:      1        2         3      5   4
   */
  @Test
   void scenario13d() {
    //given
    LocalDate editedValidFrom = versionableObject2.getValidFrom();
    LocalDate editedValidTo = versionableObject4.getValidFrom().plusMonths(2);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .property("Ciao-Ciao")
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2, versionableObject3, versionableObject4));

    //then
    assertThat(result).hasSize(5);
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
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(versionableObject3.getValidFrom());
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(versionableObject3.getValidTo());
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(versionableObject4.getValidFrom());
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(fifthVersionedObject).isNotNull();
    assertThat(fifthVersionedObject.getValidFrom()).isEqualTo(editedValidTo.plusDays(1));
    assertThat(fifthVersionedObject.getValidTo()).isEqualTo(versionableObject4.getValidTo());
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyFifthVersionedObjectEntity.getValue()).isEqualTo("Ciao4");
    Property oneToManyRelationFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFifthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFifthVersionedObjectEntity.getOneToMany()).isEmpty();
  }

  /**
   * Szenario 13e: Update startet in einer Version und endet auf die nächste Version
   * NEU:                   |_____________|
   * IST:      |--------|-------|---------|----------|
   * Version:       1       2        3           4
   *
   * RESULTAT: |--------|---|____|________|----------|
   * Version:       1     2    5      3           4
   */
  @Test
   void scenario13e() {
    //given
    LocalDate editedValidFrom = versionableObject2.getValidFrom().plusMonths(2);
    LocalDate editedValidTo = versionableObject3.getValidTo();

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .property("Ciao-Ciao")
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2, versionableObject3, versionableObject4));

    //then
    assertThat(result).hasSize(5);
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
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(editedValidFrom.minusDays(1));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao2");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(versionableObject3.getValidFrom());
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fifthVersionedObject).isNotNull();
    assertThat(fifthVersionedObject.getValidFrom()).isEqualTo(versionableObject4.getValidFrom());
    assertThat(fifthVersionedObject.getValidTo()).isEqualTo(versionableObject4.getValidTo());
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyFifthVersionedObjectEntity.getValue()).isEqualTo("Ciao4");
    Property oneToManyRelationFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFifthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFifthVersionedObjectEntity.getOneToMany()).isEmpty();
  }

  /**
   * Szenario 13f: Update startet in einer Version und endet auf eine spätere Version
   * NEU:            |____________________|
   * IST:      |--------|-------|---------|----------|
   * Version:      1         2       3         4
   *
   * RESULTAT: |-----|__|_______|_________|----------|
   * Version:      1  5     2        3            4
   */
  @Test
   void scenario13f() {
    //given
    LocalDate editedValidFrom = versionableObject1.getValidFrom().plusMonths(2);
    LocalDate editedValidTo = versionableObject3.getValidTo();

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .property("Ciao-Ciao")
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2, versionableObject3, versionableObject4));

    //then
    assertThat(result).hasSize(5);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(versionableObject1.getValidFrom());
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(editedValidFrom.minusDays(1));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(versionableObject1.getValidTo());
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(versionableObject3.getValidFrom());
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity fourthVersionedObjectEntity = fourthVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fifthVersionedObject).isNotNull();
    assertThat(fifthVersionedObject.getValidFrom()).isEqualTo(versionableObject4.getValidFrom());
    assertThat(fifthVersionedObject.getValidTo()).isEqualTo(versionableObject4.getValidTo());
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyFifthVersionedObjectEntity.getValue()).isEqualTo("Ciao4");
    Property oneToManyRelationFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFifthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFifthVersionedObjectEntity.getOneToMany()).isEmpty();
  }
}