package ch.sbb.timetable.field.number.versioning.service;

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

public class VersionableServiceScenario1Test extends VersionableServiceBaseTest {

  /**
   * Szenario 1a: Update einer bestehenden Version am Ende
   * NEU:                             |________________________________
   * IST:      |----------------------|--------------------------------
   * Version:        1                                2
   *
   * RESULTAT: |----------------------|________________________________
   * Version:        1                                2
   */
  @Test
  public void scenario1a() {
    //given
    Relation relation1 = Relation.builder().id(1L).value("value1").build();
    Relation relation2 = Relation.builder().id(2L).value("value2").build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .property("Ciao1")
        .oneToManyRelation(List.of(relation1, relation2))
        .build();

    VersionableObject editedVersion = VersionableObject.builder().property("Ciao-Ciao").build();
    Relation editedRelation = Relation.builder().id(3L).value("value-3-changed").build();
    editedVersion.setOneToManyRelation(List.of(editedRelation));

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        VersionableObject.VERSIONABLE,
        versionableObject2,
        editedVersion, Arrays.asList(versionableObject1, versionableObject2));

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(versionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(versionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity entityToChange = versionedObject.getEntity();
    assertThat(entityToChange.getId()).isEqualTo(versionableObject2.getId());
    assertThat(entityToChange.getProperties()).isNotEmpty();
    List<Property> propertiesResult = entityToChange.getProperties();
    assertThat(propertiesResult.size()).isEqualTo(2);
    Property property1Result = propertiesResult.stream()
                                               .filter(
                                                   property -> VersionableObject.Fields.property.equals(
                                                       property.getKey()))
                                               .findFirst()
                                               .orElse(null);
    assertThat(property1Result).isNotNull();
    assertThat(property1Result.getKey()).isEqualTo(VersionableObject.Fields.property);
    assertThat(property1Result.getValue()).isEqualTo("Ciao-Ciao");

    Property property2Result = propertiesResult.stream()
                                               .filter(
                                                   property -> VersionableObject.Fields.oneToManyRelation.equals(
                                                       property.getKey()))
                                               .findFirst()
                                               .orElse(null);
    assertThat(property2Result).isNotNull();
    assertThat(property2Result.getKey()).isEqualTo(VersionableObject.Fields.oneToManyRelation);
    assertThat(property2Result.hasOneToManyRelation()).isTrue();
    List<Entity> oneToManyRelation = property2Result.getOneToMany();
    assertThat(oneToManyRelation).isNotEmpty();
    assertThat(oneToManyRelation.size()).isEqualTo(1);
    Entity entityRelation = oneToManyRelation.get(0);
    assertThat(entityRelation).isNotNull();
    List<Property> entityRelationProperties = entityRelation.getProperties();
    assertThat(entityRelationProperties).isNotEmpty();
    assertThat(entityRelationProperties.size()).isEqualTo(1);
    Property entityRelationProperty = entityRelationProperties.get(0);
    assertThat(entityRelationProperty).isNotNull();
    assertThat(entityRelationProperty.getKey()).isEqualTo(Relation.Fields.value);
    assertThat(entityRelationProperty.getValue()).isEqualTo("value-3-changed");
  }

  /**
   * Szenario 1a: Update einer bestehenden Version am Ende
   * NEU:                             |________________________________
   * IST:      |----------------------|--------------------------------
   * Version:        1                                2
   *
   * RESULTAT: |----------------------|________________________________
   * Version:        1                                2
   */
  @Test
  public void scenario1aEditedValidFromAndEditedValidToAreEqualsToCurrentValidFromAndCurrentValidTo() {
    //given
    Relation relation1 = Relation.builder().id(1L).value("value1").build();
    Relation relation2 = Relation.builder().id(2L).value("value2").build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .property("Ciao1")
        .oneToManyRelation(List.of(relation1, relation2))
        .build();

    VersionableObject editedVersion = VersionableObject.builder()
                                                       .property("Ciao-Ciao")
                                                       .validFrom(LocalDate.of(2022, 1, 1))
                                                       .validTo(LocalDate.of(2023, 12, 31))
                                                       .build();
    Relation editedRelation = Relation.builder().id(3L).value("value-3-changed").build();
    editedVersion.setOneToManyRelation(List.of(editedRelation));

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        VersionableObject.VERSIONABLE,
        versionableObject2,
        editedVersion, Arrays.asList(versionableObject1, versionableObject2));

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(versionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(versionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity entityToChange = versionedObject.getEntity();
    assertThat(entityToChange.getId()).isEqualTo(versionableObject2.getId());
    assertThat(entityToChange.getProperties()).isNotEmpty();
    List<Property> propertiesResult = entityToChange.getProperties();
    assertThat(propertiesResult.size()).isEqualTo(2);
    Property property1Result = propertiesResult.stream()
                                               .filter(
                                                   property -> VersionableObject.Fields.property.equals(
                                                       property.getKey()))
                                               .findFirst()
                                               .orElse(null);
    assertThat(property1Result).isNotNull();
    assertThat(property1Result.getKey()).isEqualTo(VersionableObject.Fields.property);
    assertThat(property1Result.getValue()).isEqualTo("Ciao-Ciao");

    Property property2Result = propertiesResult.stream()
                                               .filter(
                                                   property -> VersionableObject.Fields.oneToManyRelation.equals(
                                                       property.getKey()))
                                               .findFirst()
                                               .orElse(null);
    assertThat(property2Result).isNotNull();
    assertThat(property2Result.getKey()).isEqualTo(VersionableObject.Fields.oneToManyRelation);
    assertThat(property2Result.hasOneToManyRelation()).isTrue();
    List<Entity> oneToManyRelation = property2Result.getOneToMany();
    assertThat(oneToManyRelation).isNotEmpty();
    assertThat(oneToManyRelation.size()).isEqualTo(1);
    Entity entityRelation = oneToManyRelation.get(0);
    assertThat(entityRelation).isNotNull();
    List<Property> entityRelationProperties = entityRelation.getProperties();
    assertThat(entityRelationProperties).isNotEmpty();
    assertThat(entityRelationProperties.size()).isEqualTo(1);
    Property entityRelationProperty = entityRelationProperties.get(0);
    assertThat(entityRelationProperty).isNotNull();
    assertThat(entityRelationProperty.getKey()).isEqualTo(Relation.Fields.value);
    assertThat(entityRelationProperty.getValue()).isEqualTo("value-3-changed");
  }


  /**
   * Szenario 1b: Update einer bestehenden Version in der Mitte
   * NEU:                  |______________________|
   * IST:      |-----------|----------------------|--------------------
   * Version:        1                 2                  3
   *
   * RESULTAT: |-----------|______________________|--------------------
   * Version:        1                 2                  3
   */
  @Test
  public void scenario1b() {
    //given
    VersionableObject editedVersion = VersionableObject.builder().property("Ciao-Ciao").build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        VersionableObject.VERSIONABLE, versionableObject2,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(versionedObject.getValidFrom()).isEqualTo(versionableObject2.getValidFrom());
    assertThat(versionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity entityToChange = versionedObject.getEntity();
    assertThat(entityToChange).isNotNull();
    assertThat(entityToChange.getId()).isEqualTo(versionableObject2.getId());
    assertThat(entityToChange.getProperties().size()).isEqualTo(2);
    Property propertyOneToManyRelation = entityToChange.getProperties()
                                                       .stream()
                                                       .filter(
                                                           property -> VersionableObject.Fields.oneToManyRelation.equals(
                                                               property.getKey()))
                                                       .findFirst()
                                                       .orElse(null);
    assertThat(propertyOneToManyRelation).isNotNull();
    assertThat(propertyOneToManyRelation.getValue()).isNull();
    assertThat(propertyOneToManyRelation.getOneToMany()).isEmpty();
    Property propertyProperty = entityToChange.getProperties()
                                              .stream()
                                              .filter(
                                                  property -> VersionableObject.Fields.property.equals(
                                                      property.getKey()))
                                              .findFirst()
                                              .orElse(null);
    assertThat(propertyProperty.getValue()).isEqualTo("Ciao-Ciao");
    assertThat(propertyProperty.getOneToMany()).isNull();
    assertThat(propertyProperty.getOneToOne()).isNull();
  }


  /**
   * Szenario 1c: Update einer bestehenden Version am Anfang
   *
   * NEU:       |___________|
   * IST:       |-----------|----------------------|--------------------
   * Version:         1                 2                   3
   *
   * RESULTAT: |___________|----------------------|--------------------
   * Version:        1                 2                  3
   */
  @Test
  public void scenario1c() {
    //given
    VersionableObject editedVersion = VersionableObject.builder().property("Ciao-Ciao").build();

    //when
    List<VersionedObject> result = versionableService.versioningObjects(
        VersionableObject.VERSIONABLE,
        versionableObject1,
        editedVersion,
        Arrays.asList(versionableObject1, versionableObject2, versionableObject3));

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    assertThat(versionedObject.getValidFrom()).isEqualTo(versionableObject1.getValidFrom());
    assertThat(versionedObject.getValidTo()).isEqualTo(versionableObject1.getValidTo());
    Entity entityToChange = versionedObject.getEntity();
    assertThat(entityToChange.getProperties().size()).isEqualTo(2);
    Property propertyOneToManyRelation = entityToChange.getProperties()
                                                       .stream()
                                                       .filter(
                                                           property -> VersionableObject.Fields.oneToManyRelation.equals(
                                                               property.getKey()))
                                                       .findFirst()
                                                       .orElse(null);
    assertThat(propertyOneToManyRelation).isNotNull();
    assertThat(propertyOneToManyRelation.getValue()).isNull();
    assertThat(propertyOneToManyRelation.getOneToMany()).isEmpty();
    Property propertyProperty = entityToChange.getProperties()
                                              .stream()
                                              .filter(
                                                  property -> VersionableObject.Fields.property.equals(
                                                      property.getKey()))
                                              .findFirst()
                                              .orElse(null);
    assertThat(propertyProperty.getValue()).isEqualTo("Ciao-Ciao");
    assertThat(propertyProperty.getOneToMany()).isNull();
    assertThat(propertyProperty.getOneToOne()).isNull();
  }

}