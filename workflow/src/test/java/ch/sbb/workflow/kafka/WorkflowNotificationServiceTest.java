package ch.sbb.workflow.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.line.directory.workflow.api.LineWorkflowEvent;
import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import ch.sbb.atlas.base.service.model.workflow.WorkflowType;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.service.lidi.LineWorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class WorkflowNotificationServiceTest {

  private WorkflowNotificationService notificationService;

  @Mock
  private LineWorkflowProducerService lineWorkflowProducerService;

  @Mock
  private MailProducerService mailProducerService;

  @Mock
  private LineWorkflowService lineWorkflowService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new WorkflowNotificationService(lineWorkflowProducerService, mailProducerService, lineWorkflowService);
  }

  @Test
  public void shouldSendEventToMail() {
    //given
    Workflow workflow = Workflow.builder()
        .workflowType(WorkflowType.LINE)
        .status(WorkflowStatus.STARTED)
        .build();
    MailNotification mailNotification = MailNotification.builder().build();
    when(lineWorkflowService.buildWorkflowStartedMailNotification(workflow)).thenReturn(mailNotification);

    //when
    notificationService.sendEventToMail(workflow);

    //then
    Mockito.verify(mailProducerService).produceMailNotification(any(MailNotification.class));
  }

  @Test
  public void shouldNotSendEventToMail() {
    //given
    Workflow workflow = Workflow.builder()
        .build();
    MailNotification mailNotification = MailNotification.builder().build();
    when(lineWorkflowService.buildWorkflowStartedMailNotification(workflow)).thenReturn(mailNotification);
    //when
    notificationService.sendEventToMail(workflow);
    //then
    Mockito.verify(mailProducerService, never()).produceMailNotification(any(MailNotification.class));
  }

  @Test
  public void shouldSendEventToLidi() {
    //given
    Workflow workflow = Workflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .status(WorkflowStatus.ADDED)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();

    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
        .workflowId(workflow.getId())
        .businessObjectId(workflow.getBusinessObjectId())
        .workflowStatus(workflow.getStatus())
        .build();

    //when
    notificationService.sendEventToLidi(workflow);

    //then
    Mockito.verify(lineWorkflowProducerService).produceWorkflowNotification(lineWorkflowEvent);
  }
}