package ch.sbb.timetable.field.number.versioning.version;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.timetable.field.number.BaseTest.VersionableObject;
import ch.sbb.timetable.field.number.versioning.model.Entity;
import ch.sbb.timetable.field.number.versioning.model.Property;
import ch.sbb.timetable.field.number.versioning.model.ToVersioning;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersioningHelperTest {

  @Test
  public void shouldReturnTrueIfEditedVersionIsInTheMiddleOfACurrentVersion() {
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentVersion(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseIfEditedValidToIsBiggerThenCurrentValidTo() {
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentVersion(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseIfEditedValidFromIsSmallerThenCurrentValidFrom() {
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentVersion(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseIfEditedVersionIsBeforeTheCurrentVersion() {
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentVersion(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseIfEditedVersionIsAfterTheCurrentVersion() {
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentVersion(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueIfEditedVersionExactMatchMultipleVersions() {
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
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleVersions(
        editedValidFrom, editedValidTo, objectsToVersioning);

    //then
    assertThat(result).isTrue();

  }

  @Test
  public void shouldReturnFalseIfEditedVersionIsBiggerThenMultipleVersionsFound() {
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
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleVersions(
        editedValidFrom, editedValidTo, objectsToVersioning);

    //then
    assertThat(result).isFalse();

  }

  @Test
  public void shouldReturnFalseIfEditedVersionIsSmallerThenMultipleVersionsFound() {
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
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleVersions(
        editedValidFrom, editedValidTo, objectsToVersioning);

    //then
    assertThat(result).isFalse();

  }

  @Test
  public void shouldReturnTrueIfEditedValidToIsAfterTheRightBorder() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidTo = LocalDate.of(2021, 1, 1);

    //when
    boolean result = VersioningHelper.isEditedValidToAfterTheRightBorder(editedValidTo,
        toVersioning);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseIfEditedValidToIsEqualToTheCurrentValidFrom() {
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

    //when
    boolean result = VersioningHelper.isEditedValidToAfterTheRightBorder(editedValidTo,
        toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseIfEditedValidToIsBeforeTheCurrentValidFrom() {
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

    //when
    boolean result = VersioningHelper.isEditedValidToAfterTheRightBorder(editedValidTo,
        toVersioning);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueIfVersionIsOnTheLeftBorder() {
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
    boolean result = VersioningHelper.isVersionOnTheLeftBorder(toVersioning, editedValidTo);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueIfThereIsGapBetweenVersions() {
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
  public void shouldReturnFalseIfTheVersionsAreSequential() {
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
  public void shouldReturnTrueIfOnlyValidToIsEdited() {
    //given
    VersionableObject versionableObject = VersionableObject
        .builder()
        .validTo(LocalDate.of(2020, 1, 1))
        .id(1L)
        .property("Ciao1")
        .build();
    Entity entity = Entity.builder().properties(Collections.emptyList()).build();

    //when
    boolean result = VersioningHelper.isOnlyValidToEditedWithNoEditedProperties(
        versionableObject, entity);

    //then
    assertThat(result).isTrue();

  }

  @Test
  public void shouldReturnTrueIfValidToAndPropertiesAreEdited() {
    //given
    VersionableObject versionableObject = VersionableObject
        .builder()
        .validTo(LocalDate.of(2020, 1, 1))
        .id(1L)
        .property("Ciao1")
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity entity = Entity.builder().properties(List.of(property)).build();
    //when
    boolean result = VersioningHelper.areValidToAndPropertiesEdited(
        versionableObject, entity);

    //then
    assertThat(result).isTrue();

  }

  @Test
  public void shouldFindObjectToVersioningIfEditedValidFromIsEqualToCurrentValidTo() {
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
        objectsToVersioning, editedValidFrom, editedValidTo);

    //then
    assertThat(result).isNotNull();

  }

  @Test
  public void shouldReturnTrueIfTheEditedVersionIsOnTheRightBorder() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidTo = LocalDate.of(2019, 12, 31);

    //when
    boolean result = VersioningHelper.isVersionOnTheRightBorder(
        toVersioning, editedValidTo);

    //then
    assertThat(result).isNotNull();

  }


  @Test
  public void shouldReturnTrueWhenValidFromAndValidToAreNotEdited() {
    //given
    VersionableObject edited = VersionableObject.builder().build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(current, edited);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenEditedValidFromIsEqualToCurrentValidFromAndValidToIsNotEdited() {
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validFrom(LocalDate.of(2000, 1, 1))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(current, edited);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseWhenValidFromIsNotEditedAndEditedValidToIsEqualToCurrentValidTo() {
    //given
    VersionableObject edited = VersionableObject.builder()
                                                .validTo(LocalDate.of(2000, 12, 31))
                                                .build();
    VersionableObject current = VersionableObject.builder()
                                                 .validFrom(LocalDate.of(2000, 1, 1))
                                                 .validTo(LocalDate.of(2000, 12, 31))
                                                 .build();
    //when
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(current, edited);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueWhenCurrentAndEditedValidFrom_ValidToAreEquals() {
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
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(current, edited);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenVersionIsOnOrOverTheLeftBorder() {
    //given
    VersionableObject versionableObject1 = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .property("Ciao1")
        .build();
    ToVersioning toVersioning = ToVersioning.builder().versionable(versionableObject1).build();
    LocalDate editedValidFrom = LocalDate.of(2019, 2, 1);
    LocalDate editedValidTo = LocalDate.of(2020, 12, 31);
    //when
    boolean result = VersioningHelper.isOnTheLeftBorderAndEditedValidFromIsBeforeTheLeftBorder(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenVersionIsOnOrOverTheRightBorder() {
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
    LocalDate editedValidTo = LocalDate.of(2021, 12, 31);
    //when
    boolean result = VersioningHelper.isOnTheRightBorderAndEditedEntityIsOnOrOverTheBorder(
        editedValidFrom, editedValidTo, toVersioning);

    //then
    assertThat(result).isTrue();
  }


}