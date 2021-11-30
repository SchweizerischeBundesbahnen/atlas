package ch.sbb.atlas.versioning.service;

import static ch.sbb.atlas.versioning.MergeBaseTest.MergeVersionableObject.Fields.anotherProperty;
import static ch.sbb.atlas.versioning.MergeBaseTest.MergeVersionableObject.Fields.oneToManyRelation;
import static ch.sbb.atlas.versioning.MergeBaseTest.MergeVersionableObject.Fields.property;
import static ch.sbb.atlas.versioning.MergeBaseTest.MergeVersionableObject.Fields.propertyToBeIgnored;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
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
  public void init() {
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

  }

  /**
   * Merge mit ignored property
   *
   * Änderung        |_______________________________|
   *                 |-----------------|-------------|
   *                     Version 1       Version 2
   *
   * Ergebnis: Versionen werden gemerged
   */
  @Test
  public void scenarioMergeWithIgnoredProperty() {
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
    assertThat(result.size()).isEqualTo(2);
    List<VersionedObject> sortedVersionedObjects =
        result.stream().sorted(comparing(VersionedObject::getValidFrom)).collect(toList());

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
    assertThat(propertyToBeIgnoredSecondVersionedObjectEntity.getValue()).isEqualTo(versionableObject2.getPropertyToBeIgnored());
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(
        secondVersionedObjectEntity.getProperties(), oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationSecondVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  private Property filterProperty(List<Property> properties, String fieldProperty) {
    return properties.stream().filter(property -> fieldProperty.equals(
                         property.getKey()))
                     .findFirst()
                     .orElse(null);
  }

}