package ch.sbb.timetable.field.number.versioning;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.versioning.model.ObjectProperty;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
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
  private static class VersionableObject implements Versionable {
    private LocalDate validFrom;
    private LocalDate validTo;
    private Long id;
    private String property;
  }

  private VersioningEngine versioningEngine = new VersioningEngine();

  private VersionableObject versionableObject1;
  private VersionableObject versionableObject2;
  private VersionableObject versionableObject3;

  @BeforeEach
  public void init(){
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
        .validFrom(LocalDate.of(2023, 1, 1))
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
  public void shouldJustUpdateAnObjectWhenAttributeNameIsChangedScenario1a(){
    //given
    VersionableObject editedVersion = VersionableObject.builder().property("Ciao-Ciao").build();
    List<ObjectProperty> editedObjectProperties = new ArrayList<>();
    ObjectProperty editedObjectProperty =
        ObjectProperty.builder()
                      .objectId(null)
                      .key(VersionableObject.Fields.property)
                      .value("Ciao-Ciao")
                      .build();
    editedObjectProperties.add(editedObjectProperty);

    ObjectProperty toVersioning1_ObjectProperty = buildObjectProperty(versionableObject1);
    ObjectProperty toVersioning2_ObjectProperty = buildObjectProperty(versionableObject2);

    ToVersioning toVersioning1 = getToVersioning(toVersioning1_ObjectProperty, versionableObject1);
    ToVersioning toVersioning2 = getToVersioning(toVersioning2_ObjectProperty, versionableObject2);

    //when
    List<VersionedObject> result = versioningEngine.applyVersioning(versionableObject2,
        editedVersion, editedObjectProperties, Arrays.asList(toVersioning1, toVersioning2));

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    //TODO replace me with assert on validFrom and ValidTo
    assertThat(versionedObject.getObjectProperties()).isNotEmpty();
    assertThat(versionedObject.getObjectProperties().size()).isEqualTo(1);
    ObjectProperty objectPropertyToChange = versionedObject.getObjectProperties().get(0);
    assertThat(objectPropertyToChange.getObjectId()).isEqualTo(versionableObject2.getId());
    assertThat(objectPropertyToChange.getKey()).isEqualTo(VersionableObject.Fields.property);
    assertThat(objectPropertyToChange.getValue()).isEqualTo("Ciao-Ciao");
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
  public void shouldJustUpdateAnObjectWhenAttributeNameIsChangedScenario1b(){
    //given
    VersionableObject editedVersion = VersionableObject.builder().property("Ciao-Ciao").build();
    List<ObjectProperty> editedAttributes = new ArrayList<>();
    ObjectProperty editedObjectProperty =
        ObjectProperty.builder()
                      .objectId(null)
                      .key(VersionableObject.Fields.property)
                      .value("Ciao-Ciao")
                      .build();
    editedAttributes.add(editedObjectProperty);

    ObjectProperty toVersioning1_ObjectProperty = buildObjectProperty
        (versionableObject1);
    ObjectProperty toVersioning2_ObjectProperty = buildObjectProperty(versionableObject2);
    ObjectProperty toVersioning3_ObjectProperty = buildObjectProperty(versionableObject3);

    ToVersioning toVersioning1 = getToVersioning(toVersioning1_ObjectProperty, versionableObject1);
    ToVersioning toVersioning2 = getToVersioning(toVersioning2_ObjectProperty, versionableObject2);
    ToVersioning toVersioning3 = getToVersioning(toVersioning3_ObjectProperty, versionableObject3);

    //when
    List<VersionedObject> result = versioningEngine.applyVersioning(versionableObject2,
        editedVersion, editedAttributes, Arrays.asList(toVersioning1, toVersioning2,toVersioning3));

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    //TODO replace me with assert on validFrom and ValidTo
    assertThat(versionedObject.getObjectProperties()).isNotEmpty();
    assertThat(versionedObject.getObjectProperties().size()).isEqualTo(1);
    ObjectProperty objectPropertyToChange = versionedObject.getObjectProperties().get(0);
    assertThat(objectPropertyToChange.getObjectId()).isEqualTo(versionableObject2.getId());
    assertThat(objectPropertyToChange.getKey()).isEqualTo(VersionableObject.Fields.property);
    assertThat(objectPropertyToChange.getValue()).isEqualTo("Ciao-Ciao");
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
  public void shouldJustUpdateAnObjectWhenAttributeNameIsChangedScenario1c(){
    //given
    VersionableObject editedVersion = VersionableObject.builder().property("Ciao-Ciao").build();

    List<ObjectProperty> editedAttributes = new ArrayList<>();
    ObjectProperty editedObjectProperty =
        ObjectProperty.builder()
                      .objectId(null)
                      .key(VersionableObject.Fields.property)
                      .value("Ciao-Ciao")
                      .build();
    editedAttributes.add(editedObjectProperty);

    ObjectProperty toVersioning1_ObjectProperty = buildObjectProperty(versionableObject1);
    ObjectProperty toVersioning2_ObjectProperty = buildObjectProperty(versionableObject2);
    ObjectProperty toVersioning3_ObjectProperty = buildObjectProperty(versionableObject3);

    ToVersioning toVersioning1 = getToVersioning(toVersioning1_ObjectProperty, versionableObject1);
    ToVersioning toVersioning2 = getToVersioning(toVersioning2_ObjectProperty, versionableObject2);
    ToVersioning toVersioning3 = getToVersioning(toVersioning3_ObjectProperty, versionableObject3);

    //when
    List<VersionedObject> result = versioningEngine.applyVersioning(versionableObject1,
        editedVersion, editedAttributes, Arrays.asList(toVersioning1, toVersioning2,toVersioning3));

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result.get(0)).isNotNull();
    VersionedObject versionedObject = result.get(0);
    assertThat(versionedObject.getAction()).isEqualTo(VersioningAction.UPDATE);
    //TODO replace me with assert on validFrom and ValidTo
    assertThat(versionedObject.getObjectProperties()).isNotEmpty();
    assertThat(versionedObject.getObjectProperties().size()).isEqualTo(1);
    ObjectProperty objectPropertyToChange = versionedObject.getObjectProperties().get(0);
    assertThat(objectPropertyToChange.getObjectId()).isEqualTo(versionableObject1.getId());
    assertThat(objectPropertyToChange.getKey()).isEqualTo(VersionableObject.Fields.property);
    assertThat(objectPropertyToChange.getValue()).isEqualTo("Ciao-Ciao");
  }

  private ToVersioning getToVersioning(ObjectProperty toVersioningObjectProperty,
      VersionableObject versionableObject3) {
    return ToVersioning.builder()
                       .objectId(versionableObject3.getId())
                       .versionable(versionableObject3)
                       .objectProperties(
                           Arrays.asList(toVersioningObjectProperty))
                       .build();
  }

  private ObjectProperty buildObjectProperty(VersionableObject versionableObject1) {
    return ObjectProperty.builder()
                         .objectId(versionableObject1.getId())
                         .key(VersionableObject.Fields.property)
                         .value(versionableObject1.getProperty())
                         .build();
  }
}