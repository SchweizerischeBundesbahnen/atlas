package ch.sbb.timetable.hearing.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.workflow.tth.dossier.BoAnswerModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierModel;
import ch.sbb.atlas.api.workflow.tth.dossier.TthDossierQuestionModel;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.DossierQuestion;
import ch.sbb.timetable.hearing.service.DossierService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DossierApiInternalControllerUnitTest {

  private static final String TOPIC = "Takt Bern, Salem";

  @Mock
  private DossierService dossierService;

  @InjectMocks
  private DossierApiInternalController dossierApiInternalController;

  @Test
  void shouldGetDossier() {
    when(dossierService.getDossierById(1L)).thenReturn(Dossier.builder().id(1L).topic(TOPIC).build());

    TthDossierModel dossier = dossierApiInternalController.getDossier(1L);

    assertThat(dossier.getId()).isEqualTo(1L);
    verify(dossierService).getDossierById(1L);
  }

  @Test
  void shouldCreateDossier() {
    String question = "Ist es möglich?";
    when(dossierService.createDossier(any())).thenReturn(Dossier.builder().id(1L).topic(TOPIC).dossierQuestions(List.of(
        DossierQuestion.builder().question(question).build())).build());

    TthDossierModel model = TthDossierModel.builder()
        .topic(TOPIC)
        .boContactMail("uerli@bernmobil.ch")
        .boDeadlineToAnswer(LocalDate.now().plusDays(1)).questions(List.of(TthDossierQuestionModel.builder()
            .question(question).build()))
        .build();
    TthDossierModel dossier = dossierApiInternalController.createDossier(model);

    assertThat(dossier.getId()).isEqualTo(1L);
    verify(dossierService).createDossier(any());
  }

  @Test
  void shouldCancelDossier() {
    Dossier dossier = Dossier.builder().id(1L).topic(TOPIC).build();
    when(dossierService.getDossierById(any())).thenReturn(dossier);

    dossierApiInternalController.completeDossier(1L, DossierStatus.CANCELED);

    verify(dossierService).completeDossier(any(), eq(DossierStatus.CANCELED));
  }

  @Test
  void shouldSendDossierToBo() {
    Dossier dossier = Dossier.builder().id(1L).topic(TOPIC).build();
    when(dossierService.getDossierById(any())).thenReturn(dossier);

    dossierApiInternalController.sendDossierToBo(1L);

    verify(dossierService).sendDossierToBo(dossier);
  }

  @Test
  void shouldUpdateDossier() {
    Dossier dossier = Dossier.builder().id(1L).topic(TOPIC).build();
    when(dossierService.updateDossier(any(), any())).thenReturn(dossier);

    dossierApiInternalController.updateDossier(1L, TthDossierModel.builder().topic(TOPIC).build());

    verify(dossierService).updateDossier(eq(1L), any());
  }

  @Test
  void shouldAnswerQuestion() {
    String answerToCanton = "Nein, leider nicht";
    Dossier dossier = Dossier.builder().id(1L).topic(TOPIC).build();
    when(dossierService.getDossierByQuestionId(1L)).thenReturn(dossier);

    dossierApiInternalController.answerQuestion(1L, BoAnswerModel.builder().answerToCanton(answerToCanton).build());
    verify(dossierService).answerQuestion(1L, answerToCanton, dossier);
  }
}