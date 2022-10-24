package ch.sbb.workflow.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.atlas.kafka.model.workflow.model.BusinessObjectType;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowType;
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
  private WorkflowProducerService workflowProducerService;

  @Mock
  private MailProducerService mailProducerService;

  @Mock
  private LineWorkflowService lineWorkflowService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new WorkflowNotificationService(workflowProducerService, mailProducerService, lineWorkflowService);
  }

  @Test
  public void shouldSendEventToMail() {
    //given
    Workflow workflow = Workflow.builder()
        .workflowType(WorkflowType.LINE)
        .build();
    MailNotification mailNotification = MailNotification.builder().build();
    when(lineWorkflowService.buildMailNotification(workflow)).thenReturn(mailNotification);

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
    when(lineWorkflowService.buildMailNotification(workflow)).thenReturn(mailNotification);
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
        .businessObjectType(BusinessObjectType.SLNID)
        .status(WorkflowStatus.ADDED)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();

    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(workflow.getId())
        .businessObjectId(workflow.getBusinessObjectId())
        .businessObjectType(workflow.getBusinessObjectType())
        .workflowStatus(workflow.getStatus())
        .workflowType(workflow.getWorkflowType())
        .swissId(workflow.getSwissId())
        .build();

    //when
    notificationService.sendEventToLidi(workflow);

    //then
    Mockito.verify(workflowProducerService).produceWorkflowNotification(workflowEvent);
  }
}