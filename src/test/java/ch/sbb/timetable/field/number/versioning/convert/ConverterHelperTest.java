package ch.sbb.timetable.field.number.versioning.convert;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.versioning.VersioningEngineTest;
import ch.sbb.timetable.field.number.versioning.VersioningEngineTest.VersionableObject.Relation;
import ch.sbb.timetable.field.number.versioning.convert.ConverterHelperTest.VersionableObject.Fields;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConverterHelperTest {

  private VersionableObject versionableObject1;
  private VersionableObject versionableObject2;
  private Relation relation;

  @BeforeEach
  public void init() {
    relation = Relation.builder().id(1L).value("value1").build();
    versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao")
        .oneToManyRelation(List.of(relation))
        .build();
    versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .property("Ciao2")
        .build();
  }

  @Test
  public void shouldConvertToEditedEntity() {
    //given

    //when
    Entity result = ConverterHelper.convertToEditedEntity(VersionableObject.VERSIONABLE,
        versionableObject1.getId(), versionableObject1);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    List<Property> properties = result.getProperties();
    assertThat(properties).isNotEmpty();
    assertThat(properties.size()).isEqualTo(2);
    Property propertyField = properties.stream()
                                       .filter(
                                           property -> VersionableObject.Fields.property.equals(
                                               property.getKey()))
                                       .findFirst()
                                       .orElse(null);
    assertThat(propertyField).isNotNull();
    assertThat(propertyField.getKey()).isEqualTo(
        VersioningEngineTest.VersionableObject.Fields.property);
    assertThat(propertyField.getValue()).isEqualTo("Ciao");

    Property oneToManyRelationField = properties.stream()
                                                .filter(
                                                    property -> VersionableObject.Fields.oneToManyRelation.equals(
                                                        property.getKey()))
                                                .findFirst()
                                                .orElse(null);
    assertThat(oneToManyRelationField).isNotNull();
    assertThat(oneToManyRelationField.getKey()).isEqualTo(
        VersioningEngineTest.VersionableObject.Fields.oneToManyRelation);
    assertThat(oneToManyRelationField.hasOneToManyRelation()).isTrue();
    List<Entity> oneToManyRelation = oneToManyRelationField.getOneToMany();
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
    assertThat(entityRelationProperty.getValue()).isEqualTo("value1");
  }

  @Test
  public void shouldConvertAllObjectsToVersioning() {
    //given

    //when
    List<ToVersioning> result = ConverterHelper.convertAllObjectsToVersioning(
        VersionableObject.VERSIONABLE,
        List.of(versionableObject1, versionableObject2));

    //then
    assertThat(result).isNotEmpty();
    assertThat(result.size()).isEqualTo(2);
    result.sort(Comparator.comparing(toVersioning -> toVersioning.getVersionable().getValidFrom()));
    ToVersioning firstItemToVersioning = result.get(0);
    assertThat(firstItemToVersioning).isNotNull();
    assertThat(firstItemToVersioning.getVersionable().getId()).isEqualTo(
        versionableObject1.getId());
    assertThat(firstItemToVersioning.getVersionable().getValidFrom()).isEqualTo(
        versionableObject1.getValidFrom());
    assertThat(firstItemToVersioning.getVersionable().getValidTo()).isEqualTo(
        versionableObject1.getValidTo());
    Entity entityFirstItem = firstItemToVersioning.getEntity();
    assertThat(entityFirstItem).isNotNull();
    assertThat(entityFirstItem.getId()).isEqualTo(1);
    List<Property> entityFirstItemProperties = entityFirstItem.getProperties();
    assertThat(entityFirstItemProperties).isNotEmpty();
    assertThat(entityFirstItemProperties.size()).isEqualTo(2);

    Property firstPropertyFirstItem = entityFirstItemProperties.get(0);
    assertThat(firstPropertyFirstItem).isNotNull();
    assertThat(firstPropertyFirstItem.getKey()).isEqualTo(Fields.property);
    assertThat(firstPropertyFirstItem.getValue()).isEqualTo("Ciao");
    assertThat(firstPropertyFirstItem.getOneToOne()).isNull();
    assertThat(firstPropertyFirstItem.getOneToMany()).isNull();

    Property secondPropertyFirstItem = entityFirstItemProperties.get(1);
    assertThat(secondPropertyFirstItem).isNotNull();
    assertThat(secondPropertyFirstItem.getKey()).isEqualTo(Fields.oneToManyRelation);
    assertThat(secondPropertyFirstItem.getValue()).isNull();
    assertThat(secondPropertyFirstItem.getOneToOne()).isNull();
    List<Entity> oneToManyRelation = secondPropertyFirstItem.getOneToMany();
    assertThat(oneToManyRelation).isNotEmpty();
    assertThat(oneToManyRelation.size()).isEqualTo(1);
    Entity entityOneToManyRelation = oneToManyRelation.get(0);
    assertThat(entityOneToManyRelation).isNotNull();
    List<Property> entityOneToManyRelationProperties = entityOneToManyRelation.getProperties();
    assertThat(entityOneToManyRelationProperties).isNotEmpty();
    assertThat(entityOneToManyRelationProperties.size()).isEqualTo(1);
    assertThat(entityOneToManyRelationProperties.get(0).getKey()).isEqualTo(Relation.Fields.value);
    assertThat(entityOneToManyRelationProperties.get(0).getValue()).isEqualTo("value1");
    assertThat(entityOneToManyRelationProperties.get(0).getOneToOne()).isNull();
    assertThat(entityOneToManyRelationProperties.get(0).getOneToMany()).isNull();

    ToVersioning secondItemToVersioning = result.get(1);
    assertThat(secondItemToVersioning).isNotNull();
    assertThat(secondItemToVersioning.getVersionable().getId()).isEqualTo(
        versionableObject2.getId());
    assertThat(secondItemToVersioning.getVersionable().getValidFrom()).isEqualTo(
        versionableObject2.getValidFrom());
    assertThat(secondItemToVersioning.getVersionable().getValidTo()).isEqualTo(
        versionableObject2.getValidTo());
    Entity entitySecondItem = secondItemToVersioning.getEntity();
    assertThat(entitySecondItem).isNotNull();
    assertThat(entitySecondItem.getId()).isEqualTo(2);
    List<Property> entitySecondItemProperties = entitySecondItem.getProperties();
    assertThat(entitySecondItemProperties).isNotEmpty();
    assertThat(entitySecondItemProperties.size()).isEqualTo(2);

    Property firstPropertySecondItem = entitySecondItemProperties.get(0);
    assertThat(firstPropertySecondItem).isNotNull();
    assertThat(firstPropertySecondItem.getKey()).isEqualTo(VersionableObject.Fields.property);
    assertThat(firstPropertySecondItem.getValue()).isEqualTo("Ciao2");
    assertThat(firstPropertySecondItem.getOneToOne()).isNull();
    assertThat(firstPropertySecondItem.getOneToMany()).isNull();

    Property secondPropertySecondItem = entitySecondItemProperties.get(1);
    assertThat(secondPropertySecondItem).isNotNull();
    assertThat(secondPropertySecondItem.getKey()).isEqualTo(
        VersionableObject.Fields.oneToManyRelation);
    assertThat(secondPropertySecondItem.getValue()).isNull();
    assertThat(secondPropertySecondItem.getOneToOne()).isNull();
    List<Entity> oneToManyRelationSecondItem = secondPropertySecondItem.getOneToMany();
    assertThat(oneToManyRelationSecondItem).isEmpty();
  }

  //Move to base test class
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
              List.of(VersioningEngineTest.VersionableObject.Relation.Fields.value)
          ).build());
    }

    private LocalDate validFrom;
    private LocalDate validTo;
    private Long id;
    private String property;
    private List<VersioningEngineTest.VersionableObject.Relation> oneToManyRelation;

    @Data
    @AllArgsConstructor
    @Builder
    @FieldNameConstants
    public static class Relation {

      private Long id;
      private String value;
    }
  }


}