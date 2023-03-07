package ch.sbb.line.directory.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.security.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.line.directory.LineTestData;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.line.directory.exception.ForbiddenDueToInReviewException;
import ch.sbb.line.directory.exception.LineInReviewValidationException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LineUpdateValidationServiceTest {

  @Mock
  private BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  private LineUpdateValidationService lineUpdateValidationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    lineUpdateValidationService = new LineUpdateValidationService(businessOrganisationBasedUserAdministrationService);
  }

  @Test
  void shouldNotBeAllowedToUpdateInReviewVersionAsWriterOrSuperuser() {
    when(businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)).thenReturn(false);

    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().status(Status.IN_REVIEW).build();
    LineVersion editedLineVersion = LineTestData.lineVersionBuilder().description("This is better").build();

    assertThrows(ForbiddenDueToInReviewException.class,
        () -> lineUpdateValidationService.validateLineForUpdate(currentLineVersion, editedLineVersion,
            List.of(currentLineVersion)));
  }

  @Test
  void shouldNotBeAllowedToUpdateOtherVersionOverlappingInReviewVersionAsWriterOrSuperuser() {
    when(businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)).thenReturn(false);

    LineVersion currentLineVersion1 = LineTestData.lineVersionBuilder()
        .status(Status.DRAFT)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    LineVersion currentLineVersion2 = LineTestData.lineVersionBuilder()
        .status(Status.IN_REVIEW)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    LineVersion editedLineVersion = LineTestData.lineVersionBuilder()
        .description("This is better")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();

    assertThrows(ForbiddenDueToInReviewException.class,
        () -> lineUpdateValidationService.validateLineForUpdate(currentLineVersion1, editedLineVersion,
            List.of(currentLineVersion1, currentLineVersion2)));
  }

  @Test
  void shouldBeAllowedToUpdateOtherVersionNotOverlappingInReviewVersionAsWriterOrSuperuser() {
    when(businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)).thenReturn(false);

    LineVersion currentLineVersion1 = LineTestData.lineVersionBuilder()
        .status(Status.DRAFT)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();
    LineVersion currentLineVersion2 = LineTestData.lineVersionBuilder()
        .status(Status.IN_REVIEW)
        .validFrom(LocalDate.of(2021, 1, 1))
        .validTo(LocalDate.of(2021, 12, 31))
        .build();
    LineVersion editedLineVersion = LineTestData.lineVersionBuilder()
        .description("This is better")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 7, 31))
        .build();

    assertDoesNotThrow(() -> lineUpdateValidationService.validateLineForUpdate(currentLineVersion1, editedLineVersion,
        List.of(currentLineVersion1, currentLineVersion2)));
  }

  @Test
  void shouldBeAllowedToUpdateInReviewVersionAsSupervisor() {
    when(businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)).thenReturn(true);

    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().status(Status.IN_REVIEW).build();
    LineVersion editedLineVersion = LineTestData.lineVersionBuilder().description("This is better").build();

    assertDoesNotThrow(() -> lineUpdateValidationService.validateLineForUpdate(currentLineVersion, editedLineVersion,
        List.of(currentLineVersion)));
  }

  @Test
  void shouldNotBeAllowedToUpdateValidityOrTypeInReviewVersionAsSupervisor() {
    when(businessOrganisationBasedUserAdministrationService.isAtLeastSupervisor(ApplicationType.LIDI)).thenReturn(true);

    LineVersion currentLineVersion = LineTestData.lineVersionBuilder().status(Status.IN_REVIEW).build();
    LineVersion editedLineVersion = LineTestData.lineVersionBuilder().lineType(LineType.TEMPORARY).build();

    assertThrows(LineInReviewValidationException.class,
        () -> lineUpdateValidationService.validateLineForUpdate(currentLineVersion, editedLineVersion,
            List.of(currentLineVersion)));
  }

}