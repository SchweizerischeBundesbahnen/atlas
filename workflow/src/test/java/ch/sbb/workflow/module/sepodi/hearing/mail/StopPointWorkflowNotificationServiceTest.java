package ch.sbb.workflow.module.sepodi.hearing.mail;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.mail.MailNotification;
import ch.sbb.workflow.mail.MailClient;
import ch.sbb.workflow.module.sepodi.hearing.enity.StopPointWorkflow;
import ch.sbb.workflow.module.sepodi.hearing.service.StopPointWorkflowBuilderNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StopPointWorkflowNotificationServiceTest {

  @Mock
  private MailClient mailClient;

  @Mock
  private StopPointWorkflowBuilderNotificationService builderNotificationService;

  private StopPointWorkflowNotificationService stopPointWorkflowNotificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    stopPointWorkflowNotificationService = new StopPointWorkflowNotificationService(mailClient,
        builderNotificationService);

    when(builderNotificationService.buildPinCodeMail(any(), anyString(), anyString())).thenReturn(
        MailNotification.builder().build());
  }

  @Test
  void shouldSendPinCodeMailViaKafka() {
    stopPointWorkflowNotificationService.sendPinCodeMail(new StopPointWorkflow(), "to@here.ch", "45646");

    verify(builderNotificationService).buildPinCodeMail(any(), eq("to@here.ch"), eq("45646"));
    verify(mailClient).sendMailRequest(any());
  }

  @Test
  void shouldSendApprovedMailViaKafka() {
    stopPointWorkflowNotificationService.sendApprovedStopPointWorkflowMail(new StopPointWorkflow());

    verify(builderNotificationService).buildWorkflowApprovedMail(any());
    verify(mailClient).sendMailRequest(any());
  }

  @Test
  void shouldSendCancelMailViaKafka() {
    stopPointWorkflowNotificationService.sendCanceledStopPointWorkflowMail(new StopPointWorkflow(), "cancel it");

    verify(builderNotificationService).buildWorkflowCanceledMail(any(), eq("cancel it"));
    verify(mailClient).sendMailRequest(any());
  }

  @Test
  void shouldSendRestartMailViaKafka() {
    stopPointWorkflowNotificationService.sendRestartStopPointWorkflowMail(new StopPointWorkflow(), new StopPointWorkflow());

    verify(builderNotificationService).buildWorkflowRestartedMail(any(), any());
    verify(builderNotificationService).buildWorkflowRestartedCCMail(any(), any());
    verify(mailClient, times(2)).sendMailRequest(any());
  }

}