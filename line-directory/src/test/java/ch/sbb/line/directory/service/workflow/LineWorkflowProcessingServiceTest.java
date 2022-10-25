package ch.sbb.line.directory.service.workflow;

import static ch.sbb.atlas.kafka.model.workflow.model.BusinessObjectType.SLNID;
import static ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus.STARTED;
import static ch.sbb.atlas.kafka.model.workflow.model.WorkflowType.LINE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.LineVersionWorkflowRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LineWorkflowProcessingServiceTest {

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private LineVersionWorkflowRepository lineWorkflowRepository;

  private LineWorkflowProcessingService workflowProcessingService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    workflowProcessingService = new LineWorkflowProcessingService(lineVersionRepository, lineWorkflowRepository);
  }

  @Test
  public void shouldExecuteProcessLineWorkflow() {
    //given
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .businessObjectType(SLNID)
        .workflowStatus(STARTED)
        .workflowType(LINE)
        .swissId("ch:slnid:1000")
        .build();
    LineVersion lineVersion = LineVersion.builder().id(1000L).build();
    when(lineVersionRepository.findById(1000L)).thenReturn(Optional.of(lineVersion));

    //when
    workflowProcessingService.processLineWorkflow(workflowEvent);

    //then
    verify(lineVersionRepository).save(lineVersion);
    verify(lineWorkflowRepository).save(any(LineVersionWorkflow.class));

  }

}