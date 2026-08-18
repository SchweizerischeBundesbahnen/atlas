package ch.sbb.workflow.module.lidi.tth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TthDossierRepositoryTest {

  private final TthDossierRepository tthDossierRepository;

  @Autowired
  TthDossierRepositoryTest(TthDossierRepository tthDossierRepository) {
    this.tthDossierRepository = tthDossierRepository;
  }

  @AfterEach
  void tearDown() {
    tthDossierRepository.deleteAll();
  }

  @Test
  void shouldFindStatementIdsByDossierStatusIn() {
    // given
    tthDossierRepository.saveAll(List.of(
        TthDossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.ADDED)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(1L, 5L))
            .timetableYear(2024L)
            .build(),
        TthDossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.DOSSIER_CANTON_CHECK)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(7L))
            .timetableYear(2024L)
            .build(),
        TthDossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.CANCELED)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(3L, 4L))
            .timetableYear(2024L)
            .build()
    ));
    // when
    List<Long> foundIds = tthDossierRepository.findStatementIdsByDossierStatusIn(
        List.of(DossierStatus.DOSSIER_CANTON_CHECK, DossierStatus.ADDED));
    // then
    assertThat(foundIds).containsExactlyInAnyOrder(7L, 1L, 5L);
  }

  @Test
  void shouldUpdateDossierStatus() {
    // given
    List<Long> savedIds = tthDossierRepository.saveAll(List.of(
        TthDossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.ADDED)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(1L, 5L))
            .timetableYear(2024L)
            .build(),
        TthDossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.DOSSIER_CANTON_CHECK)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(7L))
            .timetableYear(2024L)
            .build(),
        TthDossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(8L))
            .timetableYear(2024L)
            .build(),
        TthDossier.builder()
            .swissCanton(SwissCanton.BERN)
            .topic("test")
            .dossierStatus(DossierStatus.MOVED)
            .boContactMail("test@bo.ch")
            .boDeadlineToAnswer(LocalDate.of(2025, 12, 31))
            .statementIds(List.of(10L))
            .timetableYear(2024L)
            .build()
    )).stream().map(TthDossier::getId).toList();
    // when
    tthDossierRepository.updateDossierStatus(DossierStatus.DISSOLVED,
        List.of(DossierStatus.MOVED, DossierStatus.DOSSIER_CANTON_CHECK, DossierStatus.DOSSIER_BO_CHECK));
    // then
    assertThat(tthDossierRepository.findById(savedIds.getFirst()).get().getDossierStatus()).isEqualTo(DossierStatus.ADDED);
    assertThat(tthDossierRepository.findById(savedIds.get(1)).get().getDossierStatus()).isEqualTo(DossierStatus.DISSOLVED);
    assertThat(tthDossierRepository.findById(savedIds.get(2)).get().getDossierStatus()).isEqualTo(DossierStatus.DISSOLVED);
    assertThat(tthDossierRepository.findById(savedIds.get(3)).get().getDossierStatus()).isEqualTo(DossierStatus.DISSOLVED);
  }
}
