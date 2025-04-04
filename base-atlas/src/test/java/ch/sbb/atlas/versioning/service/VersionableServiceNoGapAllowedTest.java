package ch.sbb.atlas.versioning.service;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import ch.sbb.atlas.versioning.exception.GapsNotAllowedException;
import ch.sbb.atlas.versioning.model.VersionedObject;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class VersionableServiceNoGapAllowedTest extends VersionableServiceBaseTest {

  @Test
  void shouldThrowExceptionOnVersionedObjectsWithGaps() {
    //given
    VersionedObject versionedObject = VersionedObject
        .builder()
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .build();
    VersionedObject versionedObject2 = VersionedObject
        .builder()
        .validFrom(LocalDate.of(2024, 1, 5))
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    List<VersionedObject> versionedObjects = List.of(versionedObject, versionedObject2);

    //when
    assertThatExceptionOfType(GapsNotAllowedException.class).isThrownBy(
        () -> versionableService.doNotAllowGaps(versionedObjects));
  }

  @Test
  void shouldNotThrowExceptionOnVersionedObjectsWithoutGaps() {
    //given
    VersionedObject versionedObject = VersionedObject
        .builder()
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2023, 12, 31))
        .build();
    VersionedObject versionedObject2 = VersionedObject
        .builder()
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2025, 12, 31))
        .build();
    List<VersionedObject> versionedObjects = List.of(versionedObject, versionedObject2);

    //when
    assertThatNoException().isThrownBy(
        () -> versionableService.doNotAllowGaps(versionedObjects));
  }

}