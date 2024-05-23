package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.UpdateAffectsInReviewVersionException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ServicePointValidationServiceTest {

  private ServicePointValidationService servicePointValidationService;

  @Mock
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;
  @Mock
  private ServicePointVersionRepository servicePointVersionRepository;

  @BeforeEach
  void setUp() {
    servicePointValidationService = new ServicePointValidationService(sharedBusinessOrganisationService,
        servicePointVersionRepository);
  }

  @Test
  void checkInReviewVersionInsideUpdatedPeriodShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2019, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2023, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

  @Test
  void checkMultipleInReviewVersionsAffectedShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    ServicePointVersion bern2 = ServicePointTestData.getBern();
    bern2.setStatus(Status.IN_REVIEW);
    bern2.setValidFrom(LocalDate.of(2023, 1, 1));
    bern2.setValidTo(LocalDate.of(2025, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2026, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern, bern2), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(2);
  }

  @Test
  void checkNotAffectedInReviewShouldNotThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2024, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2026, 1, 1));

    assertDoesNotThrow(() -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
  }

  @Test
  void checkUpdateIsInsideInReviewVersionShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2021, 8, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

  @Test
  void checkOneInReviewIsAffectedAtLeftBorderShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2018, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2020, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

  @Test
  void checkOneInReviewAffectedAtRightBorderShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2022, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2024, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

  @Test
  void checkUpdateInsideAndTouchingLeftBorderShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2020, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2021, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

  @Test
  void checkUpdateInsideAndTouchingRightBorderShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2022, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

  @Test
  void checkOneNotInReviewAffectedShouldNotThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.VALIDATED);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2022, 1, 1));

    assertDoesNotThrow(() -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
  }

  @Test
  void checkOneInReviewIsAffectedWithLeftBorderOverlapShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2018, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2021, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

  @Test
  void checkOneInReviewIsAffectedWithRightBorderOverlapShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    CreateServicePointVersionModel updateVersion = ServicePointTestData.getAargauServicePointVersionModel();
    updateVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2023, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

}
