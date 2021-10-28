package ch.sbb.timetable.field.number.versioning;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.versioning.VersioningEngineTest.VersionableObject.Fields;
import ch.sbb.timetable.field.number.versioning.VersioningEngineTest.VersionableObject.Relation;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import ch.sbb.timetable.field.number.versioning.model.VersionedObject;
import ch.sbb.timetable.field.number.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VersioningEngineTest {

  @Data
  @AllArgsConstructor
  @Builder
  @FieldNameConstants
  public static class VersionableObject implements Versionable {

    public static List<VersionableProperty> VERSIONABLE = new ArrayList<>();

    static {
      VERSIONABLE.add(VersionableProperty.builder().fieldName(Fields.property).relationType(
          RelationType.NONE).build());
      VERSIONABLE.add(
          VersionableProperty.builder().fieldName(Fields.oneToManyRelation).relationType(
              RelationType.ONE_TO_MANY).relationsFields(
              List.of(Relation.Fields.id, Relation.Fields.value)
          ).build());
    }

    private LocalDate validFrom;
    private LocalDate validTo;
    private Long id;
    private String property;
    private List<Relation> oneToManyRelation;

    @Data
    @AllArgsConstructor
    @Builder
    @FieldNameConstants
    public static class Relation {

      private Long id;
      private String value;
    }
  }

  private final VersioningEngine versioningEngine = new VersioningEngine();

  private VersionableObject versionableObject1;
  private VersionableObject versionableObject2;
  private VersionableObject versionableObject3;

  @BeforeEach
  public void init() {
    versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();

    versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .property("Ciao1")
        .build();
    versionableObject3 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .property("Ciao1")
        .build();

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
    List<VersionedObject> result = versioningEngine.applyVersioning(
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
    assertThat(entityToChange .getProperties()).isNotEmpty();
    List<Property> propertiesResult = entityToChange.getProperties();
    assertThat(propertiesResult.size()).isEqualTo(2);
    Property property1Result = propertiesResult.stream()
                                         .filter(
                                             property -> Fields.property.equals(property.getKey()))
                                         .findFirst()
                                         .orElse(null);
    assertThat(property1Result).isNotNull();
    assertThat(property1Result.getKey()).isEqualTo(VersionableObject.Fields.property);
    assertThat(property1Result.getValue()).isEqualTo("Ciao-Ciao");

    Property property2Result = propertiesResult.stream()
                                         .filter(
                                             property -> Fields.oneToManyRelation.equals(property.getKey()))
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
    assertThat(entityRelation.getId()).isEqualTo(3L);
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
    List<VersionedObject> result = versioningEngine.applyVersioning(
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
                                      .filter(property -> Fields.oneToManyRelation.equals(property.getKey()))
                                      .findFirst()
                                      .orElse(null);
    assertThat(propertyOneToManyRelation).isNotNull();
    assertThat(propertyOneToManyRelation.getValue()).isNull();
    assertThat(propertyOneToManyRelation.getOneToMany()).isEmpty();
    Property propertyProperty = entityToChange.getProperties()
                                                       .stream()
                                                       .filter(property -> Fields.property.equals(property.getKey()))
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
    List<VersionedObject> result = versioningEngine.applyVersioning(
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
                                                       .filter(property -> Fields.oneToManyRelation.equals(property.getKey()))
                                                       .findFirst()
                                                       .orElse(null);
    assertThat(propertyOneToManyRelation).isNotNull();
    assertThat(propertyOneToManyRelation.getValue()).isNull();
    assertThat(propertyOneToManyRelation.getOneToMany()).isEmpty();
    Property propertyProperty = entityToChange.getProperties()
                                              .stream()
                                              .filter(property -> Fields.property.equals(property.getKey()))
                                              .findFirst()
                                              .orElse(null);
    assertThat(propertyProperty.getValue()).isEqualTo("Ciao-Ciao");
    assertThat(propertyProperty.getOneToMany()).isNull();
    assertThat(propertyProperty.getOneToOne()).isNull();
  }

  /** Which scenario 2
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
    Property propertyFirstVersionedObjectEntity = filterProperty(firstVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyFirstVersionedObjectEntity).isNotNull();
    assertThat(propertyFirstVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationFirstVersionedObjectEntity = filterProperty(firstVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
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
    Property propertySecondVersionedObjectEntity = filterProperty(secondVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertySecondVersionedObjectEntity).isNotNull();
    assertThat(propertySecondVersionedObjectEntity.getValue()).isEqualTo("Ciao-Ciao");
    Property oneToManyRelationSecondVersionedObjectEntity = filterProperty(secondVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationSecondVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    List<Entity> oneToManyRelationEntitiesSecondVersionedObjectEntity = oneToManyRelationSecondVersionedObjectEntity.getOneToMany();
    assertThat(oneToManyRelationEntitiesSecondVersionedObjectEntity).isNotEmpty();
    assertThat(oneToManyRelationEntitiesSecondVersionedObjectEntity.size()).isEqualTo(1);
    Entity lineRelationSecondVersionedObject = oneToManyRelationEntitiesSecondVersionedObjectEntity.get(0);
    assertThat(lineRelationSecondVersionedObject.getProperties()).isNotEmpty();
    assertThat(lineRelationSecondVersionedObject.getProperties().size()).isEqualTo(1);
    assertThat(lineRelationSecondVersionedObject.getProperties().get(0).getValue()).isEqualTo("first Relation");

    VersionedObject thirdVersionedObject = sortedVersionedObjects.get(2);
    assertThat(thirdVersionedObject.getAction()).isEqualTo(VersioningAction.NEW);
    assertThat(thirdVersionedObject).isNotNull();
    assertThat(thirdVersionedObject.getValidFrom()).isEqualTo(editedValidTo.plusDays(1));
    assertThat(thirdVersionedObject.getValidTo()).isEqualTo(versionableObject2.getValidTo());
    Entity thirdVersionedObjectEntity = firstVersionedObject.getEntity();
    assertThat(thirdVersionedObjectEntity).isNotNull();
    assertThat(thirdVersionedObjectEntity.getProperties().size()).isEqualTo(2);
    Property propertyThirdVersionedObjectEntity = filterProperty(thirdVersionedObjectEntity.getProperties(), Fields.property);
    assertThat(propertyThirdVersionedObjectEntity).isNotNull();
    assertThat(propertyThirdVersionedObjectEntity.getValue()).isEqualTo("Ciao1");
    Property oneToManyRelationThirdVersionedObjectEntity = filterProperty(thirdVersionedObjectEntity.getProperties(), Fields.oneToManyRelation);
    assertThat(oneToManyRelationThirdVersionedObjectEntity.hasOneToManyRelation()).isTrue();
    assertThat(oneToManyRelationThirdVersionedObjectEntity.getOneToMany()).isEmpty();

  }

  private Property filterProperty(List<Property> properties, String fieldProperty){
    return properties.stream().filter(property -> fieldProperty.equals(
                                  property.getKey()))
                              .findFirst()
                              .orElse(null);
  }

}