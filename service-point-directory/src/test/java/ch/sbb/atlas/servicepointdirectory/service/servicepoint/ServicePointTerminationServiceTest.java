package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedException;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.atlas.versioning.model.VersionedObject;
import java.time.LocalDate;
import java.util.ArrayList;
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
   * Currently StopPoint
   * |-----------------|
   * <p>
   * Update on ValidTo only to shorten
   * |-------------|
   */
  @Test
  void shouldReportTerminationNotAllowedForWriterOnEnd() {
    ServicePointVersion editedServicePoint = ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 20))
        .build();

    List<ServicePointVersion> currentVersions = new ArrayList<>(List.of(ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build()));
    List<VersionedObject> versionedObjects = new ArrayList<>(List.of(VersionedObject.builder()
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 20)).build()));

    ThrowingCallable terminationCheck = () -> servicePointTerminationService.checkTerminationAllowed(editedServicePoint,
        currentVersions, versionedObjects);
    assertThatExceptionOfType(TerminationNotAllowedException.class).isThrownBy(terminationCheck);

    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)).thenReturn(true);
    assertThatNoException().isThrownBy(terminationCheck);
  }

  /**
   * Not a StopPoint
   * |-----------------|
   * <p>
   * Update on ValidTo only to shorten
   * |-------------|
   */
  @Test
  void shouldReportNoTerminationIfNotStopPoint() {
    ServicePointVersion editedServicePoint = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 20))
        .build();

    List<ServicePointVersion> currentVersions = new ArrayList<>(List.of(ServicePointVersion.builder()
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build()));
    List<VersionedObject> versionedObjects = new ArrayList<>(List.of(VersionedObject.builder()
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 20)).build()));

    ThrowingCallable terminationCheck = () -> servicePointTerminationService.checkTerminationAllowed(editedServicePoint,
        currentVersions, versionedObjects);
    assertThatNoException().isThrownBy(terminationCheck);

    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)).thenReturn(true);
    assertThatNoException().isThrownBy(terminationCheck);
  }

  /**
   * Currently StopPoint
   * |-----------------|
   * <p>
   * Update on ValidFrom only to shorten
   *     |-------------|
   */
  @Test
  void shouldReportTerminationNotAllowedForWriterOnBeginning() {
    ServicePointVersion editedServicePoint = ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 5))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    List<ServicePointVersion> currentVersions = new ArrayList<>(List.of(ServicePointVersion.builder()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build()));
    List<VersionedObject> versionedObjects = new ArrayList<>(List.of(VersionedObject.builder()
        .validFrom(LocalDate.of(2020, 1, 5))
        .validTo(LocalDate.of(2020, 12, 31)).build()));

    ThrowingCallable terminationCheck = () -> servicePointTerminationService.checkTerminationAllowed(editedServicePoint,
        currentVersions, versionedObjects);
    assertThatExceptionOfType(TerminationNotAllowedException.class).isThrownBy(terminationCheck);

    when(userAdministrationService.isAtLeastSupervisor(ApplicationType.SEPODI)).thenReturn(true);
    assertThatNoException().isThrownBy(terminationCheck);
  }
}