package ch.sbb.timetable.field.number.versioning.version;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.timetable.field.number.BaseTest.VersionableObject;
import ch.sbb.timetable.field.number.versioning.exception.VersioningException;
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
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
    boolean result = VersioningHelper.isEditedVersionInTheMiddleOfCurrentEntity(
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
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleEntities(
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
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleEntities(
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
    boolean result = VersioningHelper.isEditedVersionExactMatchingMultipleEntities(
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
    boolean result = VersioningHelper.isVersionOverTheLeftBorder(toVersioning, editedValidTo);

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
    boolean result = VersioningHelper.isOnTheRightBorderAndOnlyValidToIsEditedWithNoEditedProperties(
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
    boolean result = VersioningHelper.isOnTheRightBorderAndValidToAndPropertiesAreEdited(
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
        editedValidFrom, editedValidTo, objectsToVersioning);

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
    boolean result = VersioningHelper.isVersionOverTheRightBorder(
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
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(edited, current);

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
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(edited, current);

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
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(edited, current);

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
    boolean result = VersioningHelper.areValidToAndValidFromNotEdited(edited, current);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenEditedVersionIsBetweenMultipleVersionsAndOverTheBorders() {
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
  public void shouldReturnFalseWhenEditedVersionIsOverOneVersionAndOverTheBorders() {
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

  @Test
  public void shouldReturnToVersioningObjectWhenEntityIsOnAGapBetweenTwoVersions() {
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
        editedValidFrom, editedValidTo, List.of(toVersioning1,toVersioning2));

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(toVersioning1);
  }

  @Test
  public void shouldReturnNullWhenEntityIsOverTheLeftGapBetweenTwoVersions() {
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
        editedValidFrom, editedValidTo, List.of(toVersioning1,toVersioning2));

    //then
    assertThat(result).isNull();
  }

  @Test
  public void shouldReturnNullWhenEntityIsOverTheRightGapBetweenTwoVersions() {
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
        editedValidFrom, editedValidTo, List.of(toVersioning1,toVersioning2));

    //then
    assertThat(result).isNull();
  }

  @Test
  public void shouldReturnNullWhenEntityIsOverTheRightAndTheLeftGapBetweenTwoVersions() {
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
        editedValidFrom, editedValidTo, List.of(toVersioning1,toVersioning2));

    //then
    assertThat(result).isNull();
  }

  @Test
  public void shouldReturnNullWhenThereIsNoGapBetweenTwoVersions() {
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
        editedValidFrom, editedValidTo, List.of(toVersioning1,toVersioning2));

    //then
    assertThat(result).isNull();
  }

  @Test
  public void shouldReturnTrueWennIndexIsTheNextItemThatExists(){
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
  public void shouldReturnFalseWennIndexIsBiggerThenSizeOfTheList(){
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
  public void shouldReturnTrueWennOnlyValidToIsChanged(){
    //given
    VersionableObject edited = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();

    //when
    boolean result = VersioningHelper.isOnlyValidToChanged(edited,current);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWennEditedValidToAndEditedValidFromIsChanged(){
    //given
    VersionableObject edited = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();

    //when
    boolean result = VersioningHelper.isOnlyValidToChanged(edited,current);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueWhenBothValidToAndValidFromChanged(){
    //given
    VersionableObject edited = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();

    //when
    boolean result = VersioningHelper.areBothValidToAndValidFromChanged(edited,current);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenBothEditedValidToAndEditedValidFromChanged(){
    //given
    VersionableObject edited = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();

    //when
    boolean result = VersioningHelper.areBothValidToAndValidFromChanged(edited,current);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenBothEditedValidToAndEditedValidAreNull(){
    //given
    VersionableObject edited = VersionableObject
        .builder()
        .id(1L)
        .build();
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();

    //when
    boolean result = VersioningHelper.areBothValidToAndValidFromChanged(edited,current);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueEditedValidFromIsAfterCurrentValidFromAndBetweenCurrentValidTo(){
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2021,6,1);
    //when
    boolean result = VersioningHelper.isEditedValidFromAfterCurrentValidFromAndBeforeCurrentValidTo(editedValidFrom,toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWennEditedValidFromIsBeforeCurrentValidFrom(){
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2020,6,1);
    //when
    boolean result = VersioningHelper.isEditedValidFromAfterCurrentValidFromAndBeforeCurrentValidTo(editedValidFrom,toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseWennEditedValidFromIsAfterCurrentValidTo(){
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2022,6,1);
    //when
    boolean result = VersioningHelper.isEditedValidFromAfterCurrentValidFromAndBeforeCurrentValidTo(editedValidFrom,toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueWhenEditedValidFromIsEqualToCurrentValidFrom(){
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2021,1,1);
    //when
    boolean result = VersioningHelper.isEditedValidFromExactOnTheLeftBorder(editedValidFrom,toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenEditedValidFromIsBiggerThanCurrentValidFrom(){
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2021,1,2);
    //when
    boolean result = VersioningHelper.isEditedValidFromExactOnTheLeftBorder(editedValidFrom,toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnFalseWhenEditedValidFromIsSmallerThanCurrentValidFrom(){
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2020,12,31);
    //when
    boolean result = VersioningHelper.isEditedValidFromExactOnTheLeftBorder(editedValidFrom,toVersioningCurrent);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueWhenCurrentVersionIsBetweenEditedValidFromAndEditedValidTo(){
    //given
    VersionableObject current = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningCurrent = ToVersioning.builder().versionable(current).build();
    LocalDate editedValidFrom = LocalDate.of(2020,12,31);
    LocalDate editedValidTo = LocalDate.of(2023,1,1);
    //when
    boolean result = VersioningHelper.isCurrentVersionBetweenEditedValidFromAndEditedValidTo(editedValidFrom,editedValidTo,toVersioningCurrent);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenEditedValidFromIsOverTheLeftBorder(){
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
    LocalDate editedValidFrom = LocalDate.of(2019,12,31);
    //when
    boolean result = VersioningHelper.isEditedValidFromOverTheLeftBorder(editedValidFrom,List.of(toVersioningFirst,toVersioningSecond));

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenEditedValidFromIsNotOverTheLeftBorder(){
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
    LocalDate editedValidFrom = LocalDate.of(2021,1,1);
    //when
    boolean result = VersioningHelper.isEditedValidFromOverTheLeftBorder(editedValidFrom,List.of(toVersioningFirst,toVersioningSecond));

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldThrowVersioningExceptionWhenToVersioningListHasLessThenTwoItems(){
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    LocalDate editedValidFrom = LocalDate.of(2021,1,1);

    //when
    assertThatThrownBy(() -> {
      VersioningHelper.isEditedValidFromOverTheLeftBorder(editedValidFrom,List.of(toVersioningFirst));

      //then
    }).isInstanceOf(VersioningException.class)
      .hasMessageContaining(
          "toVersioningList size must be bigger than 1");
  }

  @Test
  public void shouldReturnTrueWhenEditedValidToIsOverTheRightBorder(){
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
    LocalDate editedValidTo = LocalDate.of(2025,1,1);
    //when
    boolean result = VersioningHelper.isEditedValidToOverTheRightBorder(editedValidTo,List.of(toVersioningFirst,toVersioningSecond));

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenEditedValidToIsNotOverTheRightBorder(){
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
    LocalDate editedValidTo = LocalDate.of(2024,12,30);
    //when
    boolean result = VersioningHelper.isEditedValidToOverTheRightBorder(editedValidTo,List.of(toVersioningFirst,toVersioningSecond));

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldThrowVersioningExceptionWhenToVersioningListHasLessThenTwoItemsToCheckTheRightBorder(){
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    LocalDate editedValidFrom = LocalDate.of(2021,1,1);

    //when
    assertThatThrownBy(() -> {
      VersioningHelper.isEditedValidToOverTheRightBorder(editedValidFrom,List.of(toVersioningFirst));

      //then
    }).isInstanceOf(VersioningException.class)
      .hasMessageContaining(
          "toVersioningList size must be bigger than 1");
  }

  @Test
  public void shouldReturnTrueWhenEditedValidToIsExactOnTheRightBorder(){
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    LocalDate editedValidTo = LocalDate.of(2022,12,31);

    //when
    boolean result = VersioningHelper.isEditedValidToExactOnTheRightBorder(
        editedValidTo, toVersioningFirst);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenEditedValidToIsNotExactOnTheRightBorder(){
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).build();
    LocalDate editedValidTo = LocalDate.of(2022,12,30);

    //when
    boolean result = VersioningHelper.isEditedValidToExactOnTheRightBorder(
        editedValidTo, toVersioningFirst);

    //then
    assertThat(result).isFalse();
  }

  @Test
  public void shouldReturnTrueWhenVersionsAreSequential(){
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
  public void shouldReturnFalseWhenVersionsAreSequential(){
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
  public void shouldFoundObjectToVersioning(){
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity firstEntity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).entity(firstEntity).build();
    VersionableObject second = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2023, 1, 2))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    Entity secondEntity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningSecond = ToVersioning.builder().versionable(second).entity(secondEntity).build();

    //when
    ToVersioning result = VersioningHelper.findObjectToVersioning(first, List.of(toVersioningFirst,toVersioningSecond));

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(toVersioningFirst);
  }

  @Test
  public void shouldNotFoundObjectToVersioning(){
    //given
    VersionableObject first = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();
    Property property = Property.builder().value("CiaoCiao").key("property").build();
    Entity firstEntity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningFirst = ToVersioning.builder().versionable(first).entity(firstEntity).build();
    VersionableObject second = VersionableObject
        .builder()
        .id(2L)
        .validFrom(LocalDate.of(2023, 1, 2))
        .validTo(LocalDate.of(2024, 12, 31))
        .build();
    Entity secondEntity = Entity.builder().id(1L).properties(List.of(property)).build();
    ToVersioning toVersioningSecond = ToVersioning.builder().versionable(second).entity(secondEntity).build();

    VersionableObject toFind = VersionableObject
        .builder()
        .id(3L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    //when
    ToVersioning result = VersioningHelper.findObjectToVersioning(toFind, List.of(toVersioningFirst,toVersioningSecond));

    //then
    assertThat(result).isNull();
  }

}