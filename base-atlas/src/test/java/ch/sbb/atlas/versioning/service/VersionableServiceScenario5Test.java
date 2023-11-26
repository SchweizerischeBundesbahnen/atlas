package ch.sbb.atlas.versioning.service;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionableServiceScenario5Test extends VersionableServiceBaseTest {

  /**
   * Szenario 5: Update, das über mehrere Versionen hinausragt
   * <p>
   * NEU:             |___________________________________| IST:      |-----------|-----------|-----------|-------------------
   * Version:        1           2          3               4
   * <p>
   * RESULTAT: |------|_____|__________|____________|_____|------------     NEUE VERSION EINGEFÜGT Version:      1     5       2
   * 3         6      4
   */
  @Test
   void scenario5() {
    //given
    LocalDate editedValidFrom = LocalDate.of(2020, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2025, 6, 1);

    VersionableObject editedVersion = VersionableObject.builder()
        .property("Ciao-Ciao")
        .validFrom(editedValidFrom)
        .validTo(editedValidTo)
        .build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        versionableObject2,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3,
            versionableObject4));

    //then
    assertThat(result).hasSize(6);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).toList();

    VersionedObject firstVersionedObject = sortedVersionedObjects.get(0);
    assertThat(firstVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(firstVersionedObject).isNotNull();
    assertThat(firstVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 1, 1));
    assertThat(firstVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2020, 5, 31));
    Entity firstVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(firstVersionedObjectEntity).isNotNull();
    assertThat(firstVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(
        firstVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFirstVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    Assertions.assertThat(oneToManyRelationFirstVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject secondVersionedObject = sortedVersionedObjects.get(1);
    assertThat(secondVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(secondVersionedObject).isNotNull();
    assertThat(secondVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2020, 6, 1));
    assertThat(secondVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2021, 12, 31));
    Entity secondVersionedObjectEntity = secondVersionedObject.getEntity();
    assertThat(secondVersionedObjectEntity).isNotNull();
    assertThat(secondVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertySecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    Assertions.assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2022, 1, 1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2023, 12, 31));
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
    Assertions.assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject fourthVersionedObject = sortedVersionedObjects.get(3);
    assertThat(fourthVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(fourthVersionedObject).isNotNull();
    assertThat(fourthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2024, 1, 1));
    assertThat(fourthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
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
    List<Entity> oneToManyRelationEntitiesFourthVersionedObjectEntity =
        oneToManyRelationFourthVersionedObjectEntity.getOneToMany();
    assertThat(oneToManyRelationEntitiesFourthVersionedObjectEntity).isEmpty();

    VersionedObject fifthVersionedObject = sortedVersionedObjects.get(4);
    assertThat(fifthVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(fifthVersionedObject).isNotNull();
    assertThat(fifthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(fifthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2025, 6, 1));
    Entity fifthVersionedObjectEntity = fifthVersionedObject.getEntity();
    assertThat(fifthVersionedObjectEntity).isNotNull();
    assertThat(fifthVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertyFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertyFifthVersionedObjectEntity).isNotNull();
    assertThat(propertyFifthVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationFifthVersionedObjectEntity = filterProperty(
        fifthVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationFifthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    Assertions.assertThat(oneToManyRelationFifthVersionedObjectEntity.getOneToMany()).isEmpty();

    VersionedObject sixthVersionedObject = sortedVersionedObjects.get(5);
    assertThat(sixthVersionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(sixthVersionedObject).isNotNull();
    assertThat(sixthVersionedObject.getValidFrom()).isEqualTo(LocalDate.of(2025, 6, 2));
    assertThat(sixthVersionedObject.getValidTo()).isEqualTo(LocalDate.of(2025, 12, 31));
    Entity sixthVersionedObjectEntity = sixthVersionedObject.getEntity();
    assertThat(sixthVersionedObjectEntity).isNotNull();
    assertThat(sixthVersionedObjectEntity.getProperties()).hasSize(4);
    Property propertySixthVersionedObjectEntity = filterProperty(
        sixthVersionedObjectEntity.getProperties(), VersionableObject.Fields.property);
    assertThat(propertySixthVersionedObjectEntity).isNotNull();
    assertThat(propertySixthVersionedObjectEntity.getValue()).isEqualTo("Ciao4");
    Property oneToManyRelationSixthVersionedObjectEntity = filterProperty(
        sixthVersionedObjectEntity.getProperties(), VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationSixthVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    Assertions.assertThat(oneToManyRelationSixthVersionedObjectEntity.getOneToMany()).isEmpty();

  }

}
