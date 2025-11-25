package ch.sbb.workflow.module.lidi.tth.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.client.line.workflow.TimetableHearingStatementClient;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierQuestion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@IntegrationTest
class TthDossierServiceTest {

  @Autowired
  private TthDossierService tthDossierService;

  @MockitoBean
  private TimetableHearingStatementClient timetableHearingStatementClient;

  @Test
  void shouldSaveDossier() {
    TthDossier dossier = tthDossierService.createDossier(TthDossier.builder()
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .statementIds(List.of(132L, 145L))
        .boContactMail("bern@mobil.be")
        .dossierStatus(DossierStatus.ADDED)
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .build());
    assertThat(dossier.getId()).isNotNull();
  }

  @Test
  void shouldSendQuestionToTu() {
    // given
    TthDossier dossier = TthDossier.builder()
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .boContactMail("bern@mobil.be")
        .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
        .statementIds(List.of(132L, 145L))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .build();
    dossier = tthDossierService.createDossier(dossier);
    assertThat(dossier.getId()).isNotNull();

    // when
    TthDossierQuestion question = TthDossierQuestion.builder()
        .question("Wie soll mit dem Takt verfahren werden?")
        .build();
    TthDossier tthDossier = tthDossierService.sendDossierToBo(dossier.getId(), question);

    // then
    assertThat(tthDossier.getDossierQuestions()).hasSize(1);
  }

  @Test
  void shouldGetDossier() {
    // given
    TthDossier dossier = TthDossier.builder()
        .topic("Bern, Salem - Takt")
        .internalComment("Noch mit Bernmobil abklären")
        .publicComment("In Abklärung mit GO")
        .boContactMail("bern@mobil.be")
        .dossierStatus(DossierStatus.DOSSIER_BO_CHECK)
        .statementIds(List.of(132L, 145L))
        .boDeadlineToAnswer(LocalDate.now().plusDays(7))
        .build();
    TthDossierQuestion question = TthDossierQuestion.builder()
        .question("Wie soll mit dem Takt verfahren werden?")
        .tthDossier(dossier)
        .build();
    dossier.setDossierQuestions(new ArrayList<>(List.of(question)));
    tthDossierService.createDossier(dossier);

    // when
    dossier = tthDossierService.getDossierById(dossier.getId());

    // then
    assertThat(dossier.getId()).isNotNull();
    assertThat(dossier.getDossierQuestions()).hasSize(1);
  }
}