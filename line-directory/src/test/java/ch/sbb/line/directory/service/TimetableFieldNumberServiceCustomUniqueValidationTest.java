package ch.sbb.line.directory.service;

import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.exception.TimetableFieldNumberConflictException;
import ch.sbb.line.directory.repository.TimetableFieldNumberVersionRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
 class TimetableFieldNumberServiceCustomUniqueValidationTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private final TimetableFieldNumberService timetableFieldNumberService;
  private final TimetableFieldNumberVersionRepository versionRepository;

  private TimetableFieldNumberVersion version = TimetableFieldNumberVersion.builder().ttfnid("ch:1:ttfnid:100000")
                                                                                 .description("FPFN Description")
                                                                                 .number("10.100")
                                                                                 .status(Status.VALIDATED)
                                                                                 .swissTimetableFieldNumber("b0.100")
                                                                                 .validFrom(LocalDate.of(2020, 1, 1))
                                                                                 .validTo(LocalDate.of(2020, 12, 31))
                                                                                 .businessOrganisation("sbb")
                                                                                 .build();

  @Autowired
   TimetableFieldNumberServiceCustomUniqueValidationTest(
      TimetableFieldNumberService timetableFieldNumberService,
      TimetableFieldNumberVersionRepository versionRepository) {
    this.timetableFieldNumberService = timetableFieldNumberService;
    this.versionRepository = versionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    version = versionRepository.save(version);
  }

  @Test
  void shouldNotThrowConflictException() {
    // Given
    TimetableFieldNumberVersion version = TimetableFieldNumberVersion.builder()
                                                                     .ttfnid("ch:1:ttfnid:100000")
                                                                     .description("FPFN Description")
                                                                     .number("10.100")
                                                                     .status(Status.VALIDATED)
                                                                     .swissTimetableFieldNumber("b0.100")
                                                                     .validFrom(LocalDate.of(2021, 1, 1))
                                                                     .validTo(LocalDate.of(2021, 12, 31))
                                                                     .businessOrganisation("sbb")
                                                                     .build();
    // When
    Executable saveExecutable = () -> timetableFieldNumberService.save(version);
    // Then
    Assertions.assertDoesNotThrow(saveExecutable);
  }

  @Test
  void shouldThrowConflictExceptionIfNumberNotUnique() {
    // Given
    TimetableFieldNumberVersion version = TimetableFieldNumberVersion.builder()
                                                                     .ttfnid("ch:1:ttfnid:100001")
                                                                     .description("FPFN Description")
                                                                     .number("10.100")
                                                                     .status(Status.VALIDATED)
                                                                     .swissTimetableFieldNumber("b0.101")
                                                                     .validFrom(LocalDate.of(2020, 2, 1))
                                                                     .validTo(LocalDate.of(2020, 10, 1)).build();
    // When
    Executable saveExecutable = () -> timetableFieldNumberService.save(version);
    // Then
    Assertions.assertThrows(TimetableFieldNumberConflictException.class, saveExecutable);
  }

  @Test
  void shouldThrowConflictExceptionIfSttfnNotUnique() {
    // Given
    TimetableFieldNumberVersion version = TimetableFieldNumberVersion.builder()
                                                                     .ttfnid("ch:1:ttfnid:100001")
                                                                     .description("FPFN Description")
                                                                     .number("10.101")
                                                                     .status(Status.VALIDATED)
                                                                     .swissTimetableFieldNumber("B0.100")
                                                                     .validFrom(LocalDate.of(2019, 1, 1))
                                                                     .validTo(LocalDate.of(2021, 12, 31)).build();
    // When
    Executable saveExecutable = () -> timetableFieldNumberService.save(version);
    // Then
    Assertions.assertThrows(TimetableFieldNumberConflictException.class, saveExecutable);
  }

  @Test
  void shouldThrowConflictExceptionIfBothNotUnique() {
    // Given
    versionRepository.save(TimetableFieldNumberVersion.builder().ttfnid("ch:1:ttfnid:100000")
                                                      .description("FPFN Description")
                                                      .number("10.100")
                                                      .status(Status.VALIDATED)
                                                      .swissTimetableFieldNumber("b0.100")
                                                      .validFrom(LocalDate.of(2021, 1, 1))
                                                      .validTo(LocalDate.of(2021, 12, 31))
                                                      .businessOrganisation("sbb")
                                                      .build());
    TimetableFieldNumberVersion version = TimetableFieldNumberVersion.builder()
                                                                     .ttfnid("ch:1:ttfnid:100001")
                                                                     .description("FPFN Description")
                                                                     .number("10.100")
                                                                     .status(Status.VALIDATED)
                                                                     .swissTimetableFieldNumber("b0.100")
                                                                     .validFrom(LocalDate.of(2019, 1, 1))
                                                                     .validTo(LocalDate.of(2022, 12, 31)).build();
    // When
    Executable saveExecutable = () -> timetableFieldNumberService.save(version);
    // Then
    Assertions.assertThrows(TimetableFieldNumberConflictException.class, saveExecutable);
  }

  @Test
  void shouldThrowConflictExceptionIfNotUniqueAndValidityOverlap() {
    // Given
    TimetableFieldNumberVersion version = TimetableFieldNumberVersion.builder()
                                                                     .ttfnid("ch:1:ttfnid:100001")
                                                                     .description("FPFN Description")
                                                                     .number("10.100")
                                                                     .status(Status.VALIDATED)
                                                                     .swissTimetableFieldNumber("b0.101")
                                                                     .validFrom(LocalDate.of(2019, 1, 1))
                                                                     .validTo(LocalDate.of(2020, 10, 1)).build();
    // When
    Executable saveExecutable = () -> timetableFieldNumberService.save(version);
    // Then
    Assertions.assertThrows(TimetableFieldNumberConflictException.class, saveExecutable);
  }

  @Test
  void shouldNotThrowConflictExceptionWhenExistingVersionGotRevoked() {
    // Given
    version.setStatus(Status.REVOKED);

    TimetableFieldNumberVersion version = TimetableFieldNumberVersion.builder()
        .ttfnid("ch:1:ttfnid:100000")
        .description("FPFN Description")
        .number("10.100")
        .status(Status.VALIDATED)
        .swissTimetableFieldNumber("b0.100")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("sbb")
        .build();
    // When
    Executable saveExecutable = () -> timetableFieldNumberService.save(version);
    // Then
    Assertions.assertDoesNotThrow(saveExecutable);
  }

  @AfterEach
  void clearVersions() {
    versionRepository.deleteAll();
  }
}
