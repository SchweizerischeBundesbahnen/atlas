package ch.sbb.workflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.base.service.model.exception.NotFoundException;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowType;
import ch.sbb.workflow.api.ExaminantWorkflowCheckModel;
import ch.sbb.workflow.api.PersonModel;
import ch.sbb.workflow.entity.Workflow;
import ch.sbb.workflow.kafka.WorkflowNotificationService;
import ch.sbb.workflow.workflow.WorkflowRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WorkflowServiceTest {

  private WorkflowService service;

  @Mock
  private WorkflowRepository repository;

  @Mock
  private WorkflowNotificationService notificationService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new WorkflowService(repository, notificationService);
  }

  @Test
  public void shouldCreateWorkflow() {
    //given
    Workflow workflow = Workflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();
    when(repository.save(workflow)).thenReturn(workflow);

    //when
    Workflow result = service.startWorkflow(workflow);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getStatus()).isEqualTo(WorkflowStatus.STARTED);
    verify(notificationService).sendEventToMail(workflow);
    verify(notificationService).sendEventToLidi(workflow);

  }

  @Test
  public void shouldGetWorkflow() {
    //given
    Workflow workflow = Workflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();
    when(repository.findById(1L)).thenReturn(Optional.of(workflow));

    //when
    Workflow result = service.getWorkflow(1L);

    //then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(workflow);

  }

  @Test
  public void shouldNotGetWorkflow() {
    //given
    when(repository.findById(1L)).thenReturn(Optional.empty());

    //when
    assertThrows(NotFoundException.IdNotFoundException.class, () -> service.getWorkflow(1L));

  }

  @Test
  public void shouldGetWorkflows() {
    //given
    Workflow workflow = Workflow.builder()
        .id(123L)
        .businessObjectId(123L)
        .workflowType(WorkflowType.LINE)
        .swissId("ch:slnid:123")
        .build();
    when(repository.findAll()).thenReturn(List.of(workflow));

    //when
    List<Workflow> result = service.getWorkflows();

    //then
    assertThat(result).isNotNull();
    assertThat(result.size()).isEqualTo(1);
    assertThat(result).contains(workflow);

  }

  @Test
  public void shouldApproveWorkflow() {
    //given
    Workflow workflow = Workflow.builder()
            .id(1L)
            .businessObjectId(123L)
            .workflowType(WorkflowType.LINE)
            .swissId("ch:slnid:123")
            .build();
    when(repository.findById(1L)).thenReturn(Optional.of(workflow));

    //when
    Workflow result = service.examinantCheck(1L, ExaminantWorkflowCheckModel.builder()
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
    verify(notificationService).sendEventToLidi(workflow);
    verify(notificationService).sendResultMailToClient(workflow);
  }
  @Test
  public void shouldRejectWorkflow() {
    //given
    Workflow workflow = Workflow.builder()
            .id(1L)
            .businessObjectId(123L)
            .workflowType(WorkflowType.LINE)
            .swissId("ch:slnid:123")
            .build();
    when(repository.findById(1L)).thenReturn(Optional.of(workflow));

    //when
    Workflow result = service.examinantCheck(1L, ExaminantWorkflowCheckModel.builder()
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
    verify(notificationService).sendEventToLidi(workflow);
    verify(notificationService).sendResultMailToClient(workflow);

  }
}