package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.StatementSender;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.entity.TimetableHearingStatement.TimetableHearingStatementBuilder;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.time.LocalDate;
import java.util.Collections;
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

    TimetableHearingStatement hearingStatement = timetableHearingStatementService.createHearingStatement(statement, Collections.emptyList());

    assertThat(hearingStatement).isNotNull();
    assertThat(hearingStatement.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }

  @Test
  void shouldNotCreateHearingStatementIfYearIsUnknown() {
    TimetableHearingStatement statement = buildStatement()
        .timetableYear(2020L)
        .build();

    assertThatThrownBy(() -> timetableHearingStatementService.createHearingStatement(statement, Collections.emptyList())).isInstanceOf(
        IdNotFoundException.class);
  }

  @Test
  void shouldUpdateHearingStatement() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatement statement = buildStatement().build();

    TimetableHearingStatement updatingStatement = timetableHearingStatementService.createHearingStatement(statement, Collections.emptyList());
    updatingStatement.setStatementStatus(StatementStatus.JUNK);

    TimetableHearingStatement updatedStatement = timetableHearingStatementService.updateHearingStatement(
        updatingStatement, statement, Collections.emptyList());

    assertThat(updatedStatement).isNotNull();
    assertThat(updatedStatement.getStatementStatus()).isEqualTo(StatementStatus.JUNK);
  }

  @Test
  void shouldNotUpdateHearingStatementIfYearIsUnknown() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);

    TimetableHearingStatement statement = buildStatement().build();

    TimetableHearingStatement updatingStatement = timetableHearingStatementService.createHearingStatement(statement, Collections.emptyList());
    updatingStatement.setTimetableYear(2020L);

    assertThatThrownBy(() -> timetableHearingStatementService.updateHearingStatement(updatingStatement, statement, Collections.emptyList())).isInstanceOf(
        IdNotFoundException.class);
  }

  private TimetableHearingStatementBuilder<?, ?> buildStatement() {
    return TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statementSender(StatementSender.builder()
            .email("mike@thebike.com")
            .build())
        .statement("Ich mag bitte mehr Bös fahren");
  }

}