package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.StatementSender;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.timetable.hearing.repository.DossierRepository;
import ch.sbb.timetable.hearing.repository.TimetableHearingStatementRepository;
import ch.sbb.timetable.hearing.repository.TimetableHearingYearRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class YearServiceTest {

  private final YearService yearService;
  private final DossierRepository dossierRepository;
  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;

  @Autowired
  YearServiceTest(YearService yearService, DossierRepository dossierRepository,
      TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingStatementRepository timetableHearingStatementRepository) {
    this.yearService = yearService;
    this.dossierRepository = dossierRepository;
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
  }

  @AfterEach
  void tearDown() {
    dossierRepository.deleteAll();
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldUpdateDossierStatusAndArchiveYearOnCloseYearCorrectly() {
    // given
    TimetableHearingYear hearingYear = givenHearingYear(2026L, HearingStatus.ACTIVE);
    TimetableHearingStatement statement = givenStatement(2026L);

    long dossierId = dossierRepository.save(Dossier.builder()
        .swissCanton(SwissCanton.BERN)
        .topic("topic")
        .dossierStatus(DossierStatus.ADDED)
        .statementIds(List.of(statement.getId()))
        .tthDossierYear(hearingYear)
        .build()).getId();

    // when
    yearService.closeTimetableHearingYear(2026L);

    // then
    assertThat(dossierRepository.findById(dossierId).orElseThrow().getDossierStatus()).isEqualTo(DossierStatus.CANCELED);
    assertThat(timetableHearingYearRepository.findById(2026L).orElseThrow().getHearingStatus())
        .isEqualTo(HearingStatus.ARCHIVED);

    TimetableHearingStatement removedFromDossier = timetableHearingStatementRepository.findById(statement.getId())
        .orElseThrow();
    assertThat(removedFromDossier.getDossierId()).isNull();
    assertThat(removedFromDossier.getStatementStatus()).isEqualTo(StatementStatus.RECEIVED);
  }

  @Test
  void shouldNotCloseYearThatIsNotActive() {
    // given
    givenHearingYear(2026L, HearingStatus.PLANNED);

    // when & then
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> yearService.closeTimetableHearingYear(2026L));
  }

  @Test
  void shouldStartTimetableHearingYearCorrectly() {
    // given
    givenHearingYear(2028L, HearingStatus.PLANNED);

    // when
    yearService.startTimetableHearingYear(2028L);

    // then
    assertThat(timetableHearingYearRepository.findById(2028L).orElseThrow().getHearingStatus())
        .isEqualTo(HearingStatus.ACTIVE);
  }

  private TimetableHearingYear givenHearingYear(Long timetableYear, HearingStatus hearingStatus) {
    return timetableHearingYearRepository.saveAndFlush(TimetableHearingYear.builder()
        .timetableYear(timetableYear)
        .hearingStatus(hearingStatus)
        .hearingFrom(LocalDate.of(timetableYear.intValue() - 1, 1, 1))
        .hearingTo(LocalDate.of(timetableYear.intValue() - 1, 2, 1))
        .build());
  }

  private TimetableHearingStatement givenStatement(Long timetableYear) {
    return timetableHearingStatementRepository.saveAndFlush(TimetableHearingStatement.builder()
        .timetableYear(timetableYear)
        .statementStatus(StatementStatus.IN_REVIEW)
        .swissCanton(SwissCanton.BERN)
        .statement("Statement")
        .statementSender(StatementSender.builder().emails(List.of("statement@sender.ch")).build())
        .dossierId(1L)
        .build());
  }
}
