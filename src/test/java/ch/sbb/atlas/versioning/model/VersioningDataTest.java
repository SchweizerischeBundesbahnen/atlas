package ch.sbb.atlas.versioning.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.versioning.BaseTest.VersionableObject;
import ch.sbb.atlas.versioning.exception.VersioningException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersioningDataTest {

  private VersionableObject editedVersion = VersionableObject
      .builder()
      .id(1L)
      .validFrom(LocalDate.of(2020, 1, 1))
      .validTo(LocalDate.of(2020, 12, 31))
      .build();

  private VersionableObject currentVersion = VersionableObject
      .builder()
      .id(1L)
      .validFrom(LocalDate.of(2020, 1, 1))
      .validTo(LocalDate.of(2020, 12, 31))
      .build();

  private Property property = Property.builder().value("CiaoCiao").key("property").build();
  private Entity editedEntity = Entity.builder().id(1L).properties(List.of(property)).build();
  private ToVersioning toVersioningCurrent = ToVersioning.builder()
                                                         .versionable(currentVersion)
                                                         .build();
  private List<ToVersioning> toVersioningList = Arrays.asList(toVersioningCurrent);


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
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(toVersioningCurrent);
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
  public void shouldThrowVersioningExceptionWhenValidFromIsBiggerThanValidTo() {
    //given
    editedVersion.setValidFrom(LocalDate.of(2020, 1, 2));
    editedVersion.setValidTo(LocalDate.of(2019, 1, 2));

    //when
    assertThatThrownBy(() -> {
      new VersioningData(editedVersion, currentVersion, editedEntity,
          toVersioningList);
      //then
    }).isInstanceOf(VersioningException.class)
      .hasMessageContaining(
          "Edited ValidFrom 2020-01-02 is bigger than edited ValidTo 2019-01-02");
  }

}