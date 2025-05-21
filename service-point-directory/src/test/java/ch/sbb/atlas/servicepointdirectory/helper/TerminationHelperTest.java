package ch.sbb.atlas.servicepointdirectory.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationAlreadyInProgressException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedWhenVersionInWrongStatusException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotOnLastVersionException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class TerminationHelperTest {

  @Test
  void shouldValidateStopPointTermination() {
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
    ServicePointVersion result = TerminationHelper.validateStopPointTermination(bern2.getSloid(), bern2.getId(),
        List.of(bern, bern2));
    //then
    assertThat(result).isNotNull();
  }

  @Test
  void shouldNotValidateStopPointTerminationWhenSelectedIdIsNotTheLast() {
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
    assertThrows(TerminationNotOnLastVersionException.class,
        () -> TerminationHelper.validateStopPointTermination(bern2.getSloid(), bern.getId(), List.of(bern, bern2)));
  }

  @Test
  void shouldNotValidateStopPointTerminationWhenSloidNotFound() {
    //given & when
    assertThrows(SloidNotFoundException.class,
        () -> TerminationHelper.validateStopPointTermination("ch:1:sloid:666", 1L, List.of()));
  }

  @Test
  void shouldNotValidateStopPointTerminationWhenStatusIsNotValidated() {
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
    assertThrows(TerminationNotAllowedWhenVersionInWrongStatusException.class,
        () -> TerminationHelper.validateStopPointTermination(bern2.getSloid(), bern2.getId(), List.of(bern, bern2)));
  }

  @Test
  void shouldNotValidateStopPointTerminationWhenTerminationAlreadyInProgress() {
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
    assertThrows(TerminationAlreadyInProgressException.class,
        () -> TerminationHelper.validateStopPointTermination(bern2.getSloid(), bern2.getId(), List.of(bern, bern2)));
  }

}