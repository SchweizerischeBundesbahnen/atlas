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
import ch.sbb.timetable.hearing.entity.TthDossier;
import ch.sbb.timetable.hearing.entity.TthDossierQuestion;
import ch.sbb.timetable.hearing.service.TthDossierService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TthDossierApiInternalControllerUnitTest {

  private static final String TOPIC = "Takt Bern, Salem";

  @Mock
  private TthDossierService tthDossierService;

  @InjectMocks
  private TthDossierApiInternalController tthDossierApiInternalController;

  @Test
  void shouldGetDossier() {
    when(tthDossierService.getDossierById(1L)).thenReturn(TthDossier.builder().id(1L).topic(TOPIC).build());

    TthDossierModel dossier = tthDossierApiInternalController.getDossier(1L);

    assertThat(dossier.getId()).isEqualTo(1L);
    verify(tthDossierService).getDossierById(1L);
  }

  @Test
  void shouldCreateDossier() {
    String question = "Ist es möglich?";
    when(tthDossierService.createDossier(any())).thenReturn(TthDossier.builder().id(1L).topic(TOPIC).dossierQuestions(List.of(
        TthDossierQuestion.builder().question(question).build())).build());

    TthDossierModel model = TthDossierModel.builder()
        .topic(TOPIC)
        .boContactMail("uerli@bernmobil.ch")
        .boDeadlineToAnswer(LocalDate.now().plusDays(1)).questions(List.of(TthDossierQuestionModel.builder()
            .question(question).build()))
        .build();
    TthDossierModel dossier = tthDossierApiInternalController.createDossier(model);

    assertThat(dossier.getId()).isEqualTo(1L);
    verify(tthDossierService).createDossier(any());
  }

  @Test
  void shouldCancelDossier() {
    TthDossier dossier = TthDossier.builder().id(1L).topic(TOPIC).build();
    when(tthDossierService.getDossierById(any())).thenReturn(dossier);

    tthDossierApiInternalController.completeDossier(1L, DossierStatus.CANCELED);

    verify(tthDossierService).completeDossier(any(), eq(DossierStatus.CANCELED));
  }

  @Test
  void shouldSendDossierToBo() {
    TthDossier dossier = TthDossier.builder().id(1L).topic(TOPIC).build();
    when(tthDossierService.getDossierById(any())).thenReturn(dossier);

    tthDossierApiInternalController.sendDossierToBo(1L);

    verify(tthDossierService).sendDossierToBo(dossier);
  }

  @Test
  void shouldUpdateDossier() {
    TthDossier dossier = TthDossier.builder().id(1L).topic(TOPIC).build();
    when(tthDossierService.updateDossier(any(), any())).thenReturn(dossier);

    tthDossierApiInternalController.updateDossier(1L, TthDossierModel.builder().topic(TOPIC).build());

    verify(tthDossierService).updateDossier(eq(1L), any());
  }

  @Test
  void shouldAnswerQuestion() {
    String answerToCanton = "Nein, leider nicht";
    TthDossier dossier = TthDossier.builder().id(1L).topic(TOPIC).build();
    when(tthDossierService.getDossierByQuestionId(1L)).thenReturn(dossier);

    tthDossierApiInternalController.answerQuestion(1L, BoAnswerModel.builder().answerToCanton(answerToCanton).build());
    verify(tthDossierService).answerQuestion(1L, answerToCanton, dossier);
  }
}