package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ServicePointStatusDeciderTest {

  @Test
  void shouldUpdateStatusToValidatedOnValidityChangedToLessThan60Days() {
    ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
    servicePointVersion.setStatus(Status.DRAFT);
    servicePointVersion.setValidFrom(LocalDate.of(2010, 1, 1));
    servicePointVersion.setValidTo(LocalDate.of(2010, 3, 31));

    ServicePointVersion newServicePointVersion = servicePointVersion.toBuilder().build();
    newServicePointVersion.setValidTo(LocalDate.of(2010, 1, 1));

    Status status = ServicePointStatusDecider.getStatusForServicePoint(newServicePointVersion,
        Optional.of(servicePointVersion), List.of(servicePointVersion));
    assertThat(status).isEqualTo(Status.VALIDATED);
  }

  @Test
  void shouldUpdateStatusToDraftOnValidityChangedToMoreThan60Days() {
    ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
    servicePointVersion.setStatus(Status.VALIDATED);
    servicePointVersion.setValidFrom(LocalDate.of(2010, 1, 1));
    servicePointVersion.setValidTo(LocalDate.of(2010, 1, 31));

    ServicePointVersion newServicePointVersion = servicePointVersion.toBuilder().build();
    newServicePointVersion.setValidTo(LocalDate.of(2010, 12, 1));

    Status status = ServicePointStatusDecider.getStatusForServicePoint(newServicePointVersion,
        Optional.of(servicePointVersion), List.of(servicePointVersion));
    assertThat(status).isEqualTo(Status.DRAFT);
  }

  @Test
  void shouldUpdateStatusToValidatedOnStopPointChangedToOperatingPoint() {
    ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
    servicePointVersion.setStatus(Status.DRAFT);
    servicePointVersion.setValidFrom(LocalDate.of(2010, 1, 1));
    servicePointVersion.setValidTo(LocalDate.of(2010, 12, 31));
    assertThat(servicePointVersion.isStopPoint()).isTrue();

    ServicePointVersion newServicePointVersion = servicePointVersion.toBuilder().build();
    newServicePointVersion.setMeansOfTransport(Set.of());
    assertThat(newServicePointVersion.isStopPoint()).isFalse();

    Status status = ServicePointStatusDecider.getStatusForServicePoint(newServicePointVersion,
        Optional.of(servicePointVersion), List.of(servicePointVersion));
    assertThat(status).isEqualTo(Status.VALIDATED);
  }

  @Test
  void shouldUpdateStatusToDraftOnOperatingPointChangedToStopPoint() {
    ServicePointVersion servicePointVersion = ServicePointTestData.getBern();
    servicePointVersion.setStatus(Status.VALIDATED);
    servicePointVersion.setValidFrom(LocalDate.of(2010, 1, 1));
    servicePointVersion.setValidTo(LocalDate.of(2010, 12, 31));
    servicePointVersion.setMeansOfTransport(Set.of());
    assertThat(servicePointVersion.isStopPoint()).isFalse();

    ServicePointVersion newServicePointVersion = servicePointVersion.toBuilder().build();
    newServicePointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));
    assertThat(newServicePointVersion.isStopPoint()).isTrue();

    Status status = ServicePointStatusDecider.getStatusForServicePoint(newServicePointVersion,
        Optional.of(servicePointVersion), List.of(servicePointVersion));
    assertThat(status).isEqualTo(Status.DRAFT);
  }
}