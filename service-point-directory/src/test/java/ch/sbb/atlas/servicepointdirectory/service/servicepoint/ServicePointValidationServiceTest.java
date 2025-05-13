package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.ServicePointConstants;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.InvalidFareStopException;
import ch.sbb.atlas.servicepointdirectory.exception.InvalidFreightServicePointException;
import ch.sbb.atlas.servicepointdirectory.exception.UpdateAffectsInReviewVersionException;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ServicePointValidationServiceTest {

  private ServicePointValidationService servicePointValidationService;

  @Mock
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;
  @Mock
  private ServicePointVersionRepository servicePointVersionRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    servicePointValidationService = new ServicePointValidationService(sharedBusinessOrganisationService,
        servicePointVersionRepository);
    when(servicePointVersionRepository.findDesignationLongOverlaps(any())).thenReturn(Collections.emptyList());
    when(servicePointVersionRepository.findDesignationOfficialOverlaps(any())).thenReturn(Collections.emptyList());
  }

  @Test
  void checkInReviewVersionInsideUpdatedPeriodShouldThrow() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.IN_REVIEW);
    bern.setValidFrom(LocalDate.of(2020, 1, 1));
    bern.setValidTo(LocalDate.of(2022, 1, 1));

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
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

    ServicePointVersion updateVersion = ServicePointTestData.getBern();
    updateVersion.setValidFrom(LocalDate.of(2021, 1, 1));
    updateVersion.setValidTo(LocalDate.of(2023, 1, 1));

    UpdateAffectsInReviewVersionException exception = assertThrows(
        UpdateAffectsInReviewVersionException.class,
        () -> servicePointValidationService.checkNotAffectingInReviewVersions(List.of(bern), updateVersion));
    assertThat(exception.getErrorResponse().getDetails()).hasSize(1);
  }

  @Test
  void shouldNotAllowSwissFreightServicePointWithoutSortCodeOfDestinationCode() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setValidFrom(LocalDate.now());
    bern.setValidTo(LocalDate.now());
    bern.setFreightServicePoint(true);
    bern.setSortCodeOfDestinationStation(null);

    assertThrows(InvalidFreightServicePointException.class,
        () -> servicePointValidationService.validateSortCodeOfDestinationStationOnFreightServicePoint(bern));
  }

  @Test
  void shouldAllowSwissFreightServicePointWithSortCodeOfDestinationCode() {
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setValidFrom(LocalDate.now());
    bern.setValidTo(LocalDate.now());
    bern.setFreightServicePoint(true);
    bern.setSortCodeOfDestinationStation("code");

    assertDoesNotThrow(() -> servicePointValidationService.validateSortCodeOfDestinationStationOnFreightServicePoint(bern));
  }

  @Test
  void shouldThrowExceptionOnFareStopNotBelongingToASP() {
    ServicePointVersion servicePointVersion = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8589108))
        .sloid("ch:1:sloid:89108")
        .numberShort(89108)
        .country(Country.SWITZERLAND)
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
        .designationOfficial("Tarifhaltestelle")
        .businessOrganisation("ch:1:sboid:100626")
        .validFrom(LocalDate.of(2025, 12, 14))
        .validTo(LocalDate.of(2026, 3, 31))
        .build();
    assertThrows(InvalidFareStopException.class,
        () -> servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion));
  }

  @Test
  void shouldNotThrowExceptionOnFareStopNotBelongingToAspBeforeAtlasMigration() {
    ServicePointVersion servicePointVersion = ServicePointVersion
        .builder()
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(8589108))
        .sloid("ch:1:sloid:89108")
        .numberShort(89108)
        .country(Country.SWITZERLAND)
        .operatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT)
        .designationOfficial("Tarifhaltestelle")
        .businessOrganisation("ch:1:sboid:100626")
        .validFrom(ServicePointConstants.ATLAS_MIGRATION_DATE.minusDays(5))
        .validTo(ServicePointConstants.ATLAS_MIGRATION_DATE.minusDays(1))
        .build();
    assertThatNoException().isThrownBy(
        () -> servicePointValidationService.validateServicePointPreconditionBusinessRule(servicePointVersion));
  }

}
