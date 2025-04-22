package ch.sbb.workflow.mail;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.lidi.line.entity.LineWorkflow;
import ch.sbb.workflow.lidi.line.service.LineWorkflowBuilderNotificationService;
import ch.sbb.workflow.lidi.line.mail.LineWorkflowNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class LineLineWorkflowNotificationServiceTest {

  private LineWorkflowNotificationService notificationService;

  @Mock
  private MailProducerService mailProducerService;

  @Mock
  private LineWorkflowBuilderNotificationService lineWorkflowBuilderNotificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new LineWorkflowNotificationService(mailProducerService, lineWorkflowBuilderNotificationService);
  }

  @Test
  void shouldSendEventToMail() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .workflowType(WorkflowType.LINE)
        .status(WorkflowStatus.STARTED)
        .build();
    MailNotification mailNotification = MailNotification.builder().build();
    when(lineWorkflowBuilderNotificationService.buildWorkflowMailNotification(lineWorkflow)).thenReturn(mailNotification);

    //when
    notificationService.sendEventToMail(lineWorkflow);

    //then
    Mockito.verify(mailProducerService).produceMailNotification(any(MailNotification.class));
  }

  @Test
  void shouldNotSendEventToMail() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .build();
    MailNotification mailNotification = MailNotification.builder().build();
    when(lineWorkflowBuilderNotificationService.buildWorkflowMailNotification(lineWorkflow)).thenReturn(mailNotification);
    //when
    notificationService.sendEventToMail(lineWorkflow);
    //then
    Mockito.verify(mailProducerService, never()).produceMailNotification(any(MailNotification.class));
  }

}