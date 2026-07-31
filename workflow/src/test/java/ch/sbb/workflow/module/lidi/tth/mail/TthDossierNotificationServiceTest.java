package ch.sbb.workflow.module.lidi.tth.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.mail.MailProducerService;
import ch.sbb.workflow.module.lidi.tth.client.UserAdministrationAdminClient;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierQuestion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TthDossierNotificationServiceTest {

  private static final TthDossier DOSSIER = TthDossier.builder()
      .swissCanton(SwissCanton.BERN)
      .boContactMail("urs@bernmobil.be")
      .boDeadlineToAnswer(LocalDate.now())
      .dossierQuestions(List.of(TthDossierQuestion.builder().id(1L).question("Könnt ihr?").build()))
      .build();

  @Mock
  private MailProducerService mailProducerService;

  @Mock
  private UserAdministrationAdminClient userAdministrationAdminClient;

  @InjectMocks
  private TthDossierNotificationService tthDossierNotificationService;

  @Test
  void shouldNotifyBoAboutNewQuestion() {
    tthDossierNotificationService.notifyBoAboutNewQuestion(DOSSIER);

    verify(mailProducerService).produceMailNotification(any());
  }

  @Test
  void shouldSendNotificationToAzureMailWhenCreatorHasNoManualMail() {
    // Given
    when(userAdministrationAdminClient.getUser(any())).thenReturn(
        UserModel.builder().mail("user@canton.ch").build());
    ArgumentCaptor<MailNotification> mailNotificationCaptor = ArgumentCaptor.forClass(MailNotification.class);

    // When
    tthDossierNotificationService.notifyCantonAboutNewAnswer(DOSSIER);

    // Then
    verify(mailProducerService).produceMailNotification(mailNotificationCaptor.capture());
    assertThat(mailNotificationCaptor.getValue().getTo()).containsExactly("user@canton.ch");
  }

  @Test
  void shouldSendNotificationToManualMailWhenCreatorHasManualMail() {
    // Given
    when(userAdministrationAdminClient.getUser(any())).thenReturn(
        UserModel.builder().mail("user@canton.ch").manualMail("override@canton.ch").build());
    ArgumentCaptor<MailNotification> mailNotificationCaptor = ArgumentCaptor.forClass(MailNotification.class);

    // When
    tthDossierNotificationService.notifyCantonAboutNewAnswer(DOSSIER);

    // Then
    verify(mailProducerService).produceMailNotification(mailNotificationCaptor.capture());
    assertThat(mailNotificationCaptor.getValue().getTo()).containsExactly("override@canton.ch");
  }
}