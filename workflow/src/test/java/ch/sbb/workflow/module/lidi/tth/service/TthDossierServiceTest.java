package ch.sbb.workflow.module.lidi.tth.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierQuestion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TthDossierServiceTest {

  @Autowired
  private TthDossierService tthDossierService;

  @Test
  void shouldSaveDossier() {
    TthDossier dossier = tthDossierService.createDossier(TthDossier.builder()
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit TU")
        .boContactMail("bern@mobil.be")
        .dossierStatus(StatementStatus.RECEIVED)
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .build());
    assertThat(dossier.getId()).isNotNull();
  }

  @Test
  void shouldSaveDossierWithQuestion() {
    TthDossier dossier = TthDossier.builder()
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit TU")
        .boContactMail("bern@mobil.be")
        .dossierStatus(StatementStatus.RECEIVED)
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .build();
    dossier.setDossierQuestions(List.of(TthDossierQuestion.builder()
        .tthDossier(dossier)
        .answerToCanton("Wir können den Takt nicht erhöhen.")
        .build()));

    dossier = tthDossierService.createDossier(dossier);
    assertThat(dossier.getId()).isNotNull();
    assertThat(dossier.getDossierQuestions()).hasSize(1);
  }
}