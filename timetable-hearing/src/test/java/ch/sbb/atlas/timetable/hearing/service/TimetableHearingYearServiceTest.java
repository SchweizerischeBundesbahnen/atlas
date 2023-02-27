package ch.sbb.atlas.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.atlas.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.timetable.hearing.exception.HearingCurrentlyActiveException;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class TimetableHearingYearServiceTest {

  private static final long YEAR = 2023L;
  private static final TimetableHearingYear TIMETABLE_HEARING_YEAR = TimetableHearingYear.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2022, 1, 1))
      .hearingTo(LocalDate.of(2022, 2, 1))
      .build();

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearService timetableHearingYearService;

  @Autowired
  public TimetableHearingYearServiceTest(TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearService timetableHearingYearService) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearService = timetableHearingYearService;
  }

  @AfterEach
  void tearDown() {
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldCreateHearingYear() {
    TimetableHearingYear timetableHearing = timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    assertThat(timetableHearing.getHearingStatus()).isEqualTo(HearingStatus.PLANNED);
    assertThat(timetableHearing.isStatementCreatableExternal()).isTrue();
    assertThat(timetableHearing.isStatementCreatableInternal()).isTrue();
    assertThat(timetableHearing.isStatementEditable()).isTrue();
  }

  @Test
  void shouldGetHearingYear() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(YEAR);
    assertThat(hearingYear).isNotNull();
  }

  @Test
  void shouldStartHearingYear() {
    TimetableHearingYear timetableHearing = timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingYear startedYear = timetableHearingYearService.startTimetableHearing(timetableHearing);
    assertThat(startedYear.getHearingStatus()).isEqualTo(HearingStatus.ACTIVE);
    assertThat(startedYear.isStatementCreatableExternal()).isTrue();
  }

  @Test
  void shouldNotStartTwoHearings() {
    TimetableHearingYear timetableHearing2022 = timetableHearingYearService.createTimetableHearing(TimetableHearingYear.builder()
        .timetableYear(2022L)
        .hearingFrom(LocalDate.of(2021, 1, 1))
        .hearingTo(LocalDate.of(2021, 2, 1))
        .build());
    TimetableHearingYear timetableHearing2023 = timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingYear startedYear = timetableHearingYearService.startTimetableHearing(timetableHearing2023);
    assertThat(startedYear.getHearingStatus()).isEqualTo(HearingStatus.ACTIVE);

    assertThatThrownBy(() -> timetableHearingYearService.startTimetableHearing(timetableHearing2022)).isInstanceOf(
        HearingCurrentlyActiveException.class);
  }

  @Test
  void shouldUpdateHearingStatus() {
    TimetableHearingYear timetableHearing = timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    timetableHearing.setStatementCreatableExternal(false);

    TimetableHearingYear updatedHearing = timetableHearingYearService.updateTimetableHearingSettings(timetableHearing);
    assertThat(updatedHearing.isStatementCreatableExternal()).isFalse();
  }

  @Test
  void shouldCloseHearingStatus() {
    TimetableHearingYear timetableHearing = timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    assertThatThrownBy(() -> timetableHearingYearService.closeTimetableHearing(timetableHearing)).isInstanceOf(
        IllegalStateException.class);

    TimetableHearingYear startedTimetableHearing = timetableHearingYearService.startTimetableHearing(timetableHearing);

    TimetableHearingYear closed = timetableHearingYearService.closeTimetableHearing(startedTimetableHearing);
    assertThat(closed.getHearingStatus()).isEqualTo(HearingStatus.ARCHIVED);
  }
}