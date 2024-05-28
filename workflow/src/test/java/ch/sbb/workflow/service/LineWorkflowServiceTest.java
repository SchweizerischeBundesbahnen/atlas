package ch.sbb.workflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.client.line.workflow.LineWorkflowClient;
import ch.sbb.atlas.api.workflow.ExaminantWorkflowCheckModel;
import ch.sbb.atlas.api.workflow.PersonModel;
import ch.sbb.atlas.model.exception.NotFoundException;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowType;
import ch.sbb.workflow.entity.LineWorkflow;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyInReviewException;
import ch.sbb.workflow.exception.BusinessObjectCurrentlyNotInReviewException;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.workflow.WorkflowRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

 class LineWorkflowServiceTest {

  private WorkflowService service;

  @Mock
  private WorkflowRepository repository;

  @Mock
  private WorkflowNotificationService notificationService;
  @Mock
  private LineWorkflowClient lineWorkflowClient;

  @BeforeEach
   void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new WorkflowService(repository, notificationService, lineWorkflowClient);
  }

  @Test
   void shouldCreateWorkflow() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();
    when(repository.save(lineWorkflow)).thenReturn(lineWorkflow);
    when(lineWorkflowClient.processWorkflow(any())).thenReturn(WorkflowStatus.STARTED);

    //when
    LineWorkflow result = service.startWorkflow(lineWorkflow);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.STARTED);
    verify(notificationService).sendEventToMail(lineWorkflow);
    verify(lineWorkflowClient).processWorkflow(any());
    verify(repository).findAllByBusinessObjectIdAndStatus(any(), eq(WorkflowStatus.STARTED));
  }

  @Test
   void shouldNotCreateDuplicateWorkflow() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();
    when(repository.findAllByBusinessObjectIdAndStatus(any(), eq(WorkflowStatus.STARTED))).thenReturn(List.of(lineWorkflow));

    //when
    assertThrows(BusinessObjectCurrentlyInReviewException.class, () -> service.startWorkflow(lineWorkflow));
  }

  @Test
   void shouldGetWorkflow() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();
    when(repository.findById(1L)).thenReturn(Optional.of(lineWorkflow));

    //when
    LineWorkflow result = service.getWorkflow(1L);

    //then
    assertThat(result).isNotNull().isEqualTo(lineWorkflow);
  }

  @Test
   void shouldNotGetWorkflow() {
    //given
    when(repository.findById(1L)).thenReturn(Optional.empty());

    //when
    assertThrows(NotFoundException.IdNotFoundException.class, () -> service.getWorkflow(1L));

  }

  @Test
   void shouldGetWorkflows() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();
    when(repository.findAll()).thenReturn(List.of(lineWorkflow));

    //when
    List<LineWorkflow> result = service.getWorkflows();

    //then
    assertThat(result).isNotNull().hasSize(1).contains(lineWorkflow);

  }

  @Test
   void shouldApproveWorkflow() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .id(1L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .status(WorkflowStatus.STARTED)
        .build();
    when(repository.findById(1L)).thenReturn(Optional.of(lineWorkflow));

    //when
    LineWorkflow result = service.examinantCheck(1L, ExaminantWorkflowCheckModel.builder()
        .accepted(true)
        .checkComment("Great Job")
        .examinant(PersonModel.builder()
            .firstName("Marek")
            .lastName("Hamsik")
            .personFunction("Centrocampista")
            .build())
        .build());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getCheckComment()).isEqualTo("Great Job");
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.APPROVED);

    verify(lineWorkflowClient).processWorkflow(any());
    verify(notificationService).sendEventToMail(lineWorkflow);
  }

  @Test
   void shouldRejectWorkflow() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .id(1L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .status(WorkflowStatus.STARTED)
        .build();
    when(repository.findById(1L)).thenReturn(Optional.of(lineWorkflow));

    //when
    LineWorkflow result = service.examinantCheck(1L, ExaminantWorkflowCheckModel.builder()
        .accepted(false)
        .checkComment("Bad Job")
        .examinant(PersonModel.builder()
            .firstName("Marek")
            .lastName("Hamsik")
            .personFunction("Centrocampista")
            .build())
        .build());

    //then
    assertThat(result).isNotNull();
    assertThat(result.getCheckComment()).isEqualTo("Bad Job");
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.REJECTED);

    verify(lineWorkflowClient).processWorkflow(any());
    verify(notificationService).sendEventToMail(lineWorkflow);

  }

  @Test
   void shouldFailOnRejectWorkflowWhenNotStarted() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .id(1L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .status(WorkflowStatus.APPROVED)
        .build();
    when(repository.findById(1L)).thenReturn(Optional.of(lineWorkflow));

    //when
    Executable executeRejection = () -> service.examinantCheck(1L, ExaminantWorkflowCheckModel.builder()
        .accepted(false)
        .checkComment("Bad Job")
        .examinant(PersonModel.builder()
            .firstName("Marek")
            .lastName("Hamsik")
            .personFunction("Centrocampista")
            .build())
        .build());

    assertThrows(BusinessObjectCurrentlyNotInReviewException.class, executeRejection);
  }

  @Test
   void shouldNotSendMailsOnRevokedWorkflow() {
    //given
    LineWorkflow lineWorkflow = LineWorkflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();
    when(repository.save(lineWorkflow)).thenReturn(lineWorkflow);
    when(lineWorkflowClient.processWorkflow(any())).thenReturn(WorkflowStatus.REVOKED);

    //when
    LineWorkflow result = service.startWorkflow(lineWorkflow);

    //then
    assertThat(result).isNotNull();
    verifyNoInteractions(notificationService);
  }
}