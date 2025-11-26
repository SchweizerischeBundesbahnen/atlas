package ch.sbb.workflow.module.lidi.tth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierQuestionModel;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.service.TthDossierService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TthDossierControllerTest {

  private static final String TOPIC = "Takt Bern, Salem";

  @Mock
  private TthDossierService tthDossierService;

  @InjectMocks
  private TthDossierController tthDossierController;

  @Test
  void shouldGetDossier() {
    when(tthDossierService.getDossierById(1L)).thenReturn(TthDossier.builder().id(1L).topic(TOPIC).build());

    TthDossierModel dossier = tthDossierController.getDossier(1L);

    assertThat(dossier.getId()).isEqualTo(1L);
    verify(tthDossierService).getDossierById(1L);
  }

  @Test
  void shouldCreateDossier() {
    when(tthDossierService.createDossier(any())).thenReturn(TthDossier.builder().id(1L).topic(TOPIC).build());

    TthDossierModel model = TthDossierModel.builder()
        .topic(TOPIC)
        .boContactMail("uerli@bernmobil.ch")
        .boDeadlineToAnswer(LocalDate.now().plusDays(1))
        .build();
    TthDossierModel dossier = tthDossierController.createDossier(model);

    assertThat(dossier.getId()).isEqualTo(1L);
    verify(tthDossierService).createDossier(any());
  }

  @Test
  void shouldSendDossierToBo() {
    TthDossierQuestionModel model = TthDossierQuestionModel.builder()
        .question("Wie soll mit dem Takt verfahren werden?")
        .build();
    tthDossierController.sendDossierToBo(1L, model);

    verify(tthDossierService).sendDossierToBo(eq(1L), any());
  }
}