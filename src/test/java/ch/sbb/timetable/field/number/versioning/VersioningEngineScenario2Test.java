package ch.sbb.timetable.field.number.versioning;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.BaseTest.VersionableObject.Relation;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersioningEngineScenario2Test extends VersionEngineBaseTest {

  /**
   * Szenario 2: Update innerhalb existierender Version
   * NEU:                       |___________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   * RESULTAT: |-----------|----|___________|-----|--------------------     NEUE VERSION EINGEFÜGT
   * Version:        1       2         4       5          3
   */
  @Test
  public void scenario2() {
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
    List<VersionedObject> result = versioningEngine.applyVersioning(
        VersionableObject.VERSIONABLE,
        versionableObject2,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(3);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).collect(toList());

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(editedValidFrom.minusDays(1));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties().size()).isEqualTo(2);
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(editedValidFrom);
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(editedValidTo);
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties().size()).isEqualTo(2);
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    List<Entity> oneToManyRelationEntitiesSecondVersionedObjectEntity = oneToManyRelationSecondVersionedObjectEntity.getOneToMany();
    assertThat(oneToManyRelationEntitiesSecondVersionedObjectEntity).isNotEmpty();
    assertThat(oneToManyRelationEntitiesSecondVersionedObjectEntity.size()).isEqualTo(1);
    Entity lineRelationSecondVersionedObject = oneToManyRelationEntitiesSecondVersionedObjectEntity.get(
        0);
    assertThat(lineRelationSecondVersionedObject.getProperties()).isNotEmpty();
    assertThat(lineRelationSecondVersionedObject.getProperties().size()).isEqualTo(1);
    assertThat(lineRelationSecondVersionedObject.getProperties().get(0).getValue()).isEqualTo(
        "first Relation");

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(editedValidTo.plusDays(1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity thirdVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties().size()).isEqualTo(2);
    Property propertyThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(
        thirdVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

  }

}