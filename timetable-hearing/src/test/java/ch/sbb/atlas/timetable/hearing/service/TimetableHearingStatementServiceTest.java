package ch.sbb.atlas.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.timetable.hearing.entity.StatementSender;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingStatement.TimetableHearingStatementBuilder;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.atlas.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingStatementRepository;
import ch.sbb.atlas.timetable.hearing.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class TimetableHearingStatementServiceTest {

  private static final long YEAR = 2023L;
  private static final TimetableHearingYear TIMETABLE_HEARING_YEAR = TimetableHearingYear.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2022, 1, 1))
      .hearingTo(LocalDate.of(2022, 2, 1))
      .build();

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingStatementService timetableHearingStatementService;

  @Autowired
  public TimetableHearingStatementServiceTest(TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearService timetableHearingYearService,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      TimetableHearingStatementService timetableHearingStatementService) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearService = timetableHearingYearService;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingStatementService = timetableHearingStatementService;
  }

  @AfterEach
  void tearDown() {
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldCreateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatement statement = buildStatement().build();

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.createHearingStatement(statement);

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }

  @Test
  void shouldNotCreateHearingStatementIfYearIsUnknown() {
    TimetableHearingStatement statement = buildStatement()
        .timetableYear(2020L)
        .build();

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatement(statement)).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldUpdateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatement statement = buildStatement().build();

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.createHearingStatement(statement);
    hearingStatement.setStatementStatus(StatementStatus.JUNK);

    TimetableHearingStatement updatedStatement = timetableHearingStatementService.updateHearingStatement(
        hearingStatement);

    assertThat(updatedStatement).isNotNull();
    assertThat(updatedStatement.getStatementStatus()).isEqualTo(StatementStatus.JUNK);
  }

  @Test
  void shouldNotUpdateHearingStatementIfYearIsUnknown() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatement statement = buildStatement().build();

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.createHearingStatement(statement);
    hearingStatement.setTimetableYear(2020L);

    assertThatThrownBy(() -> timetableHearingStatementService.updateHearingStatement(hearingStatement)).isInstanceOf(
        IdNotFoundException.class);
  }

  private TimetableHearingStatementBuilder<?, ?> buildStatement() {
    return TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren");
  }

}