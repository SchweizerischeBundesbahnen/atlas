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
   void shouldReturnJustOneObjectToVersioningFound() {
    //given
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList);

    //when
    ToVersioning result = versioningData.getSingleFoundObjectToVersioning();

    //then
    assertThat(result).isNotNull().isEqualTo(toVersioningCurrent);
  }

  @Test
   void shouldReturnNullWhenNoObjectToVersioningFound() {
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
   void shouldThrowDateValidationExceptionWhenValidFromIsBiggerThanValidTo() {
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
   void shouldThrowDateValidationExceptionWhenValidFromIsBefore1700_01_01() {
    //given
    editedVersion.setValidFrom(LocalDate.of(1699, 12, 31));
    editedVersion.setValidTo(LocalDate.of(2019, 1, 2));

    //when
    assertThatThrownBy(() -> {
      new VersioningData(editedVersion, currentVersion, editedEntity,
          toVersioningList);
      //then
    }).isInstanceOf(DateValidationException.class)
      .hasMessageContaining(
          "ValidFrom cannot be before 1.1.1700.");
  }

  @Test
   void shouldThrowDateValidationExceptionWhenValidToIsAfter2099_12_31() {
    //given
    editedVersion.setValidFrom(LocalDate.of(1699, 12, 31));
    editedVersion.setValidTo(LocalDate.of(2019, 1, 2));

    //when
    assertThatThrownBy(() -> {
      new VersioningData(editedVersion, currentVersion, editedEntity,
          toVersioningList);
      //then
    }).isInstanceOf(DateValidationException.class)
      .hasMessageContaining(
          "ValidFrom cannot be before 1.1.1700.");
  }

}