package ch.sbb.atlas.base.service.versioning.version;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.base.service.versioning.BaseTest.VersionableObject;
import ch.sbb.atlas.base.service.versioning.model.Entity;
import ch.sbb.atlas.base.service.versioning.model.Property;
import ch.sbb.atlas.base.service.versioning.model.ToVersioning;
import ch.sbb.atlas.base.service.versioning.model.VersioningData;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GapFillerTest {

  private VersionableObject editedVersion;
  private VersionableObject currentVersion;
  private VersionableObject futureVersion;
  private Entity editedEntity;

  private static List<ToVersioning> toVersioningList(VersionableObject... versionableObjects) {
    return Arrays.stream(versionableObjects)
                 .map(i -> ToVersioning.builder().versionable(i).build())
                 .collect(Collectors.toList());
  }

  @BeforeEach
  void setUp() {
    editedVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    currentVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    futureVersion = VersionableObject
        .builder()
        .id(1L)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();

    Property property = Property.builder().value("CiaoCiao").key("property").build();
    editedEntity = Entity.builder().id(1L).properties(List.of(property)).build();
  }

  /**
   * ToVersioning: |----------------------|-------------------------------|
   * Version:                 1                          2
   *
   * Resultat: No Gap -> Nothing to do
   */
  @Test
  void shouldDoNothingWhenNoGapsPresent() {
    // Given
    List<ToVersioning> initalToVersioning = toVersioningList(currentVersion, futureVersion);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        new ArrayList<>(initalToVersioning));
    // When
    GapFiller.fillGapsInToVersioning(versioningData);

    // Then
    assertThat(versioningData.getObjectsToVersioning()).isEqualTo(initalToVersioning);
  }

  /**
   * ToVersioning: |---------------|         |------------------|
   * Version:             1                          2
   *
   * Resultat: ToVersioning von Version 1 wird verlängert
   * |------------------------||------------------|
   */
  @Test
  void shouldDoFillGapByProlongingPreviousVersion() {
    // Given
    editedVersion.setValidFrom(LocalDate.of(2020, 7, 1));
    editedVersion.setValidTo(LocalDate.of(2022, 7, 31));

    futureVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    futureVersion.setValidTo(LocalDate.of(2022, 12, 31));

    List<ToVersioning> initalToVersioning = toVersioningList(currentVersion, futureVersion);
    VersioningData versioningData = new VersioningData(editedVersion, currentVersion, editedEntity,
        toVersioningList(currentVersion, futureVersion));

    // When
    GapFiller.fillGapsInToVersioning(versioningData);

    // Then
    assertThat(versioningData.getObjectsToVersioning()).isNotEqualTo(initalToVersioning);
    assertThat(versioningData.getObjectsToVersioning().get(0).getValidTo()).isEqualTo(
        futureVersion.getValidFrom().minusDays(1));

  }

}