package ch.sbb.timetable.field.number.service;

import ch.sbb.timetable.field.number.IntegrationTest;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.exceptions.ConflictException;
import ch.sbb.timetable.field.number.repository.VersionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class VersionServiceCustomUniqueValidationTest {

  private final VersionService versionService;
  private final VersionRepository versionRepository;
  private final Version version = Version.builder().ttfnid("ch:1:ttfnid:100000")
      .name("FPFN Name")
      .number("10.100")
      .status(Status.ACTIVE)
      .swissTimetableFieldNumber("b0.100")
      .validFrom(LocalDate.of(2020, 1, 1))
      .validTo(LocalDate.of(2020, 12, 31))
      .build();

  @Autowired
  public VersionServiceCustomUniqueValidationTest(VersionService versionService,
      VersionRepository versionRepository) {
    this.versionService = versionService;
    this.versionRepository = versionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    versionRepository.save(version);
  }

  @Test
  void shouldNotThrowConflictException() {
    // Given
    Version version = Version.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .name("FPFN Name")
        .number("10.100")
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("b0.100")
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    // When
    Executable saveExecutable = () -> versionService.save(version);
    // Then
    Assertions.assertDoesNotThrow(saveExecutable);
  }

  @Test
  void shouldThrowConflictExceptionIfNumberNotUnique() {
    // Given
    Version version = Version.builder()
        .ttfnid("ch:1:ttfnid:100001")
        .name("FPFN Name")
        .number("10.100")
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("b0.101")
        .validFrom(LocalDate.of(2020, 2, 1))
        .validTo(LocalDate.of(2020, 10, 1)).build();
    // When
    Executable saveExecutable = () -> versionService.save(version);
    // Then
    Assertions.assertThrows(ConflictException.class, saveExecutable);
  }

  @Test
  void shouldThrowConflictExceptionIfSttfnNotUnique() {
    // Given
    Version version = Version.builder()
        .ttfnid("ch:1:ttfnid:100001")
        .name("FPFN Name")
        .number("10.101")
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("b0.100")
        .validFrom(LocalDate.of(2019, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31)).build();
    // When
    Executable saveExecutable = () -> versionService.save(version);
    // Then
    Assertions.assertThrows(ConflictException.class, saveExecutable);
  }

  @Test
  void shouldThrowConflictExceptionIfBothNotUnique() {
    // Given
    versionRepository.save(Version.builder().ttfnid("ch:1:ttfnid:100000")
        .name("FPFN Name")
        .number("10.100")
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("b0.100")
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build());
    Version version = Version.builder()
        .ttfnid("ch:1:ttfnid:100001")
        .name("FPFN Name")
        .number("10.100")
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("b0.100")
        .validFrom(LocalDate.of(2019, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31)).build();
    // When
    Executable saveExecutable = () -> versionService.save(version);
    // Then
    Assertions.assertThrows(ConflictException.class, saveExecutable);
  }

  @Test
  void shouldThrowConflictExceptionIfNotUniqueAndValidityOverlap() {
    // Given
    Version version = Version.builder()
        .ttfnid("ch:1:ttfnid:100001")
        .name("FPFN Name")
        .number("10.100")
        .status(Status.ACTIVE)
        .swissTimetableFieldNumber("b0.101")
        .validFrom(LocalDate.of(2019, 1, 1))
        .validTo(LocalDate.of(2020, 10, 1)).build();
    // When
    Executable saveExecutable = () -> versionService.save(version);
    // Then
    Assertions.assertThrows(ConflictException.class, saveExecutable);
  }

  @AfterEach
  void clearVersions() {
    versionRepository.deleteAll();
  }
}
