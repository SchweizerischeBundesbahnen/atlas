package ch.sbb.line.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementAlternatingModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementAlternationService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

@IntegrationTest
class TimetableHearingStatementAlternationServiceTest {

  private static final long YEAR = 2023L;
  private static final TimetableHearingYear TIMETABLE_HEARING_YEAR = TimetableHearingYear.builder()
      .timetableYear(YEAR)
      .hearingFrom(LocalDate.of(2022, 1, 1))
      .hearingTo(LocalDate.of(2022, 2, 1))
      .build();

  private static final TimetableHearingStatementRequestParams STATEMENT_REQUEST_PARAMS =
      TimetableHearingStatementRequestParams.builder()
          .canton(SwissCanton.BERN)
          .timetableHearingYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
          .build();
  private static final Pageable PAGEABLE = Pageable.ofSize(1);

  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingYearService timetableHearingYearService;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;
  private final TimetableHearingStatementService timetableHearingStatementService;
  private final TimetableHearingStatementAlternationService timetableHearingStatementAlternationService;

  private TimetableHearingStatementModelV2 statement1;
  private TimetableHearingStatementModelV2 statement2;
  private TimetableHearingStatementModelV2 statement3;

  @Autowired
  TimetableHearingStatementAlternationServiceTest(TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingYearService timetableHearingYearService,
      TimetableHearingStatementRepository timetableHearingStatementRepository,
      TimetableHearingStatementService timetableHearingStatementService,
      TimetableHearingStatementAlternationService timetableHearingStatementAlternationService) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingYearService = timetableHearingYearService;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
    this.timetableHearingStatementService = timetableHearingStatementService;
    this.timetableHearingStatementAlternationService = timetableHearingStatementAlternationService;
  }

  @BeforeEach
  void setUp() {
    timetableHearingYearService.createTimetableHearing(TIMETABLE_HEARING_YEAR);
    TimetableHearingStatementModelV2 model1 = TimetableHearingStatementModelV2.builder()
        .timetableYear(TIMETABLE_HEARING_YEAR.getTimetableYear())
        .swissCanton(SwissCanton.BERN)
        .statement("Statement 1")
        .statementSender(TimetableHearingStatementSenderModelV2.builder()
            .emails(Set.of("fabienne.mueller@sbb.ch"))
            .build())
        .build();

    // Statement 1
    statement1 = timetableHearingStatementService.createHearingStatementV2(model1, Collections.emptyList());

    // Statement 2
    model1.setStatement("Statement 2");
    statement2 = timetableHearingStatementService.createHearingStatementV2(model1, Collections.emptyList());

    // Statement 3
    model1.setStatement("Statement 3");
    statement3 = timetableHearingStatementService.createHearingStatementV2(model1, Collections.emptyList());
  }

  @AfterEach
  void tearDown() {
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldFindNextStatementOfTwo() {
    TimetableHearingStatementAlternatingModel statementAlternation = timetableHearingStatementAlternationService.getNextStatement(
        statement2.getId(), PAGEABLE, STATEMENT_REQUEST_PARAMS);
    assertThat(statementAlternation.getTimetableHearingStatement().getStatement()).isEqualTo("Statement 3");
    assertThat(statementAlternation.getPageable().getPageNumber()).isEqualTo(2);
  }

  @Test
  void shouldFindNextStatementOfThree() {
    TimetableHearingStatementAlternatingModel statementAlternation = timetableHearingStatementAlternationService.getNextStatement(
        statement3.getId(), PAGEABLE, STATEMENT_REQUEST_PARAMS);
    assertThat(statementAlternation.getTimetableHearingStatement().getStatement()).isEqualTo("Statement 1");
    assertThat(statementAlternation.getPageable().getPageNumber()).isZero();
  }

  @Test
  void shouldFindPreviousStatementOfTwo() {
    TimetableHearingStatementAlternatingModel statementAlternation = timetableHearingStatementAlternationService.getPreviousStatement(
        statement2.getId(), PAGEABLE, STATEMENT_REQUEST_PARAMS);
    assertThat(statementAlternation.getTimetableHearingStatement().getStatement()).isEqualTo("Statement 1");
    assertThat(statementAlternation.getPageable().getPageNumber()).isZero();
  }

  @Test
  void shouldFindPreviousStatementOfOne() {
    TimetableHearingStatementAlternatingModel statementAlternation = timetableHearingStatementAlternationService.getPreviousStatement(
        statement1.getId(), PAGEABLE, STATEMENT_REQUEST_PARAMS);
    assertThat(statementAlternation.getTimetableHearingStatement().getStatement()).isEqualTo("Statement 3");
    assertThat(statementAlternation.getPageable().getPageNumber()).isEqualTo(2);
  }

}
