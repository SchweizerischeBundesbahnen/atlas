package ch.sbb.atlas.versioning.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject.Fields;
import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersionableServiceScenario14Test extends VersionableServiceBaseTest {

  /**
   * Scenario 14a: Linke Grenze ("Gültig von") auf gleichen Tag setzen, wie rechte Grenze ("Gültig bis")
   *
   * Änderung                          |
   * |-----------------|
   * Version 1
   *
   * Ergebnis: Das "Gültig von"-Datum wird auf das gleiche Datum, wie das "Gültig bis"-Datum gesetzt.
   * |
   * 1
   */
  @Test
   void scenario14a() {
    //given
    LocalDate editedValidFrom = versionableObject1.getValidTo();

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(editedValidFrom);
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
  }

  /**
   * Scenario 14b: "Gültig von" und "Gültig bis" einer einzigen Version nach innen verkürzen
   *
   * NEU:       |_|
   * IST:      |---|
   * Version:    1
   *
   * RESULTAT:  |_|
   * Version:    1
   *
   * Ergebnis: Die Version ist später gültig und weniger lang.
   */
  @Test
   void scenario14b() {
    //given
    LocalDate editedValidFrom = versionableObject1.getValidFrom().plusMonths(1);
    LocalDate editedValidTo = versionableObject1.getValidTo().minusMonths(1);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(editedValidTo);
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
  }

  /**
   * Szenario 14c: Verlängerung von validFrom&validTo, ohne eine Property zu verändern.
   *
   * NEU:      |________|
   * IST:        |---|
   * Version:      1
   *
   * RESULTAT: |________|
   * Version:      1
   */
  @Test
   void scenario14c() {
    //given
    LocalDate editedValidFrom = versionableObject1.getValidFrom().minusMonths(1);
    LocalDate editedValidTo = versionableObject1.getValidTo().plusMonths(1);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .property("stretch")
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        List.of(versionableObject1));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("stretch");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();
  }

  /**
   * Szenario 14d: Verlängerung von erster Version in die Vergangenheit
   *
   * NEU:      |________________________________|
   * IST:                      |----------------| |---------|
   * Version:                           1              2
   *
   * RESULTAT: |________________________________| |---------|
   * Version:                  1                       2
   */
  @Test
   void scenario14d() {
    //given
    LocalDate editedValidFrom = versionableObject1.getValidFrom().minusMonths(3);
    LocalDate editedValidTo = versionableObject1.getValidTo();

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
    assertThat(result).hasSize(2);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(editedValidTo);
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

    VersionedObject secondVersionedObject = result.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
  }

  /**
   * Szenario 14e: Verlängerung von letzter Version in die Zukunft
   *
   * NEU:                         |________________________________|
   * IST:      |----------------| |---------|
   * Version:           1              2
   *
   * RESULTAT: |----------------| |________________________________|
   * Version:          1                       2
   */
  @Test
   void scenario14e() {
    //given
    LocalDate editedValidFrom = versionableObject3.getValidFrom();
    LocalDate editedValidTo = versionableObject3.getValidTo().plusMonths(5);

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
    assertThat(result).hasSize(2);

    VersionedObject firstVersionedObject = result.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);

    VersionedObject secondVersionedObject = result.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).isNotEmpty();
    Property propertysecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertysecondVersionedObjectEntity).isNotNull();
    assertThat(propertysecondVersionedObjectEntity.getValue()).isEqualTo("Ciao3");
    Property oneToManyRelationsecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationsecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationsecondVersionedObjectEntity.getOneToMany()).isEmpty();
  }

  /**
   * Szenario 14m: Start-Version 2: Gültig-bis verkürzen. Props sind ungleich. Props von Version 2&Änderung sind gleich. (Ist Szenario 2 ohne Property-Update)
   *
   * NEU:                            |_________|
   * IST:      |----------------| |----------------| |----------------|
   * Version:           1                  2                  3
   *
   * RESULTAT: Warnung, dass dies nicht erlaubt ist
   */
  @Test
   void scenario14mShouldThrowException() {
    //given
    LocalDate editedValidFrom = versionableObject2.getValidFrom().plusMonths(2);
    LocalDate editedValidTo = versionableObject2.getValidTo().minusMonths(2);

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .validFrom(editedValidFrom)
                                                       .validTo(editedValidTo)
                                                       .build();

    //when & then
    assertThatExceptionOfType(VersioningException.class).isThrownBy(
        () -> versionableService.versioningObjects(
            versionableObject2,
            editedVersion,
            List.of(versionableObject1, versionableObject2, versionableObject3)));
  }
}