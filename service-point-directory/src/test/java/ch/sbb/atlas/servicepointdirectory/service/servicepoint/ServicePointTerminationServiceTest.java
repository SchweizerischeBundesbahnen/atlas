package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedException;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ServicePointTerminationServiceTest {

  @Mock
  private BusinessOrganisationBasedUserAdministrationService userAdministrationService;

  private ServicePointTerminationService servicePointTerminationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    servicePointTerminationService = new ServicePointTerminationService(userAdministrationService);

    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)).thenReturn(false);
  }

  /**
   * Currently StopPoint |-----------------|
   * <p>
   * Update on ValidTo only to shorten |-------------|
   */
  @Test
  void shouldReportTerminationNotAllowedForWriterOnEnd() {
    List<ServicePointVersion> currentVersions = List.of(ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build());
    List<ServicePointVersion> afterUpdate = List.of(ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 30))
        .build());

    ThrowingCallable terminationCheck = () -> servicePointTerminationService.checkTerminationAllowed(currentVersions,
        afterUpdate);
    assertThatExceptionOfType(TerminationNotAllowedException.class).isThrownBy(terminationCheck);

    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)).thenReturn(true);
    assertThatNoException().isThrownBy(terminationCheck);
  }

  /**
   * Not a StopPoint |-----------------|
   * <p>
   * Update on ValidTo only to shorten |-------------|
   */
  @Test
  void shouldReportNoTerminationIfNotStopPoint() {
    List<ServicePointVersion> currentVersions = List.of(ServicePointVersion.builder()
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build());
    List<ServicePointVersion> afterUpdate = List.of(ServicePointVersion.builder()
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 30))
        .build());

    ThrowingCallable terminationCheck = () -> servicePointTerminationService.checkTerminationAllowed(currentVersions,
        afterUpdate);
    assertThatNoException().isThrownBy(terminationCheck);

    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)).thenReturn(true);
    assertThatNoException().isThrownBy(terminationCheck);
  }

  /**
   * Currently StopPoint |-----------------|
   * <p>
   * Update on ValidFrom only to shorten |-------------|
   */
  @Test
  void shouldReportTerminationNotAllowedForWriterOnBeginning() {
    List<ServicePointVersion> currentVersions = List.of(ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build());
    List<ServicePointVersion> afterUpdate = List.of(ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 10))
        .validTo(LocalDate.of(2020, 12, 31))
        .build());

    ThrowingCallable terminationCheck = () -> servicePointTerminationService.checkTerminationAllowed(currentVersions,
        afterUpdate);
    assertThatExceptionOfType(TerminationNotAllowedException.class).isThrownBy(terminationCheck);

    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)).thenReturn(true);
    assertThatNoException().isThrownBy(terminationCheck);
  }

  /**
   * Currently StopPoint |-----------------|
   * <p>
   * Update to not a StopPoint
   */
  @Test
  void shouldReportTerminationNotAllowedForWriterWhenSwitchingToNonStopPoint() {
    List<ServicePointVersion> currentVersions = List.of(ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build());
    List<ServicePointVersion> afterUpdate = List.of(ServicePointVersion.builder()
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build());

    ThrowingCallable terminationCheck = () -> servicePointTerminationService.checkTerminationAllowed(currentVersions,
        afterUpdate);
    assertThatExceptionOfType(TerminationNotAllowedException.class).isThrownBy(terminationCheck);

    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)).thenReturn(true);
    assertThatNoException().isThrownBy(terminationCheck);
  }
}