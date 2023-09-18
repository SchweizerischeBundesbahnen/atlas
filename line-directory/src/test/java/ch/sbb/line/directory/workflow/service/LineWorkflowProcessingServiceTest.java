package ch.sbb.line.directory.workflow.service;

import static ch.sbb.atlas.workflow.model.WorkflowStatus.ADDED;
import static ch.sbb.atlas.workflow.model.WorkflowStatus.APPROVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.workflow.model.WorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.LineVersionSnapshotRepository;
import ch.sbb.line.directory.repository.LineVersionWorkflowRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

 class LineWorkflowProcessingServiceTest {

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private LineVersionWorkflowRepository lineWorkflowRepository;

  @Mock
  private LineVersionSnapshotRepository lineVersionSnapshotRepository;

  @Captor
  private ArgumentCaptor<LineVersionWorkflow> lineVersionWorkflowArgumentCaptor;

  private LineWorkflowProcessingService workflowProcessingService;

  @BeforeEach
   void init() {
    MockitoAnnotations.openMocks(this);
    workflowProcessingService = new LineWorkflowProcessingService(lineVersionRepository, lineWorkflowRepository,
        lineVersionSnapshotRepository);
  }

  @Test
   void shouldExecuteProcessLineWorkflow() {
    //given
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(ADDED)
        .build();
    LineVersion lineVersion = LineVersion.builder().id(1000L).build();
    when(lineVersionRepository.findById(1000L)).thenReturn(Optional.of(lineVersion));

    //when
    workflowProcessingService.processLineWorkflow(workflowEvent, lineVersion);

    //then
    verify(lineVersionRepository).save(lineVersion);
    verify(lineVersionSnapshotRepository).save(any(LineVersionSnapshot.class));
    verify(lineWorkflowRepository).save(any(LineVersionWorkflow.class));
  }

  @Test
   void shouldApproveLineWorkflow() {
    //given
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(APPROVED)
        .build();
    LineVersion lineVersion = LineVersion.builder().id(1000L).build();
    when(lineVersionRepository.findById(1000L)).thenReturn(Optional.of(lineVersion));

    //when
    workflowProcessingService.processLineWorkflow(workflowEvent, lineVersion);

    //then
    verify(lineVersionRepository).save(lineVersion);
    verify(lineWorkflowRepository).save(lineVersionWorkflowArgumentCaptor.capture());
    verify(lineVersionSnapshotRepository).save(any(LineVersionSnapshot.class));

    assertThat(lineVersionWorkflowArgumentCaptor.getValue().getWorkflowProcessingStatus()).isEqualTo(
        WorkflowProcessingStatus.EVALUATED);
  }

}