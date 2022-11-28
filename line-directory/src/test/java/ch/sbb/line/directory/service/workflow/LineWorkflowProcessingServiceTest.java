package ch.sbb.line.directory.service.workflow;

import static ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus.APPROVED;
import static ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.workflow.event.LineWorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.LineVersionWorkflowRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LineWorkflowProcessingServiceTest {

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private LineVersionWorkflowRepository lineWorkflowRepository;

  @Captor
  private ArgumentCaptor<LineVersionWorkflow> lineVersionWorkflowArgumentCaptor;

  private LineWorkflowProcessingService workflowProcessingService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    workflowProcessingService = new LineWorkflowProcessingService(lineVersionRepository, lineWorkflowRepository);
  }

  @Test
  public void shouldExecuteProcessLineWorkflow() {
    //given
    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(STARTED)
        .build();
    LineVersion lineVersion = LineVersion.builder().id(1000L).build();
    when(lineVersionRepository.findById(1000L)).thenReturn(Optional.of(lineVersion));

    //when
    workflowProcessingService.processLineWorkflow(lineWorkflowEvent);

    //then
    verify(lineVersionRepository).save(lineVersion);
    verify(lineWorkflowRepository).save(any(LineVersionWorkflow.class));
  }

  @Test
  public void shouldApproveLineWorkflow() {
    //given
    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
            .workflowId(1000L)
            .businessObjectId(1000L)
            .workflowStatus(APPROVED)
            .build();
    LineVersion lineVersion = LineVersion.builder().id(1000L).build();
    when(lineVersionRepository.findById(1000L)).thenReturn(Optional.of(lineVersion));

    //when
    workflowProcessingService.processLineWorkflow(lineWorkflowEvent);

    //then
    verify(lineVersionRepository).save(lineVersion);
    verify(lineWorkflowRepository).save(lineVersionWorkflowArgumentCaptor.capture());

    assertThat(lineVersionWorkflowArgumentCaptor.getValue().getWorkflowProcessingStatus()).isEqualTo(WorkflowProcessingStatus.EVALUATED);
  }

}