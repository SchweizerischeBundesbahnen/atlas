package ch.sbb.timetable.hearing.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.StatementSender;
import ch.sbb.timetable.hearing.entity.TimetableHearingStatement;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class DossierRepositoryTest {

  private static final long TIMETABLE_YEAR = 2024L;

  private final DossierRepository dossierRepository;
  private final TimetableHearingYearRepository timetableHearingYearRepository;
  private final TimetableHearingStatementRepository timetableHearingStatementRepository;

  private TimetableHearingYear timetableHearingYear;

  @Autowired
  DossierRepositoryTest(DossierRepository dossierRepository,
      TimetableHearingYearRepository timetableHearingYearRepository,
      TimetableHearingStatementRepository timetableHearingStatementRepository) {
    this.dossierRepository = dossierRepository;
    this.timetableHearingYearRepository = timetableHearingYearRepository;
    this.timetableHearingStatementRepository = timetableHearingStatementRepository;
  }

  @BeforeEach
  void setUp() {
    timetableHearingYear = timetableHearingYearRepository.save(TimetableHearingYear.builder()
        .timetableYear(TIMETABLE_YEAR)
        .hearingStatus(HearingStatus.ACTIVE)
        .hearingFrom(LocalDate.of(2023, 1, 1))
        .hearingTo(LocalDate.of(2023, 2, 1))
        .build());
  }

  @AfterEach
  void tearDown() {
    dossierRepository.deleteAll();
    timetableHearingStatementRepository.deleteAll();
    timetableHearingYearRepository.deleteAll();
  }

  private Long statementId() {
    return timetableHearingStatementRepository.saveAndFlush(TimetableHearingStatement.builder()
        .timetableYear(TIMETABLE_YEAR)
        .statementStatus(StatementStatus.RECEIVED)
        .swissCanton(SwissCanton.BERN)
        .statement("Statement")
        .statementSender(StatementSender.builder().emails(List.of("statement@sender.ch")).build())
        .build()).getId();
  }

  @Test
  void shouldFindStatementIdsByDossierStatusIn() {
    // given
    Long addedStatementId1 = statementId();
    Long addedStatementId2 = statementId();
    Long cantonCheckStatementId = statementId();
    Long canceledStatementId1 = statementId();
    Long canceledStatementId2 = statementId();

    dossierRepository.saveAll(List.of(
        Dossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.ADDED)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(addedStatementId1, addedStatementId2))
            .tthDossierYear(timetableHearingYear)
            .build(),
        Dossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.DOSSIER_CANTON_CHECK)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(cantonCheckStatementId))
            .tthDossierYear(timetableHearingYear)
            .build(),
        Dossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.CANCELED)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(canceledStatementId1, canceledStatementId2))
            .tthDossierYear(timetableHearingYear)
            .build()
    ));
    // when
    List<Long> foundIds = dossierRepository.findStatementIdsByDossierStatusIn(
        List.of(DossierStatus.DOSSIER_CANTON_CHECK, DossierStatus.ADDED));
    // then
    assertThat(foundIds).containsExactlyInAnyOrder(cantonCheckStatementId, addedStatementId1, addedStatementId2);
  }

  @Test
  void shouldUpdateDossierStatus() {
    // given
    List<Long> savedIds = dossierRepository.saveAll(List.of(
        Dossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.ADDED)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(statementId(), statementId()))
            .tthDossierYear(timetableHearingYear)
            .build(),
        Dossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.DOSSIER_CANTON_CHECK)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(statementId()))
            .tthDossierYear(timetableHearingYear)
            .build(),
        Dossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(statementId()))
            .tthDossierYear(timetableHearingYear)
            .build(),
        Dossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.MOVED)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(statementId()))
            .tthDossierYear(timetableHearingYear)
            .build()
    )).stream().map(Dossier::getId).toList();
    // when
    dossierRepository.updateDossierStatus(DossierStatus.DISSOLVED,
        List.of(DossierStatus.MOVED, DossierStatus.DOSSIER_CANTON_CHECK, DossierStatus.DOSSIER_BO_CHECK));
    // then
    assertThat(dossierRepository.findById(savedIds.getFirst()).get().getDossierStatus()).isEqualTo(DossierStatus.ADDED);
    assertThat(dossierRepository.findById(savedIds.get(1)).get().getDossierStatus()).isEqualTo(DossierStatus.DISSOLVED);
    assertThat(dossierRepository.findById(savedIds.get(2)).get().getDossierStatus()).isEqualTo(DossierStatus.DISSOLVED);
    assertThat(dossierRepository.findById(savedIds.get(3)).get().getDossierStatus()).isEqualTo(DossierStatus.DISSOLVED);
  }
}
