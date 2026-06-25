package ch.sbb.atlas.servicepointdirectory.module.servicepoint.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.StopPointTerminationNotOnLastVersionException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.TerminationAlreadyInProgressException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.TerminationNotAllowedWhenVersionInWrongStatusException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.TerminationNotStopPointException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TerminationHelperTest {

  @Test
  void shouldCheckIsStopPointTerminationWorkflowAllowed() {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.VALIDATED);
    bern.setId(11L);
    bern.setValidFrom(LocalDate.of(2023, 1, 1));
    bern.setValidTo(LocalDate.of(2025, 1, 1));

    ServicePointVersion bern2 = ServicePointTestData.getBern();
    bern2.setStatus(Status.VALIDATED);
    bern2.setId(111L);
    bern2.setValidFrom(LocalDate.of(2026, 1, 1));
    bern2.setValidTo(LocalDate.of(2026, 1, 1));
    //when
    ServicePointVersion result = ServicePointTerminationHelper.checkIsStopPointTerminationWorkflowAllowed(bern2.getSloid(),
        bern2.getId(),
        List.of(bern, bern2));
    //then
    assertThat(result).isNotNull();
  }

  @ParameterizedTest
  @EnumSource(value = Country.class, names = {"SWITZERLAND", "GERMANY_BUS", "AUSTRIA_BUS", "ITALY_BUS", "FRANCE_BUS"})
  void shouldCheckIsStopPointTerminationWorkflowAllowedOnAllowedCountries(Country country) {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.VALIDATED);
    bern.setCountry(country);
    bern.setId(11L);
    bern.setValidFrom(LocalDate.of(2023, 1, 1));
    bern.setValidTo(LocalDate.of(2025, 1, 1));

    ServicePointVersion bern2 = ServicePointTestData.getBern();
    bern2.setStatus(Status.VALIDATED);
    bern2.setCountry(country);
    bern2.setId(111L);
    bern2.setValidFrom(LocalDate.of(2026, 1, 1));
    bern2.setValidTo(LocalDate.of(2026, 1, 1));
    //when
    ServicePointVersion result = ServicePointTerminationHelper.checkIsStopPointTerminationWorkflowAllowed(bern2.getSloid(),
        bern2.getId(),
        List.of(bern, bern2));
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldNotCheckIsStopPointTerminationWorkflowAllowedWhenSelectedIdIsNotTheLast() {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.VALIDATED);
    bern.setId(11L);
    bern.setValidFrom(LocalDate.of(2023, 1, 1));
    bern.setValidTo(LocalDate.of(2025, 1, 1));

    ServicePointVersion bern2 = ServicePointTestData.getBern();
    bern2.setStatus(Status.VALIDATED);
    bern2.setId(111L);
    bern2.setValidFrom(LocalDate.of(2026, 1, 1));
    bern2.setValidTo(LocalDate.of(2026, 1, 1));

    //given & when
    List<ServicePointVersion> servicePointVersions = List.of(bern, bern2);
    String sloid = bern2.getSloid();
    Long id = bern.getId();
    assertThrows(StopPointTerminationNotOnLastVersionException.class,
        () -> ServicePointTerminationHelper.checkIsStopPointTerminationWorkflowAllowed(sloid, id,
            servicePointVersions));
  }

  @Test
  void shouldNotCheckIsStopPointTerminationWorkflowAllowedWhenSloidNotFound() {
    //given & when
    List<ServicePointVersion> servicePointVersions = List.of();
    assertThrows(SloidNotFoundException.class,
        () -> ServicePointTerminationHelper.checkIsStopPointTerminationWorkflowAllowed("ch:1:sloid:666", 1L,
            servicePointVersions));
  }

  @Test
  void shouldNotCheckIsStopPointTerminationWorkflowAllowedWhenStatusIsNotValidated() {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.DRAFT);
    bern.setId(11L);
    bern.setValidFrom(LocalDate.of(2023, 1, 1));
    bern.setValidTo(LocalDate.of(2025, 1, 1));

    ServicePointVersion bern2 = ServicePointTestData.getBern();
    bern2.setStatus(Status.DRAFT);
    bern2.setId(111L);
    bern2.setValidFrom(LocalDate.of(2026, 1, 1));
    bern2.setValidTo(LocalDate.of(2026, 1, 1));

    //given & when
    List<ServicePointVersion> servicePointVersions = List.of(bern, bern2);
    String sloid = bern2.getSloid();
    Long id = bern2.getId();
    assertThrows(TerminationNotAllowedWhenVersionInWrongStatusException.class,
        () -> ServicePointTerminationHelper.checkIsStopPointTerminationWorkflowAllowed(sloid, id, servicePointVersions));
  }

  @Test
  void shouldNotCheckIsStopPointTerminationWorkflowAllowedWhenIsNotStopPoint() {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.VALIDATED);
    bern.setId(11L);
    bern.setMeansOfTransport(Set.of());
    bern.setValidFrom(LocalDate.of(2023, 1, 1));
    bern.setValidTo(LocalDate.of(2025, 1, 1));

    ServicePointVersion bern2 = ServicePointTestData.getBern();
    bern2.setStatus(Status.VALIDATED);
    bern2.setMeansOfTransport(Set.of());
    bern2.setId(111L);
    bern2.setValidFrom(LocalDate.of(2026, 1, 1));
    bern2.setValidTo(LocalDate.of(2026, 1, 1));

    //given & when
    List<ServicePointVersion> servicePointVersions = List.of(bern, bern2);
    String sloid = bern2.getSloid();
    Long id = bern2.getId();
    assertThrows(TerminationNotStopPointException.class,
        () -> ServicePointTerminationHelper.checkIsStopPointTerminationWorkflowAllowed(sloid, id, servicePointVersions));
  }

  @Test
  void shouldNotCheckIsStopPointTerminationWhenTerminationWorkflowAllowedAlreadyInProgress() {
    //given
    ServicePointVersion bern = ServicePointTestData.getBern();
    bern.setStatus(Status.VALIDATED);
    bern.setId(11L);
    bern.setTerminationInProgress(true);
    bern.setValidFrom(LocalDate.of(2023, 1, 1));
    bern.setValidTo(LocalDate.of(2025, 1, 1));

    ServicePointVersion bern2 = ServicePointTestData.getBern();
    bern2.setStatus(Status.VALIDATED);
    bern2.setId(111L);
    bern2.setTerminationInProgress(true);
    bern2.setValidFrom(LocalDate.of(2026, 1, 1));
    bern2.setValidTo(LocalDate.of(2026, 1, 1));

    //given & when
    List<ServicePointVersion> servicePointVersions = List.of(bern, bern2);
    String sloid = bern2.getSloid();
    Long id = bern2.getId();
    assertThrows(TerminationAlreadyInProgressException.class,
        () -> ServicePointTerminationHelper.checkIsStopPointTerminationWorkflowAllowed(sloid, id, servicePointVersions));
  }

}
