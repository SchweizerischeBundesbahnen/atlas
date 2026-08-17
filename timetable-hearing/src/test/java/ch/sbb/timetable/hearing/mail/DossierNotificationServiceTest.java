package ch.sbb.timetable.hearing.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.timetable.hearing.client.UserAdministrationAdminClient;
import ch.sbb.timetable.hearing.entity.Dossier;
import ch.sbb.timetable.hearing.entity.DossierQuestion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DossierNotificationServiceTest {

  private static final Dossier DOSSIER = Dossier.builder()
      .swissCanton(SwissCanton.BERN)
      .boContactMail("urs@bernmobil.be")
      .boDeadlineToAnswer(LocalDate.now())
      .dossierQuestions(List.of(DossierQuestion.builder().id(1L).question("Könnt ihr?").build()))
      .build();

  @Mock
  private MailProducerService mailProducerService;

  @Mock
  private UserAdministrationAdminClient userAdministrationAdminClient;

  @InjectMocks
  private DossierNotificationService dossierNotificationService;

  @Test
  void shouldNotifyBoAboutNewQuestion() {
    dossierNotificationService.notifyBoAboutNewQuestion(DOSSIER);

    verify(mailProducerService).produceMailNotification(any());
  }

  @Test
  void shouldSendNotificationToOriginalMailWhenCreatorHasNoManualMail() {
    // Given
    when(userAdministrationAdminClient.getUser(any())).thenReturn(
        UserModel.builder().mail("user@canton.ch").originalMail("user@canton.ch").build());
    ArgumentCaptor<MailNotification> mailNotificationCaptor = ArgumentCaptor.forClass(MailNotification.class);

    // When
    dossierNotificationService.notifyCantonAboutNewAnswer(DOSSIER);

    // Then
    verify(mailProducerService).produceMailNotification(mailNotificationCaptor.capture());
    assertThat(mailNotificationCaptor.getValue().getTo()).containsExactly("user@canton.ch");
  }

  @Test
  void shouldSendNotificationToManualMailWhenCreatorHasManualMail() {
    // Given
    when(userAdministrationAdminClient.getUser(any())).thenReturn(
        UserModel.builder().mail("override@canton.ch").originalMail("user@canton.ch").build());
    ArgumentCaptor<MailNotification> mailNotificationCaptor = ArgumentCaptor.forClass(MailNotification.class);

    // When
    dossierNotificationService.notifyCantonAboutNewAnswer(DOSSIER);

    // Then
    verify(mailProducerService).produceMailNotification(mailNotificationCaptor.capture());
    assertThat(mailNotificationCaptor.getValue().getTo()).containsExactly("override@canton.ch");
  }
}