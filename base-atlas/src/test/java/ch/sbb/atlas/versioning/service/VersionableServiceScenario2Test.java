package ch.sbb.atlas.versioning.service;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject.Relation;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersionableServiceScenario2Test extends VersionableServiceBaseTest {

  /**
   * Szenario 2: Update innerhalb existierender Version NEU:                       |___________| IST:
   * |-----------|----------------------|-------------------- Version:        1                 2                  3
   * <p>
   * RESULTAT: |-----------|----|___________|-----|--------------------     NEUE VERSION EINGEFÜGT Version:        1       2
   * 4       5          3
   */
  @Test
   void scenario2() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2022, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2023, 6, 1);

    Relation relation = Relation.builder().id(1L).value("first Relation").build();
    VersionableObject editedVersion = VersionableObject.builder()
        .property("Ciao-Ciao")
        .oneToManyRelation(List.of(relation))
        .validFrom(editedValidFrom)
        .validTo(editedValidTo)
        .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject2,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(firstVersionedObject).isNotNull();
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(editedValidFrom.minusDays(1));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao2");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    List<Entity> oneToManyRelationEntitiesThirdVersionedObjectEntity = oneToManyRelationThirdVersionedObjectEntity.getOneToMany();
    assertThat(oneToManyRelationEntitiesThirdVersionedObjectEntity).isNotEmpty();
    assertThat(oneToManyRelationEntitiesThirdVersionedObjectEntity).hasSize(1);
    Entity lineRelationThirdVersionedObject = oneToManyRelationEntitiesThirdVersionedObjectEntity.get(
        0);
    assertThat(lineRelationThirdVersionedObject.getProperties()).isNotEmpty();
    assertThat(lineRelationThirdVersionedObject.getProperties()).hasSize(1);
    assertThat(lineRelationThirdVersionedObject.getProperties().get(0).getValue()).isEqualTo(
        "first Relation");

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(editedValidTo.plusDays(1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity fourthVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fifthVersionedObject).isNotNull();
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).isNotEmpty();

  }

  /**
   * Szenario 2b: Update innerhalb existierender Version: auf Version1 validFrom und validTo innerhalb von die Version2 mit
   * mindestenz eine zusätzliche property Änderung NEU:                       |___________| IST:
   * |-----------|----------------------|-------------------- Version:        1                 2                  3
   * <p>
   * RESULTAT: |-----------|----|___________|-----|--------------------     NEUE VERSION EINGEFÜGT Version:        1       2
   * 4       5          3
   */
  @Test
   void scenario2b() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2022, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2023, 6, 1);

    Relation relation = Relation.builder().id(1L).value("first Relation").build();
    VersionableObject editedVersion = VersionableObject.builder()
        .property("Ciao-Ciao")
        .oneToManyRelation(List.of(relation))
        .validFrom(editedValidFrom)
        .validTo(editedValidTo)
        .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject1,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(firstVersionedObject).isNotNull();
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).isNotEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(editedValidFrom.minusDays(1));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao2");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity thirdVersionedObjectEntity = thirdVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    List<Entity> oneToManyRelationEntitiesThirdVersionedObjectEntity = oneToManyRelationThirdVersionedObjectEntity.getOneToMany();
    assertThat(oneToManyRelationEntitiesThirdVersionedObjectEntity).isNotEmpty();
    assertThat(oneToManyRelationEntitiesThirdVersionedObjectEntity).hasSize(1);
    Entity lineRelationThirdVersionedObject = oneToManyRelationEntitiesThirdVersionedObjectEntity.get(
        0);
    assertThat(lineRelationThirdVersionedObject.getProperties()).isNotEmpty();
    assertThat(lineRelationThirdVersionedObject.getProperties()).hasSize(1);
    assertThat(lineRelationThirdVersionedObject.getProperties().get(0).getValue()).isEqualTo(
        "first Relation");

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(editedValidTo.plusDays(1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity fourthVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(fourthVersionedObjectEntity).isNotNull();
    assertThat(fourthVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFourthVersionedObjectEntity).isNotNull();
    assertThat(propertyFourthVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFourthVersionedObjectEntity = filterProperty(
        fourthVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFourthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFourthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.NOT_TOUCHED);
    assertThat(fifthVersionedObject).isNotNull();
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).isNotEmpty();

  }

}
