package ch.sbb.atlas.base.service.versioning.convert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.base.service.versioning.BaseTest;
import ch.sbb.atlas.base.service.versioning.BaseTest.VersionableObject.Fields;
import ch.sbb.atlas.base.service.versioning.BaseTest.VersionableObject.Relation;
import ch.sbb.atlas.base.service.versioning.exception.VersioningException;
import ch.sbb.atlas.base.service.versioning.model.Entity;
import ch.sbb.atlas.base.service.versioning.model.Property;
import ch.sbb.atlas.base.service.versioning.model.ToVersioning;
import ch.sbb.atlas.base.service.versioning.model.VersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.VersionableProperty.RelationType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ConverterHelperTest extends BaseTest {

  public static List<VersionableProperty> VERSIONABLE = new ArrayList<>();

  static {
    VERSIONABLE.add(VersionableProperty.builder()
        .fieldName(VersionableObject.Fields.property)
        .relationType(RelationType.NONE)
        .build());
    VERSIONABLE.add(VersionableProperty.builder()
        .fieldName(
            VersionableObject.Fields.oneToManyRelation)
        .relationType(RelationType.ONE_TO_MANY)
        .relationsFields(List.of(Relation.Fields.value))
        .build());
    VERSIONABLE.add(VersionableProperty.builder()
        .fieldName(
            Fields.oneToOneRelation)
        .relationType(RelationType.ONE_TO_ONE)
        .relationsFields(List.of(Relation.Fields.value))
        .build());
  }

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
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao")
        .oneToManyRelation(List.of(relation))
        .build();
    VersionableObject edited = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .property("Ciao2")
        .build();

    //when
    Entity result = ConverterHelper.convertToEditedEntity(current, edited, VERSIONABLE);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    List<Property> properties = result.getProperties();
    assertThat(properties).isNotEmpty();
    assertThat(properties.size()).isEqualTo(1);
    Property propertyField = properties.stream()
        .filter(
            property -> VersionableObject.Fields.property.equals(
                property.getKey()))
        .findFirst()
        .orElse(null);
    assertThat(propertyField).isNotNull();
    assertThat(propertyField.getKey()).isEqualTo(
        VersionableObject.Fields.property);
    assertThat(propertyField.getValue()).isEqualTo("Ciao2");

  }

  @Test
  public void shouldReturnConvertedEntityWithEmptyPropertiesWhenCurrentANdEditedPropertiesAreEquals() {

    //when
    Entity result = ConverterHelper.convertToEditedEntity(versionableObject1, versionableObject1,
        VERSIONABLE
    );

    //then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    List<Property> properties = result.getProperties();
    assertThat(properties).isEmpty();
  }

  @Test
  public void shouldConvertAllObjectsToVersioning() {
    //given

    //when
    List<ToVersioning> result = ConverterHelper.convertAllObjectsToVersioning(
        List.of(versionableObject1, versionableObject2), VERSIONABLE
    );

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
    assertThat(firstPropertyFirstItem.getKey()).isEqualTo(VersionableObject.Fields.property);
    assertThat(firstPropertyFirstItem.getValue()).isEqualTo("Ciao");
    assertThat(firstPropertyFirstItem.getOneToOne()).isNull();
    assertThat(firstPropertyFirstItem.getOneToMany()).isNull();

    Property secondPropertyFirstItem = entityFirstItemProperties.get(1);
    assertThat(secondPropertyFirstItem).isNotNull();
    assertThat(secondPropertyFirstItem.getKey()).isEqualTo(
        VersionableObject.Fields.oneToManyRelation);
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

  @Test
  public void shouldConvertEntityOneToOneRelation() {
    //given
    versionableObject2.setOneToOneRelation(relation);

    //when
    List<ToVersioning> result = ConverterHelper.convertAllObjectsToVersioning(
        List.of(versionableObject1, versionableObject2), VERSIONABLE
    );

    //then
    assertThat(result).isNotEmpty();

  }

  @Test
  public void shouldThrowExceptionWhenTryToUsePropertyNotDefinedAsVersionable() {
    //given
    List<VersionableProperty> versionable = new ArrayList<>();

    versionable.add(VersionableProperty.builder()
        .fieldName("not_defined")
        .relationType(RelationType.NONE)
        .build());
    //when

    assertThatThrownBy(() -> {
      ConverterHelper.convertAllObjectsToVersioning(
          List.of(versionableObject1, versionableObject2), versionable
      );
      //then
    }).isInstanceOf(VersioningException.class)
        .hasMessageContaining("Error during parse field not_defined");

  }

  @Test
  public void shouldThrowExceptionWhenTryToUsePropertyOnOneToManyRelationNotDefinedAsVersionable() {
    //given
    List<VersionableProperty> versionable = new ArrayList<>();

    versionable.add(VersionableProperty.builder()
        .fieldName(
            VersionableObject.Fields.oneToManyRelation)
        .relationType(RelationType.ONE_TO_MANY)
        .relationsFields(List.of("not_defined"))
        .build());
    //when

    assertThatThrownBy(() -> {
      ConverterHelper.convertAllObjectsToVersioning(
          List.of(versionableObject1, versionableObject2), versionable
      );
      //then
    }).isInstanceOf(VersioningException.class)
        .hasMessageContaining("Error during parse field not_defined");

  }

}