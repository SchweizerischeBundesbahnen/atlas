package ch.sbb.atlas.versioning.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject.Fields;
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
  public void scenario14a() {
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
    assertThat(result.size()).isEqualTo(1);

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
   * Scenario 14a: "Gültig von" und "Gültig bis" einer einzigen Version nach innen verkürzen
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
  public void scenario14b() {
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
    assertThat(result.size()).isEqualTo(1);

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
}