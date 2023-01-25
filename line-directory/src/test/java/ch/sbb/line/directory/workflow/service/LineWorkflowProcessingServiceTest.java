package ch.sbb.line.directory.workflow.service;

import static ch.sbb.atlas.base.service.model.workflow.WorkflowStatus.ADDED;
import static ch.sbb.atlas.base.service.model.workflow.WorkflowStatus.APPROVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.line.workflow.LineWorkflowEvent;
import ch.sbb.atlas.user.administration.security.UserAdministrationService;
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

public class LineWorkflowProcessingServiceTest {

  @Mock
  private LineVersionRepository lineVersionRepository;

  @Mock
  private LineVersionWorkflowRepository lineWorkflowRepository;

  @Mock
  private LineVersionSnapshotRepository lineVersionSnapshotRepository;

  @Mock
  private UserAdministrationService userAdministrationService;

  @Captor
  private ArgumentCaptor<LineVersionWorkflow> lineVersionWorkflowArgumentCaptor;

  private LineWorkflowProcessingService workflowProcessingService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    workflowProcessingService = new LineWorkflowProcessingService(lineVersionRepository, lineWorkflowRepository,
        lineVersionSnapshotRepository, userAdministrationService);
    when(userAdministrationService.hasUserPermissionsToCreate(any(), any())).thenReturn(true);
  }

  @Test
  public void shouldExecuteProcessLineWorkflow() {
    //given
    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(ADDED)
        .build();
    LineVersion lineVersion = LineVersion.builder().id(1000L).build();
    when(lineVersionRepository.findById(1000L)).thenReturn(Optional.of(lineVersion));

    //when
    workflowProcessingService.processLineWorkflow(lineWorkflowEvent);

    //then
    verify(lineVersionRepository).save(lineVersion);
    verify(lineVersionSnapshotRepository).save(any(LineVersionSnapshot.class));
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
    verify(lineVersionSnapshotRepository).save(any(LineVersionSnapshot.class));

    assertThat(lineVersionWorkflowArgumentCaptor.getValue().getWorkflowProcessingStatus()).isEqualTo(
        WorkflowProcessingStatus.EVALUATED);
  }

}