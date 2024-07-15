package ch.sbb.workflow.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.service.sepodi.StopPointWorkflowBuilderNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StopPointWorkflowNotificationServiceTest {

  @Mock
  private MailProducerService mailProducerService;

  @Mock
  private StopPointWorkflowBuilderNotificationService builderNotificationService;

  private StopPointWorkflowNotificationService stopPointWorkflowNotificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    stopPointWorkflowNotificationService = new StopPointWorkflowNotificationService(mailProducerService, builderNotificationService);

    when(builderNotificationService.buildPinCodeMail(any(), anyString(), anyString())).thenReturn(
        MailNotification.builder().build());
  }

  @Test
  void shouldSendPinCodeMailViaKafka() {
    stopPointWorkflowNotificationService.sendPinCodeMail(new StopPointWorkflow(), "to@here.ch", "45646");

    verify(builderNotificationService).buildPinCodeMail(any(), eq("to@here.ch"), eq("45646"));
    verify(mailProducerService).produceMailNotification(any());
  }

  @Test
  void shouldSendApprovedMailViaKafka() {
    stopPointWorkflowNotificationService.sendApprovedStopPointWorkflowMail(new StopPointWorkflow());

    verify(builderNotificationService).buildWorkflowApprovedMail(any());
    verify(mailProducerService).produceMailNotification(any());
  }

  @Test
  void shouldSendCancelMailViaKafka() {
    stopPointWorkflowNotificationService.sendCanceledStopPointWorkflowMail(new StopPointWorkflow(), "cancel it");

    verify(builderNotificationService).buildWorkflowCanceledMail(any(), eq("cancel it"));
    verify(mailProducerService).produceMailNotification(any());
  }

  @Test
  void shouldSendRestartMailViaKafka() {
    String oldDesignationOfficial = "Old Bern";
    stopPointWorkflowNotificationService.sendRestartStopPointWorkflowMail(new StopPointWorkflow(), oldDesignationOfficial);

    verify(builderNotificationService).buildWorkflowRestartedMail(any(), anyString());
    verify(builderNotificationService).buildWorkflowRestartedCCMail(any(), anyString());
    verify(mailProducerService, times(2)).produceMailNotification(any());
  }

}