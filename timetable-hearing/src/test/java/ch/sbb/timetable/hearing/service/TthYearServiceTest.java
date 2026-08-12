package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.client.line.workflow.TimetableHearingYearApiInternalClient;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.timetable.hearing.entity.TthDossier;
import ch.sbb.timetable.hearing.entity.TthDossierYear;
import ch.sbb.timetable.hearing.repository.TthDossierRepository;
import ch.sbb.timetable.hearing.repository.TthDossierYearRepository;
import feign.FeignException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class TthYearServiceTest {

  @MockitoBean
  private TimetableHearingYearApiInternalClient timetableHearingYearApiInternalClient;

  private final TthYearService tthYearService;
  private final TthDossierRepository tthDossierRepository;
  private final TthDossierYearRepository tthDossierYearRepository;

  @Autowired
  TthYearServiceTest(TthYearService tthYearService, TthDossierRepository tthDossierRepository,
      TthDossierYearRepository tthDossierYearRepository) {
    this.tthYearService = tthYearService;
    this.tthDossierRepository = tthDossierRepository;
    this.tthDossierYearRepository = tthDossierYearRepository;
  }

  @AfterEach
  void tearDown() {
    tthDossierRepository.deleteAll();
  }

  @Test
  void shouldRollbackDossierChangesWhenCloseTimetableHearingInLidiFails() {
    // given
    TthDossierYear tthDossierYear = TthDossierYear.builder()
        .timetableYear(2026L)
        .hearingStatus(HearingStatus.ACTIVE)
        .build();
    tthDossierYearRepository.save(tthDossierYear);

    long dossierId = tthDossierRepository.save(TthDossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("topic")
        .dossierStatus(DossierStatus.ADDED)
        .statementIds(List.of(1L, 3L))
        .tthDossierYear(tthDossierYear)
        .build()).getId();
    doThrow(FeignException.class).when(timetableHearingYearApiInternalClient).closeTimetableHearing(anyLong(), anyList());
    // when
    assertThrows(FeignException.class, () -> tthYearService.closeTimetableHearingYear(2026L));
    // then
    assertThat(tthDossierRepository.findById(dossierId).get().getDossierStatus()).isEqualTo(DossierStatus.ADDED);
  }

  @Test
  void shouldUpdateDossierStatusAndSendRequestToLidiOnCloseYearCorrectly() {
    // given
    TthDossierYear tthDossierYear = TthDossierYear.builder()
        .timetableYear(2026L)
        .hearingStatus(HearingStatus.ACTIVE)
        .build();
    tthDossierYearRepository.save(tthDossierYear);

    long dossierId = tthDossierRepository.save(TthDossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("topic")
        .dossierStatus(DossierStatus.ADDED)
        .statementIds(List.of(1L, 3L))
        .tthDossierYear(tthDossierYear)
        .build()).getId();
    when(timetableHearingYearApiInternalClient.closeTimetableHearing(anyLong(), anyList())).thenReturn(null);
    // when
    tthYearService.closeTimetableHearingYear(2026L);
    // then
    assertThat(tthDossierRepository.findById(dossierId).get().getDossierStatus()).isEqualTo(DossierStatus.CANCELED);
    verify(timetableHearingYearApiInternalClient).closeTimetableHearing(eq(2026L),
        assertArg(list -> assertThat(list).containsExactlyInAnyOrder(1L, 3L)));
  }

  @Test
  void shouldAddTthDossierYearAndSendRequestToLidiOnStartYearCorrectly() {
    // given
    when(timetableHearingYearApiInternalClient.startHearingYear(anyLong())).thenReturn(null);
    // when
    tthYearService.startTimetableHearingYear(2028L);
    // then
    assertThat(tthDossierYearRepository.findById(2028L).get().getHearingStatus()).isEqualTo(HearingStatus.ACTIVE);
    verify(timetableHearingYearApiInternalClient).startHearingYear(2028L);
  }

  @Test
  void shouldRollbackTthDossierYearWhenStartTimetableHearingInLidiFails() {
    doThrow(FeignException.class).when(timetableHearingYearApiInternalClient).startHearingYear(anyLong());
    // when
    assertThrows(FeignException.class, () -> tthYearService.startTimetableHearingYear(2029L));
    // then
    assertThat(tthDossierYearRepository.findById(2029L)).isEmpty();
  }

  @Test
  void shouldUpdateTimeTableHearingYearToArchive() {
    // given
    TthDossierYear tthDossierYear = TthDossierYear.builder()
        .timetableYear(2028L)
        .hearingStatus(HearingStatus.ACTIVE)
        .build();
    tthDossierYearRepository.save(tthDossierYear);
    // when
    tthYearService.updateDossierYearStatusToArchive(2028L);
    // then
    TthDossierYear savedTthDossierYear = tthDossierYearRepository.findById(2028L).orElseThrow();
    assertThat(savedTthDossierYear.getTimetableYear()).isEqualTo(2028L);
    assertThat(savedTthDossierYear.getHearingStatus()).isEqualTo(HearingStatus.ARCHIVED);
  }
}
