package ch.sbb.workflow.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.LineWorkflow;
import ch.sbb.workflow.service.lidi.LineWorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

 class LineWorkflowNotificationServiceTest {

  private WorkflowNotificationService notificationService;

  @Mock
  private MailProducerService mailProducerService;

  @Mock
  private LineWorkflowService lineWorkflowService;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new WorkflowNotificationService(mailProducerService, lineWorkflowService);
  }

  @Test
   void shouldSendEventToMail() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .workflowType(WorkflowType.LINE)
        .status(WorkflowStatus.STARTED)
        .build();
    MailNotification mailNotification = MailNotification.builder().build();
    when(lineWorkflowService.buildWorkflowStartedMailNotification(lineWorkflow)).thenReturn(mailNotification);

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
    when(lineWorkflowService.buildWorkflowStartedMailNotification(lineWorkflow)).thenReturn(mailNotification);
    //when
    notificationService.sendEventToMail(lineWorkflow);
    //then
    Mockito.verify(mailProducerService, never()).produceMailNotification(any(MailNotification.class));
  }

}