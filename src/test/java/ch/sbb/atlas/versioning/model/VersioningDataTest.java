package ch.sbb.atlas.versioning.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject;
import ch.sbb.atlas.versioning.exception.DateValidationException;
import ch.sbb.atlas.versioning.exception.VersioningException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersioningDataTest {

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
  public void shouldReturnTrueWhenObjectToVersioningNotFound() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = versioningData.isNoObjectToVersioningFound();

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenObjectToVersioningFound() {
    //given
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = versioningData.isNoObjectToVersioningFound();

    //then
    assertThat(result).isFalse();
    assertThat(versioningData.getObjectToVersioningFound().isEmpty()).isFalse();
    assertThat(versioningData.getObjectToVersioningFound().size()).isEqualTo(1);
  }

  @Test
  public void shouldReturnTrueWhenJustOneObjectToVersioningFound() {
    //given
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = versioningData.isJustOneObjectToVersioningFound();

    //then
    assertThat(result).isTrue();
    assertThat(versioningData.getObjectToVersioningFound().isEmpty()).isFalse();
    assertThat(versioningData.getObjectToVersioningFound().size()).isEqualTo(1);
  }

  @Test
  public void shouldReturnFalseWhenNoObjectToVersioningFound() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = versioningData.isJustOneObjectToVersioningFound();

    //then
    assertThat(result).isFalse();
    assertThat(versioningData.getObjectToVersioningFound().isEmpty()).isTrue();
  }

  @Test
  public void shouldReturnJustOneObjectToVersioningFound() {
    //given
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    ToVersioning result = versioningData.getSingleFoundObjectToVersioning();

    //then
    assertThat(result).isNotNull().isEqualTo(toVersioningCurrent);
  }

  @Test
  public void shouldReturnNullWhenNoObjectToVersioningFound() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    editedVersion.setValidTo(LocalDate.of(2021, 12, 31));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    //then
    assertThatThrownBy(versioningData::getSingleFoundObjectToVersioning)
        .isInstanceOf(
            VersioningException.class)
        .hasMessageContaining(
            "Found more or less than one object to versioning.");
  }

  @Test
  public void shouldReturnTrueWhenOnlyValidFromIsEdited() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2020, 1, 2));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = versioningData.isOnlyValidFromEdited();

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenValidFromIsEditedEndEditedValidToIsEqualTOCurrentValidTo() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2020, 1, 2));
    editedVersion.setValidTo(currentVersion.getValidTo());
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = versioningData.isOnlyValidFromEdited();

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenOnlyValidToIsEdited() {
    //given
    editedVersion.setValidTo(LocalDate.of(2020, 1, 2));
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = versioningData.isOnlyValidToEdited();

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenValidToIsEditedEndEditedValidFromIsEqualToCurrentValidFrom() {
    //given
    editedVersion.setValidTo(LocalDate.of(2020, 1, 2));
    editedVersion.setValidFrom(currentVersion.getValidFrom());
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    boolean result = versioningData.isOnlyValidToEdited();

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldThrowDateValidationExceptionWhenValidFromIsBiggerThanValidTo() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2020, 1, 2));
    editedVersion.setValidTo(LocalDate.of(2019, 1, 2));

    //when
    assertThatThrownBy(() -> {
      new VersioningData(editedVersion, currentVersion, editedEntity,
          toVersioningList);
      //then
    }).isInstanceOf(DateValidationException.class)
      .hasMessageContaining(
          "Edited ValidFrom 2020-01-02 is bigger than edited ValidTo 2019-01-02");
  }

  @Test
  public void shouldThrowDateValidationExceptionWhenValidFromIsBefore1900_01_01() {
    //given
    editedVersion.setValidFrom(LocalDate.of(1899, 12, 31));
    editedVersion.setValidTo(LocalDate.of(2019, 1, 2));

    //when
    assertThatThrownBy(() -> {
      new VersioningData(editedVersion, currentVersion, editedEntity,
          toVersioningList);
      //then
    }).isInstanceOf(DateValidationException.class)
      .hasMessageContaining(
          "ValidFrom cannot be before 1.1.1900.");
  }

  @Test
  public void shouldThrowDateValidationExceptionWhenValidToIsAfter2099_12_31() {
    //given
    editedVersion.setValidFrom(LocalDate.of(1899, 12, 31));
    editedVersion.setValidTo(LocalDate.of(2019, 1, 2));

    //when
    assertThatThrownBy(() -> {
      new VersioningData(editedVersion, currentVersion, editedEntity,
          toVersioningList);
      //then
    }).isInstanceOf(DateValidationException.class)
      .hasMessageContaining(
          "ValidFrom cannot be before 1.1.1900.");
  }

  @Test
  public void shouldReturnTrueWhenVersionIsFirstInList() {
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
    boolean result = versioningData.isCurrentVersionFirstVersion();

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenVersionIsLaterInList() {
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
    boolean result = versioningData.isCurrentVersionFirstVersion();

    //then
    assertThat(result).isFalse();
  }
}