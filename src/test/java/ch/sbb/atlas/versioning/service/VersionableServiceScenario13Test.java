package ch.sbb.atlas.versioning.service;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
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
  public void scenario13a() {
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
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).collect(toList());

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

}