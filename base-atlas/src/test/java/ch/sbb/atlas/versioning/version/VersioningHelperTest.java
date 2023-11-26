package ch.sbb.atlas.versioning.version;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject;
import ch.sbb.atlas.versioning.exception.VersioningException;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.ToVersioning;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersioningHelperTest {

  private final VersionableObject editedVersion = VersionableObject
      .builder()
      .id(1L)
      .validFrom(LocalDate.of(2020, 1, 1))
      .validTo(LocalDate.of(2020, 12, 31))
      .build();

  private final VersionableObject currentVersion = VersionableObject
      .builder()
      .id(1L)
      .validFrom(LocalDate.of(2020, 1, 1))
      .validTo(LocalDate.of(2020, 12, 31))
      .build();

  private final Property property = Property.builder().value("CiaoCiao").key("property").build();
  private final Entity editedEntity = Entity.builder().id(1L).properties(List.of(property)).build();
  private final ToVersioning toVersioningCurrent = ToVersioning.builder()
                                                               .versionable(currentVersion)
                                                               .build();
  private final List<ToVersioning> toVersioningList = new ArrayList<>(List.of(toVersioningCurrent));

  @Test
   void shouldReturnTrueIfEditedVersionIsInTheMiddleOfACurrentVersion() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidFrom = LocalDate.of(2020, 2, 1);
    LocalDate editedValidTo = LocalDate.of(2020, 8, 1);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseIfEditedValidToIsBiggerThenCurrentValidTo() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidFrom = LocalDate.of(2020, 2, 1);
    LocalDate editedValidTo = LocalDate.of(2021, 1, 1);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseIfEditedValidFromIsSmallerThenCurrentValidFrom() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidFrom = LocalDate.of(2019, 11, 1);
    LocalDate editedValidTo = LocalDate.of(2020, 11, 1);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseIfEditedVersionIsBeforeTheCurrentVersion() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidFrom = LocalDate.of(2019, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2019, 12, 31);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseIfEditedVersionIsAfterTheCurrentVersion() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2021, 12, 31);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueIfEditedVersionExactMatchMultipleVersions() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao2")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    List<ToVersioning> objectsToVersioning = List.of(toVersioning1, toVersioning2);

    LocalDate editedValidFrom = LocalDate.of(2020, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2021, 12, 31);

    //when
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleEntities(
        editedValidFrom, editedValidTo, objectsToVersioning);

    //then
    assertThat(result).isTrue();

  }

  @Test
   void shouldReturnFalseIfEditedVersionIsBiggerThenMultipleVersionsFound() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao2")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    List<ToVersioning> objectsToVersioning = List.of(toVersioning1, toVersioning2);

    LocalDate editedValidFrom = LocalDate.of(2020, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2022, 1, 1);

    //when
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleEntities(
        editedValidFrom, editedValidTo, objectsToVersioning);

    //then
    assertThat(result).isFalse();

  }

  @Test
   void shouldReturnFalseIfEditedVersionIsSmallerThenMultipleVersionsFound() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao2")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    List<ToVersioning> objectsToVersioning = List.of(toVersioning1, toVersioning2);

    LocalDate editedValidFrom = LocalDate.of(2020, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2021, 12, 30);

    //when
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleEntities(
        editedValidFrom, editedValidTo, objectsToVersioning);

    //then
    assertThat(result).isFalse();

  }

  @Test
   void shouldReturnTrueIfEditedValidToIsAfterTheRightBorder() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Property emptyProperty = Property.builder().build();
    Entity entity = Entity.builder().id(1L).properties(List.of(emptyProperty)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isEditedValidToAfterTheRightBorderAndValidFromNotEdited(
        versioningData,
        toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseIfEditedValidToIsBeforeTheRightBorder() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 30))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Property emptyProperty = Property.builder().build();
    Entity entity = Entity.builder().id(1L).properties(List.of(emptyProperty)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isEditedValidToAfterTheRightBorderAndValidFromNotEdited(
        versioningData,
        toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseIfEditedValidToIsEqualToTheCurrentValidFrom() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidTo = LocalDate.of(2020, 12, 31);
    VersioningData versioningData = VersioningData.builder().editedValidTo(editedValidTo).build();

    //when
    boolean result = VersioningHelper.isEditedValidToAfterTheRightBorderAndValidFromNotEdited(
        versioningData,
        toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseIfEditedValidToIsBeforeTheCurrentValidFrom() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidTo = LocalDate.of(2020, 12, 30);
    VersioningData versioningData = VersioningData.builder().editedValidTo(editedValidTo).build();

    //when
    boolean result = VersioningHelper.isEditedValidToAfterTheRightBorderAndValidFromNotEdited(
        versioningData,
        toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueIfVersionIsOnTheLeftBorder() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidTo = LocalDate.of(2019, 12, 31);

    //when
    boolean result = VersioningHelper.isVersionOverTheLeftBorder(toVersioning, editedValidTo);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueIfThereIsGapBetweenVersions() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 2))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    List<ToVersioning> toVersioningList = List.of(toVersioning1, toVersioning2);

    //when
    boolean result = VersioningHelper.isThereGapBetweenVersions(toVersioningList);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseIfTheVersionsAreSequential() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    List<ToVersioning> toVersioningList = List.of(toVersioning1, toVersioning2);

    //when
    boolean result = VersioningHelper.isThereGapBetweenVersions(toVersioningList);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueIfOnlyValidToIsEditedEndPropertiesAreNOtEdited() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 30))
        .build();
    Entity entity = Entity.builder().id(1L).properties(List.of()).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isOnlyValidToEditedAndPropertiesAreNotEdited(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseIfOnlyValidToIsEditedEndPropertiesAreEdited() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 30))
        .build();
    Property property = Property.builder().value("asd").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isOnlyValidToEditedAndPropertiesAreNotEdited(versioningData);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueIfValidToAndPropertiesAreEdited() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 30))
        .build();
    Property property = Property.builder().key("prop").value("asd").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isOnlyValidToEditedAndPropertiesAreEdited(versioningData);

    //then
    assertThat(result).isTrue();

  }

  @Test
   void shouldFindObjectToVersioningIfEditedValidFromIsEqualToCurrentValidTo() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    List<ToVersioning> objectsToVersioning = List.of(toVersioning);
    LocalDate editedValidFrom = LocalDate.of(2021, 12, 31);
    LocalDate editedValidTo = LocalDate.of(2022, 12, 31);

    //when
    List<ToVersioning> result = VersioningHelper.findObjectToVersioningInValidFromValidToRange(
        editedValidFrom, editedValidTo, objectsToVersioning);

    //then
    assertThat(result).isNotNull();

  }

  @Test
   void shouldReturnTrueIfTheEditedVersionIsOnTheRightBorder() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidTo = LocalDate.of(2022, 12, 31);

    //when
    boolean result = VersioningHelper.isVersionOverTheRightBorder(
        toVersioning, editedValidTo);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueIfEditedVersionIsInTheMiddleOfToVersioningAndNoPropertiesAreEdited() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 11, 30))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Entity entity = Entity.builder().id(1L).properties(new ArrayList<>()).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfToVersioningAndNoPropertiesAreEdited(
        toVersioningCurrent, versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseIfEditedVersionIsOnTheLeftBorderOfToVersioningAndNoPropertiesAreEdited() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 11, 30))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Entity entity = Entity.builder().id(1L).properties(new ArrayList<>()).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfToVersioningAndNoPropertiesAreEdited(
        toVersioningCurrent, versioningData);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseIfEditedVersionIsOnTheRightBorderOfToVersioningAndNoPropertiesAreEdited() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Entity entity = Entity.builder().id(1L).properties(new ArrayList<>()).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfToVersioningAndNoPropertiesAreEdited(
        toVersioningCurrent, versioningData);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseIfEditedVersionIsOverTheRightANndTheLeftBorderOfToVersioningAndNoPropertiesAreEdited() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2019, 12, 31))
        .validTo(LocalDate.of(2021, 1, 1))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Entity entity = Entity.builder().id(1L).properties(new ArrayList<>()).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfToVersioningAndNoPropertiesAreEdited(
        toVersioningCurrent, versioningData);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseIfEditedVersionIsInTheMiddleOfToVersioningAndPropertiesAreEdited() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 11, 30))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Property property = Property.builder().key("prop").value("asd").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfToVersioningAndNoPropertiesAreEdited(
        toVersioningCurrent, versioningData);

    //then
    assertThat(result).isFalse();
  }


  @Test
   void shouldReturnTrueWhenValidFromAndValidToAreNotEdited() {
    //given
    VersionableObject edited = VersionableObject.builder().build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(edited, current);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenEditedValidFromIsEqualToCurrentValidFromAndValidToIsNotEdited() {
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 1))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(edited, current);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseWhenValidFromIsNotEditedAndEditedValidToIsEqualToCurrentValidTo() {
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validTo(LocalDate.of(2000, 12, 31))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(edited, current);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenCurrentAndEditedValidFrom_ValidToAreEquals() {
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 1))
                                                .validTo(LocalDate.of(2000, 12, 31))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(edited, current);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenEditedVersionIsBetweenMultipleVersionsAndOverTheBorders() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    VersionableObject versionableObject3 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning3 = ToVersioning.builder().versionable(versionableObject3).build();
    LocalDate editedValidFrom = LocalDate.of(2020, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2022, 6, 1);
    //when
    boolean result = VersioningHelper.isBetweenMultipleVersionsAndOverTheBorders(editedValidFrom,
        editedValidTo, List.of(toVersioning1, toVersioning2, toVersioning3));

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenEditedVersionStartsOnVersionAndOverTheBorders() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    VersionableObject versionableObject3 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning3 = ToVersioning.builder().versionable(versionableObject3).build();
    LocalDate editedValidFrom = versionableObject1.getValidFrom();
    LocalDate editedValidTo = LocalDate.of(2022, 6, 1);
    //when
    boolean result = VersioningHelper.isBetweenMultipleVersionsAndStartsOnABorder(editedValidFrom,
        editedValidTo, List.of(toVersioning1, toVersioning2, toVersioning3));

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenEditedVersionEndsOnVersionAndOverTheBorders() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    VersionableObject versionableObject3 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning3 = ToVersioning.builder().versionable(versionableObject3).build();
    LocalDate editedValidFrom = LocalDate.of(2020, 6, 1);
    LocalDate editedValidTo = versionableObject3.getValidTo();
    //when
    boolean result = VersioningHelper.isBetweenMultipleVersionsAndEndsOnABorder(editedValidFrom,
        editedValidTo, List.of(toVersioning1, toVersioning2, toVersioning3));

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenEditedVersionIsOverOneVersionAndOverTheBorders() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidFrom = LocalDate.of(2019, 6, 1);
    LocalDate editedValidTo = LocalDate.of(2021, 6, 1);
    //when
    boolean result = VersioningHelper.isBetweenMultipleVersionsAndOverTheBorders(editedValidFrom,
        editedValidTo, List.of(toVersioning1));

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenVersionIsOverTheLeftBorder() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2019, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(
        versioningData, toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenVersionIsOnTheLeftBorder() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(
        versioningData, toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseWhenVersionIsNotOverTheLeftBorder() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 2))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(
        versioningData, toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenVersionIsOnOrOverTheRightBorder() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isOnTheRightBorderAndValidToIsOnOrOverTheBorder(
        versioningData, toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenVersionIsOnBeginningOfVersionAndEndingWithin() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 7, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isOnBeginningOfVersionAndEndingWithin(
        versioningData, toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnToVersioningObjectWhenEntityIsOnAGapBetweenTwoVersions() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2021, 12, 31);
    //when
    ToVersioning result = VersioningHelper.getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions(
        editedValidFrom, editedValidTo, List.of(toVersioning1, toVersioning2));

    //then
    assertThat(result).isNotNull().isEqualTo(toVersioning1);
  }

  @Test
   void shouldReturnNullWhenEntityIsOverTheLeftGapBetweenTwoVersions() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    LocalDate editedValidFrom = LocalDate.of(2020, 12, 31);
    LocalDate editedValidTo = LocalDate.of(2021, 12, 31);
    //when
    ToVersioning result = VersioningHelper.getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions(
        editedValidFrom, editedValidTo, List.of(toVersioning1, toVersioning2));

    //then
    assertThat(result).isNull();
  }

  @Test
   void shouldReturnNullWhenEntityIsOverTheRightGapBetweenTwoVersions() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2022, 1, 1);
    //when
    ToVersioning result = VersioningHelper.getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions(
        editedValidFrom, editedValidTo, List.of(toVersioning1, toVersioning2));

    //then
    assertThat(result).isNull();
  }

  @Test
   void shouldReturnNullWhenEntityIsOverTheRightAndTheLeftGapBetweenTwoVersions() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    LocalDate editedValidFrom = LocalDate.of(2020, 12, 31);
    LocalDate editedValidTo = LocalDate.of(2022, 1, 1);
    //when
    ToVersioning result = VersioningHelper.getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions(
        editedValidFrom, editedValidTo, List.of(toVersioning1, toVersioning2));

    //then
    assertThat(result).isNull();
  }

  @Test
   void shouldReturnNullWhenThereIsNoGapBetweenTwoVersions() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 1);
    LocalDate editedValidTo = LocalDate.of(2021, 12, 31);
    //when
    ToVersioning result = VersioningHelper.getPreviouslyToVersioningObjectMatchedOnGapBetweenTwoVersions(
        editedValidFrom, editedValidTo, List.of(toVersioning1, toVersioning2));

    //then
    assertThat(result).isNull();
  }

  @Test
   void shouldReturnTrueWennIndexIsTheNextItemThatExists() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();
    //when
    boolean result = VersioningHelper.hasNextVersion(List.of(toVersioning1, toVersioning2), 0);

    //then
    assertThat(result).isTrue();

  }

  @Test
   void shouldReturnFalseWennIndexIsBiggerThenSizeOfTheList() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning1 = ToVersioning.builder().versionable(versionableObject1).build();
    VersionableObject versionableObject2 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning2 = ToVersioning.builder().versionable(versionableObject2).build();

    //when
    boolean result = VersioningHelper.hasNextVersion(List.of(toVersioning1, toVersioning2), 1);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWennOnlyValidToIsChanged() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnlyValidToChanged(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWennEditedValidToAndEditedValidFromIsChanged() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnlyValidToChanged(versioningData);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenBothValidToAndValidFromChanged() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L).
        validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.areBothValidToAndValidFromChanged(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenBothEditedValidToAndEditedValidFromChanged() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.areBothValidToAndValidFromChanged(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenBothEditedValidToAndEditedValidAreNull() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.areBothValidToAndValidFromChanged(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueEditedValidFromIsAfterCurrentValidFromAndBetweenCurrentValidTo() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 2))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isEditedValidFromAfterCurrentValidFromAndBeforeOrEqualCurrentValidTo(
        versioningData, toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWennEditedValidFromIsBeforeCurrentValidFrom() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isEditedValidFromAfterCurrentValidFromAndBeforeOrEqualCurrentValidTo(
        versioningData, toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseWennEditedValidFromIsAfterCurrentValidTo() {
    //given
    VersionableObject editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2022, 12, 31))
        .build();
    VersionableObject currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(currentVersion).build();
    List<ToVersioning> toVersioningList = new ArrayList<>();
    toVersioningList.add(toVersioningCurrent);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, entity,
        toVersioningList);
    //when
    boolean result = VersioningHelper.isEditedValidFromAfterCurrentValidFromAndBeforeOrEqualCurrentValidTo(
        versioningData, toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenEditedValidFromIsEqualToCurrentValidFrom() {
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 1);
    //when
    boolean result = VersioningHelper.isEditedValidFromExactOnTheLeftBorder(editedValidFrom,
        toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenEditedValidFromIsBiggerThanCurrentValidFrom() {
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 2);
    //when
    boolean result = VersioningHelper.isEditedValidFromExactOnTheLeftBorder(editedValidFrom,
        toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnFalseWhenEditedValidFromIsSmallerThanCurrentValidFrom() {
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2020, 12, 31);
    //when
    boolean result = VersioningHelper.isEditedValidFromExactOnTheLeftBorder(editedValidFrom,
        toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenCurrentVersionIsBetweenEditedValidFromAndEditedValidTo() {
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2020, 12, 31);
    LocalDate editedValidTo = LocalDate.of(2023, 1, 1);
    //when
    boolean result = VersioningHelper.isCurrentVersionBetweenEditedValidFromAndEditedValidTo(
        editedValidFrom, editedValidTo, toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenEditedValidFromIsOverTheLeftBorder() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    VersionableObject second = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2023, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    ToVersioning toVersioningSecond = ToVersioning.builder().versionable(second).build();
    LocalDate editedValidFrom = LocalDate.of(2019, 12, 31);
    //when
    boolean result = VersioningHelper.isEditedValidFromOverTheLeftBorder(editedValidFrom,
        List.of(toVersioningFirst, toVersioningSecond));

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenEditedValidFromIsNotOverTheLeftBorder() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    VersionableObject second = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2023, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    ToVersioning toVersioningSecond = ToVersioning.builder().versionable(second).build();
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 1);
    //when
    boolean result = VersioningHelper.isEditedValidFromOverTheLeftBorder(editedValidFrom,
        List.of(toVersioningFirst, toVersioningSecond));

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldThrowVersioningExceptionWhenToVersioningListHasLessThenTwoItems() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    List<ToVersioning> toVersioningList = List.of(
        ToVersioning.builder().versionable(first).build());
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 1);

    //when
    assertThatThrownBy(() -> {
      VersioningHelper.isEditedValidFromOverTheLeftBorder(editedValidFrom, toVersioningList);

      //then
    }).isInstanceOf(VersioningException.class)
      .hasMessageContaining(
          "toVersioningList size must be bigger than 1");
  }

  @Test
   void shouldReturnTrueWhenEditedValidToIsOverTheRightBorder() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    VersionableObject second = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2023, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    ToVersioning toVersioningSecond = ToVersioning.builder().versionable(second).build();
    LocalDate editedValidTo = LocalDate.of(2025, 1, 1);
    //when
    boolean result = VersioningHelper.isEditedValidToOverTheRightBorder(editedValidTo,
        List.of(toVersioningFirst, toVersioningSecond));

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenEditedValidToIsNotOverTheRightBorder() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    VersionableObject second = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2023, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    ToVersioning toVersioningSecond = ToVersioning.builder().versionable(second).build();
    LocalDate editedValidTo = LocalDate.of(2024, 12, 30);
    //when
    boolean result = VersioningHelper.isEditedValidToOverTheRightBorder(editedValidTo,
        List.of(toVersioningFirst, toVersioningSecond));

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldThrowVersioningExceptionWhenToVersioningListHasLessThenTwoItemsToCheckTheRightBorder() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    List<ToVersioning> toVersioningList = List.of(
        ToVersioning.builder().versionable(first).build());
    LocalDate editedValidFrom = LocalDate.of(2021, 1, 1);

    //when
    assertThatThrownBy(() -> {
      VersioningHelper.isEditedValidToOverTheRightBorder(editedValidFrom, toVersioningList);

      //then
    }).isInstanceOf(VersioningException.class)
      .hasMessageContaining(
          "toVersioningList size must be bigger than 1");
  }

  @Test
   void shouldReturnTrueWhenEditedValidToIsExactOnTheRightBorder() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    LocalDate editedValidTo = LocalDate.of(2022, 12, 31);

    //when
    boolean result = VersioningHelper.isEditedValidToExactOnTheRightBorder(
        editedValidTo, toVersioningFirst);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenEditedValidToIsNotExactOnTheRightBorder() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    LocalDate editedValidTo = LocalDate.of(2022, 12, 30);

    //when
    boolean result = VersioningHelper.isEditedValidToExactOnTheRightBorder(
        editedValidTo, toVersioningFirst);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenVersionsAreSequential() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    VersionableObject second = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2023, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    ToVersioning toVersioningSecond = ToVersioning.builder().versionable(second).build();

    //when
    boolean result = VersioningHelper.areVersionsSequential(toVersioningFirst, toVersioningSecond);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenVersionsAreSequential() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    VersionableObject second = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2023, 1, 2))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    ToVersioning toVersioningSecond = ToVersioning.builder().versionable(second).build();

    //when
    boolean result = VersioningHelper.areVersionsSequential(toVersioningFirst, toVersioningSecond);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldFoundObjectToVersioning() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity firstEntity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningFirst = ToVersioning.builder()
                                                 .versionable(first)
                                                 .entity(firstEntity)
                                                 .build();
    VersionableObject second = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2023, 1, 2))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    Entity secondEntity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningSecond = ToVersioning.builder()
                                                  .versionable(second)
                                                  .entity(secondEntity)
                                                  .build();

    //when
    ToVersioning result = VersioningHelper.findObjectToVersioning(first,
        List.of(toVersioningFirst, toVersioningSecond));

    //then
    assertThat(result).isNotNull().isEqualTo(toVersioningFirst);
  }

  @Test
   void shouldNotFoundObjectToVersioning() {
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity firstEntity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningFirst = ToVersioning.builder()
                                                 .versionable(first)
                                                 .entity(firstEntity)
                                                 .build();
    VersionableObject second = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2023, 1, 2))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    Entity secondEntity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningSecond = ToVersioning.builder()
                                                  .versionable(second)
                                                  .entity(secondEntity)
                                                  .build();

    VersionableObject toFind = VersionableObject
        .builder()
        .id(3L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    //when
    ToVersioning result = VersioningHelper.findObjectToVersioning(toFind,
        List.of(toVersioningFirst, toVersioningSecond));

    //then
    assertThat(result).isNull();
  }

  @Test
   void shouldReturnTrueOnNoPropertiesEditedAndOnlySingleVersion() {
    //given
    VersionableObject version = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    Entity entity = Entity.builder().id(1L).properties(Collections.emptyList()).build();
    ToVersioning toVersioning = ToVersioning.builder()
                                            .versionable(version)
                                            .entity(entity)
                                            .build();

    //when
    boolean result = VersioningHelper.isSingularVersionAndPropertiesAreNotEdited(
        new VersioningData(version, version, entity, new ArrayList<>(List.of(toVersioning))));

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenObjectToVersioningNotFound() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isNoObjectToVersioningFound(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenObjectToVersioningFound() {
    //given
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isNoObjectToVersioningFound(versioningData);

    //then
    assertThat(result).isFalse();
    assertThat(versioningData.getObjectToVersioningFound().isEmpty()).isFalse();
    assertThat(versioningData.getObjectToVersioningFound()).hasSize(1);
  }

  @Test
   void shouldReturnTrueWhenJustOneObjectToVersioningFound() {
    //given
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isJustOneObjectToVersioningFound(versioningData);

    //then
    assertThat(result).isTrue();
    assertThat(versioningData.getObjectToVersioningFound().isEmpty()).isFalse();
    assertThat(versioningData.getObjectToVersioningFound()).hasSize(1);
  }

  @Test
   void shouldReturnFalseWhenNoObjectToVersioningFound() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isJustOneObjectToVersioningFound(versioningData);

    //then
    assertThat(result).isFalse();
    assertThat(versioningData.getObjectToVersioningFound().isEmpty()).isTrue();
  }

  @Test
   void shouldReturnTrueWhenOnlyValidFromIsEdited() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2020, 1, 2));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnlyValidFromEdited(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenValidFromIsEditedEndEditedValidToIsEqualTOCurrentValidTo() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2020, 1, 2));
    editedVersion.setValidTo(currentVersion.getValidTo());
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnlyValidFromEdited(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenOnlyValidToIsEdited() {
    //given
    editedVersion.setValidTo(LocalDate.of(2020, 1, 2));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnlyValidToEdited(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenValidToIsEditedEndEditedValidFromIsEqualToCurrentValidFrom() {
    //given
    editedVersion.setValidTo(LocalDate.of(2020, 1, 2));
    editedVersion.setValidFrom(currentVersion.getValidFrom());
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = VersioningHelper.isOnlyValidToEdited(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenVersionIsFirstInList() {
    //given
    VersionableObject firstVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2010, 12, 31))
        .build();
    VersioningData versioningData = new VersioningData(editedVersion, firstVersion, editedEntity,
        new ArrayList<>(List.of(ToVersioning.builder().versionable(firstVersion).build(),
            toVersioningCurrent)));

    //when
    boolean result = VersioningHelper.isCurrentVersionFirstVersion(versioningData);

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenVersionIsLaterInList() {
    //given
    VersionableObject firstVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2030, 1, 1))
        .validTo(LocalDate.of(2040, 12, 31))
        .build();
    VersioningData versioningData = new VersioningData(editedVersion, firstVersion, editedEntity,
        new ArrayList<>(List.of(ToVersioning.builder().versionable(firstVersion).build(),
            toVersioningCurrent)));

    //when
    boolean result = VersioningHelper.isCurrentVersionFirstVersion(versioningData);

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenThereIsAtLeastOneNewEntity() {
    //given
    VersionedObject versionedObject1 =
        VersionedObject.builder()
                       .validFrom(LocalDate.of(2022, 1, 1))
                       .validTo(LocalDate.of(2023, 12, 31))
                       .entity(Entity.builder()
                                     .id(2L)
                                     .properties(List.of(
                                         Property.builder()
                                                 .key("property")
                                                 .value("Ciao1")
                                                 .build()))
                                     .build())
                       .build();
    VersionedObject versionedObject2 =
        VersionedObject.builder()
                       .validFrom(LocalDate.of(2023, 1, 1))
                       .validTo(LocalDate.of(2024, 12, 31))
                       .entity(Entity.builder()
                                     .properties(List.of(
                                         Property.builder()
                                                 .key("property")
                                                 .value("Ciao12")
                                                 .build()))
                                     .build())
                       .build();

    VersioningData versioningData = VersioningData.builder().build();

    //when
    boolean result = VersioningHelper.checkChangesAfterVersioning(versioningData,
        List.of(versionedObject1, versionedObject2));

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnTrueWhenThereIsAPropertyChange() {
    //given
    Entity entity1 = Entity.builder()
                           .id(2L)
                           .properties(List.of(
                               Property.builder()
                                       .key("property")
                                       .value("Ciao1")
                                       .build()))
                           .build();
    VersionedObject versionedObject1 =
        VersionedObject.builder()
                       .validFrom(LocalDate.of(2022, 1, 1))
                       .validTo(LocalDate.of(2023, 12, 31))
                       .entity(entity1)
                       .build();

    Entity entity2 = Entity.builder()
                           .id(3L)
                           .properties(List.of(
                               Property.builder()
                                       .key("property")
                                       .value("Ciao12")
                                       .build()))
                           .build();
    VersionedObject versionedObject2 =
        VersionedObject.builder()
                       .validFrom(LocalDate.of(2024, 1, 1))
                       .validTo(LocalDate.of(2025, 12, 31))
                       .entity(entity2)
                       .build();
    VersionableObject versionableObject1 = VersionableObject.builder()
                                                            .validFrom(LocalDate.of(2022, 1, 1))
                                                            .validTo(LocalDate.of(2023, 12, 31))
                                                            .id(2L)
                                                            .build();

    VersionableObject versionableObject2 = VersionableObject.builder()
                                                            .validFrom(LocalDate.of(2024, 1, 1))
                                                            .validTo(LocalDate.of(2025, 12, 31))
                                                            .id(3L)
                                                            .build();

    ToVersioning toVersioning1 = ToVersioning.builder()
                                             .versionable(versionableObject1)
                                             .entity(entity1)
                                             .build();
    Entity entityEdited = Entity.builder().id(3L).properties(
                                    List.of(
                                        Property.builder()
                                                .key("property")
                                                .value("Ciao12-edited")
                                                .build()))
                                .build();
    ToVersioning toVersioning2 = ToVersioning.builder()
                                             .versionable(versionableObject2)
                                             .entity(entityEdited)
                                             .build();

    VersioningData versioningData = VersioningData.builder()
                                                  .objectsToVersioning(
                                                      List.of(toVersioning1, toVersioning2))
                                                  .build();

    //when
    boolean result = VersioningHelper.checkChangesAfterVersioning(versioningData,
        List.of(versionedObject1, versionedObject2));

    //then
    assertThat(result).isTrue();
  }

  @Test
   void shouldReturnFalseWhenNoPropertyChange() {
    //given
    Entity entity1 = Entity.builder()
                           .id(2L)
                           .properties(List.of(
                               Property.builder()
                                       .key("property")
                                       .value("Ciao1")
                                       .build()))
                           .build();
    VersionedObject versionedObject1 =
        VersionedObject.builder()
                       .validFrom(LocalDate.of(2022, 1, 1))
                       .validTo(LocalDate.of(2023, 12, 31))
                       .entity(entity1)
                       .build();

    Entity entity2 = Entity.builder()
                           .id(3L)
                           .properties(List.of(Property.builder()
                                                       .key("property")
                                                       .value("Ciao12")
                                                       .build()))
                           .build();
    VersionedObject versionedObject2 =
        VersionedObject.builder()
                       .validFrom(LocalDate.of(2024, 1, 1))
                       .validTo(LocalDate.of(2025, 12, 31))
                       .entity(entity2)
                       .build();
    VersionableObject versionableObject1 = VersionableObject.builder()
                                                            .validFrom(LocalDate.of(2022, 1, 1))
                                                            .validTo(LocalDate.of(2023, 12, 31))
                                                            .id(2L)
                                                            .build();

    VersionableObject versionableObject2 = VersionableObject.builder()
                                                            .validFrom(LocalDate.of(2024, 1, 1))
                                                            .validTo(LocalDate.of(2025, 12, 31))
                                                            .id(3L)
                                                            .build();

    ToVersioning toVersioning1 = ToVersioning.builder()
                                             .versionable(versionableObject1)
                                             .entity(entity1)
                                             .build();
    ToVersioning toVersioning2 = ToVersioning.builder()
                                             .versionable(versionableObject2)
                                             .entity(entity2)
                                             .build();

    VersioningData versioningData = VersioningData.builder()
                                                  .objectsToVersioning(
                                                      List.of(toVersioning1, toVersioning2))
                                                  .build();

    //when
    boolean result = VersioningHelper.checkChangesAfterVersioning(versioningData,
        List.of(versionedObject1, versionedObject2));

    //then
    assertThat(result).isFalse();
  }

  @Test
   void shouldReturnTrueWhenEditedVersionIsOverTheLeftAndTheRightBorder() {
    //given
    VersionableObject version = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    VersionableObject edited = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .build();

    Entity entity = Entity.builder().id(1L).properties(Collections.emptyList()).build();
    ToVersioning toVersioning = ToVersioning.builder()
                                            .versionable(version)
                                            .entity(entity)
                                            .build();
    VersioningData versioningData = new VersioningData(edited, version, entity,
        new ArrayList<>(List.of(toVersioning)));

    //when
    boolean result = VersioningHelper.isVersionOverTheLeftAndTheRightBorder(
        versioningData);
    //then
    assertThat(result).isTrue();
  }
}