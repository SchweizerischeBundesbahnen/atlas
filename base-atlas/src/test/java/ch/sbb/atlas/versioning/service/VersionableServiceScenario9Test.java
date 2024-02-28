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

public class VersionableServiceScenario9Test extends VersionableServiceBaseTest {

  /**
   * Szenario 9a (Fall 5): Update ausserhalb der existierenden version
   *
   * Änderung 1     |_____|
   * Version                 |--------------------|
   *
   * Ergebnis                |--------------------|
   * Version ist vom update nicht betroffen
   */
  @Test
   void scenario9a() {
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
    assertThat(result).hasSize(2);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2019, 6, 1));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
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

  }

  /**
   * Szenario 9b (Spezialfall 5): Update vor erster existierender Version
   *
   * Änderung  |___|
   * |-----------------|----------------|-----------|         |-------------|
   * Version 1        Version 2     Version 3               Version 4
   *
   * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 1)
   */
  @Test
   void scenario9b() {
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
        List.of(versionableObject1, versionableObject2));

    //then
    assertThat(result).hasSize(3);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2019, 6, 1));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
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

  }

  /**
   * ATLAS-1752
   * Szenario 9b
   * Änderung mit start Version 1
   *                                     |_______________|
   *|---------------| |----------------|
   *   Version 1         Version 2
   * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 1)
   */
  @Test
   void scenario9bToTheFutureChangesFromVersion1() {
   //given
   versionableObject1 = VersionableObject
       .builder()
       .id(1L)
       .validFrom(LocalDate.of(2000, 1, 1))
       .validTo(LocalDate.of(2000, 11, 30))
       .property("change me")
       .build();
   versionableObject2 = VersionableObject
       .builder()
       .id(2L)
       .validFrom(LocalDate.of(2001, 1, 1))
       .validTo(LocalDate.of(2001, 11, 30))
       .property("leave me alone")
       .build();

   LocalDate editedValidFrom = LocalDate.of(2002, 1, 1);
   LocalDate editedValidTo = LocalDate.of(2002, 11, 30);

   VersionableObject editedVersion = VersionableObject.builder()
       .validFrom(editedValidFrom)
       .validTo(editedValidTo)
       .property("change me")
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
   assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
   assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2000, 11, 30));
   Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
   assertThat(firstVersionedObjectEntity).isNotNull();
   assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
   Property propertyFirstVersionedObjectEntity = filterProperty(
       firstVersionedObjectEntity.getProperties(), Fields.property);
   assertThat(propertyFirstVersionedObjectEntity).isNotNull();
   assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("change me");
   Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
       firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
   assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
   assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

   VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
   assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
   assertThat(secondVersionedObject).isNotNull();
   assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
   assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2001, 11, 30));
   Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
   assertThat(secondVersionedObjectEntity).isNotNull();
   assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
   Property propertySecondVersionedObjectEntity = filterProperty(
       secondVersionedObjectEntity.getProperties(), Fields.property);
   assertThat(propertySecondVersionedObjectEntity).isNotNull();
   assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("leave me alone");
   Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
       secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
   assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
   assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

   VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
   assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
   assertThat(thirdVersionedObject).isNotNull();
   assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2002, 1, 1));
   assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2002, 11, 30));
   Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
   assertThat(thirdVersionedObjectEntity).isNotNull();
   assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
   Property propertyThirdVersionedObjectEntity = filterProperty(
       thirdVersionedObjectEntity.getProperties(), Fields.property);
   assertThat(propertyThirdVersionedObjectEntity).isNotNull();
   assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("change me");
   Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
       thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
   assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
   assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();
  }

 /**
  * ATLAS-1743
  * Szenario 9b
  * Änderung mit start Version 2 --> in die Vergangenheit
  * |_______________|
  *                   |---------------| |----------------|
  *                        Version 1         Version 2
  * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 1)
  */
 @Test
 void scenario9bToThePastChangesFromVersion1() {
  //given
  versionableObject1 = VersionableObject
      .builder()
      .id(1L)
      .validFrom(LocalDate.of(2000, 1, 1))
      .validTo(LocalDate.of(2000, 11, 30))
      .property("change me")
      .build();
  versionableObject2 = VersionableObject
      .builder()
      .id(2L)
      .validFrom(LocalDate.of(1999, 1, 1))
      .validTo(LocalDate.of(1999, 11, 30))
      .property("leave me alone")
      .build();

  LocalDate editedValidFrom = LocalDate.of(1998, 1, 1);
  LocalDate editedValidTo = LocalDate.of(1998, 11, 30);

  VersionableObject editedVersion = VersionableObject.builder()
      .validFrom(editedValidFrom)
      .validTo(editedValidTo)
      .property("change me")
      .build();

  //when
  List<VersionedObject> result = versionableService.versioningObjects(
      versionableObject2,
      editedVersion,
      List.of(versionableObject1, versionableObject2));

  //then
  assertThat(result).hasSize(3);
  List<VersionedObject> sortedVersionedObjects =
      result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

  VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
  assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
  assertThat(firstVersionedObject).isNotNull();
  assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(1998, 1, 1));
  assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(1998, 11, 30));
  Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
  assertThat(firstVersionedObjectEntity).isNotNull();
  assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
  Property propertyFirstVersionedObjectEntity = filterProperty(
      firstVersionedObjectEntity.getProperties(), Fields.property);
  assertThat(propertyFirstVersionedObjectEntity).isNotNull();
  assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("change me");
  Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
      firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
  assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
  assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

  VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
  assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
  assertThat(secondVersionedObject).isNotNull();
  assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(1999, 1, 1));
  assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(1999, 11, 30));
  Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
  assertThat(secondVersionedObjectEntity).isNotNull();
  assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
  Property propertySecondVersionedObjectEntity = filterProperty(
      secondVersionedObjectEntity.getProperties(), Fields.property);
  assertThat(propertySecondVersionedObjectEntity).isNotNull();
  assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("leave me alone");
  Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
      secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
  assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
  assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

  VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
  assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
  assertThat(thirdVersionedObject).isNotNull();
  assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
  assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2000, 11, 30));
  Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
  assertThat(thirdVersionedObjectEntity).isNotNull();
  assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
  Property propertyThirdVersionedObjectEntity = filterProperty(
      thirdVersionedObjectEntity.getProperties(), Fields.property);
  assertThat(propertyThirdVersionedObjectEntity).isNotNull();
  assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("change me");
  Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
      thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
  assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
  assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();
 }

 /**
  * ATLAS-1753
  * Szenario 9bc
  * Änderung mit start Version 2 --> in die Vergangenheit
  * |__________________________________________________________|
  *   |---------------| |----------------| |----------------|
  *       Version 1         Version 2          Version 3
  * Ergebnis: Neue Version wird erstellt (mit Inhalt von Änderung und Version 1)
  */
 @Test
 void scenario9bcFromVersion3() {
  //given
  versionableObject1 = VersionableObject
      .builder()
      .id(1L)
      .validFrom(LocalDate.of(2000, 1, 1))
      .validTo(LocalDate.of(2000, 11, 30))
      .property("change me")
      .anotherProperty("a")
      .build();
  versionableObject2 = VersionableObject
      .builder()
      .id(2L)
      .validFrom(LocalDate.of(2001, 1, 1))
      .validTo(LocalDate.of(2001, 11, 30))
      .property("leave me alone")
      .anotherProperty("b")
      .build();
  versionableObject3 = VersionableObject
      .builder()
      .id(2L)
      .validFrom(LocalDate.of(2002, 1, 1))
      .validTo(LocalDate.of(2002, 11, 30))
      .property("change me")
      .anotherProperty("c")
      .build();

  LocalDate editedValidFrom = LocalDate.of(1999, 1, 1);
  LocalDate editedValidTo = LocalDate.of(2003, 11, 30);

  VersionableObject editedVersion = VersionableObject.builder()
      .validFrom(editedValidFrom)
      .validTo(editedValidTo)
      .property("leave me alone")
      .build();

  //when
  List<VersionedObject> result = versionableService.versioningObjects(
      versionableObject3,
      editedVersion,
      List.of(versionableObject1, versionableObject2, versionableObject3));

  //then
  assertThat(result).hasSize(3);
  List<VersionedObject> sortedVersionedObjects =
      result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

  VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
  assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
  assertThat(firstVersionedObject).isNotNull();
  assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(1999, 1, 1));
  assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2000, 12, 31));
  Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
  assertThat(firstVersionedObjectEntity).isNotNull();
  assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
  Property propertyFirstVersionedObjectEntity = filterProperty(
      firstVersionedObjectEntity.getProperties(), Fields.property);
  assertThat(propertyFirstVersionedObjectEntity).isNotNull();
  assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("leave me alone");
  Property anotherPropertyFirstVersionedObjectEntity = filterProperty(
      firstVersionedObjectEntity.getProperties(), Fields.anotherProperty);
  assertThat(anotherPropertyFirstVersionedObjectEntity).isNotNull();
  assertThat(anotherPropertyFirstVersionedObjectEntity.getValue()).isEqualTo("a");
  Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
      firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
  assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
  assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

  VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
  assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
  assertThat(secondVersionedObject).isNotNull();
  assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2001, 1, 1));
  assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2001, 12, 31));
  Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
  assertThat(secondVersionedObjectEntity).isNotNull();
  assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
  Property propertySecondVersionedObjectEntity = filterProperty(
      secondVersionedObjectEntity.getProperties(), Fields.property);
  assertThat(propertySecondVersionedObjectEntity).isNotNull();
  assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("leave me alone");
  Property anotherPropertySecondVersionedObjectEntity = filterProperty(
      secondVersionedObjectEntity.getProperties(), Fields.anotherProperty);
  assertThat(anotherPropertySecondVersionedObjectEntity).isNotNull();
  assertThat(anotherPropertySecondVersionedObjectEntity.getValue()).isEqualTo("b");
  Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
      secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
  assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
  assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

  VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
  assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
  assertThat(thirdVersionedObject).isNotNull();
  assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2002, 1, 1));
  assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2003, 11, 30));
  Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
  assertThat(thirdVersionedObjectEntity).isNotNull();
  assertThat(thirdVersionedObjectEntity.getProperties()).isNotEmpty();
  Property propertyThirdVersionedObjectEntity = filterProperty(
      thirdVersionedObjectEntity.getProperties(), Fields.property);
  assertThat(propertyThirdVersionedObjectEntity).isNotNull();
  assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("leave me alone");
  Property anotherPropertyThirdVersionedObjectEntity = filterProperty(
      thirdVersionedObjectEntity.getProperties(), Fields.anotherProperty);
  assertThat(anotherPropertyThirdVersionedObjectEntity).isNotNull();
  assertThat(anotherPropertyThirdVersionedObjectEntity.getValue()).isEqualTo("c");
  Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
      thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
  assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
  assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();
 }

 /**
   * Szenario 9c (Spezialfall 1): Update vor und während erster Version
   * Änderung    |_____________________|
   * |-----------------|----------------|-----------|         |-------------|
   * Version 1          Version 2     Version 3               Version 4
   *
   * Ergebnis: Version 1 wird verlängert
   */
  @Test
   void scenario9c() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2019, 1, 1);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(versionableObject1.getValidTo())
                                                       .property("Ciao-Ciao")
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1, versionableObject2));

    //then
    assertThat(result).hasSize(2);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2019, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();

  }

  /**
   * Szenario 9d: Änderung überschneidet Version 1 vorne. Props sind ungleich.
   *
   * NEU:      |_______|
   * IST:             |------------------|
   * Version                   1
   *
   * RESULTAT: |_______|-----------------|
   * Version      2            1
   *
   * Ergebnis: Version 1 wird auf "Gültig von"-Seite verkürzt.
   */
  @Test
   void scenario9d() {
    //given
    LocalDate editedValidFrom = versionableObject1.getValidFrom().minusMonths(3);
    LocalDate editedValidTo = versionableObject1.getValidFrom().plusMonths(1);

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
    assertThat(result).hasSize(2);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(editedValidTo.plusDays(1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(versionableObject1.getValidTo());
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
  }

  /**
   * Szenario 9e: Änderung überschneidet Version 1 hinten. Props sind ungleich.
   *
   * NEU:                        |_______|
   * IST:      |------------------|
   * Version           1
   *
   * RESULTAT: |-----------------|_______|
   * Version           1             2
   *
   * Ergebnis: Version 1 wird auf "Gültig bis"-Seite verkürzt.
   */
  @Test
   void scenario9e() {
    //given
    LocalDate editedValidFrom = versionableObject1.getValidTo().minusMonths(1);
    LocalDate editedValidTo = versionableObject1.getValidTo().plusMonths(1);

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
    assertThat(result).hasSize(2);
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
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObject.getEntity().getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
  }
}