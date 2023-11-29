package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.exception.HearingCurrentlyActiveException;
import ch.sbb.line.directory.exception.NoHearingCurrentlyActiveException;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapper;
import ch.sbb.line.directory.model.TimetableHearingYearSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
 class TimetableHearingYearServiceTest {

  private static final long YEAR = 2023L;
  private static final TimetableHearingYear TIMETABLE_HEARING_YEAR = TimetableHearingYear.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2022, 1, 1))
      .hearingTo(LocalDate.of(2022, 2, 1))
      .build();

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingStatementMapper timetableHearingStatementMapper;

  @Autowired
   TimetableHearingYearServiceTest(TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearService timetableHearingYearService,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      TimetableHearingStatementMapper timetableHearingStatementMapper) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearService = timetableHearingYearService;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingStatementMapper = timetableHearingStatementMapper;
  }

  private static TimetableHearingStatementModel buildTimetableHearingStatementModel() {
    return TimetableHearingStatementModel.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(TimetableHearingStatementSenderModel.builder()
            .email("fabienne.mueller@sbb.ch")
            .build())
        .statement("Ich hätte gerne mehrere Verbindungen am Abend.")
        .build();
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
  void shouldNotGetHearingYear() {

    assertThatThrownBy(timetableHearingYearService::getActiveHearingYear).isInstanceOf(
        NoHearingCurrentlyActiveException.class);
  }

  @Test
  void shouldGetHearingYearByStaus() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    List<TimetableHearingYear> hearingYears =
        timetableHearingYearService.getHearingYears(TimetableHearingYearSearchRestrictions.builder()
            .statusRestrictions(Set.of(HearingStatus.PLANNED))
            .build());
    assertThat(hearingYears).hasSize(1);

    hearingYears =
        timetableHearingYearService.getHearingYears(TimetableHearingYearSearchRestrictions.builder()
            .statusRestrictions(Set.of(HearingStatus.ACTIVE))
            .build());
    assertThat(hearingYears.size()).isZero();
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

    TimetableHearingYear updatedHearing = timetableHearingYearService.updateTimetableHearingSettings(
        timetableHearing.getTimetableYear(),
        timetableHearing);
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

  @Test
  void shouldCloseTimetableHearingWithCorrectStatementAndYearUpdates() {
    // given
    TimetableHearingYear timetableHearing = timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingYear startedTimetableHearing = timetableHearingYearService.startTimetableHearing(timetableHearing);

    TimetableHearingStatementModel statementModel;
    // Junk Statement
    statementModel = buildTimetableHearingStatementModel();
    statementModel.setStatementStatus(StatementStatus.JUNK);
    timetableHearingStatementRepository.save(timetableHearingStatementMapper.toEntity(statementModel));

    // Statement to move to next year and to update status
    statementModel = buildTimetableHearingStatementModel();
    statementModel.setStatementStatus(StatementStatus.MOVED);
    timetableHearingStatementRepository.save(timetableHearingStatementMapper.toEntity(statementModel));

    // when
    TimetableHearingYear closed = timetableHearingYearService.closeTimetableHearing(startedTimetableHearing);

    // then
    assertThat(closed.getHearingStatus()).isEqualTo(HearingStatus.ARCHIVED);
    assertThat(closed.isStatementCreatableInternal()).isFalse();
    assertThat(closed.isStatementCreatableExternal()).isFalse();
    assertThat(closed.isStatementEditable()).isFalse();

    Stream<TimetableHearingStatement> resultStream = timetableHearingStatementRepository.findAll().stream();
    assertTrue(resultStream.noneMatch(resultStatement ->
        resultStatement.getStatementStatus() == StatementStatus.JUNK || resultStatement.getTimetableYear() == YEAR));
  }
}
