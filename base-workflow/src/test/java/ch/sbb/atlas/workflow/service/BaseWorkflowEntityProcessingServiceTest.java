package ch.sbb.atlas.workflow.service;

import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.kafka.model.workflow.WorkflowEvent;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.BaseWorkflowEntity;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import java.time.LocalDate;
import java.util.Optional;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

public class BaseWorkflowEntityProcessingServiceTest {

  @Mock
  private ObjectVersionRepository objectVersionRepository;

  @Mock
  private ObjectWorkflowRepository objectWorkflowRepository;

  private ObjectWorkflowProcessingService workflowProcessingService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    workflowProcessingService = new ObjectWorkflowProcessingService(objectVersionRepository, objectWorkflowRepository);
  }

  @Test
  public void shouldProcessWorkflowSuccessfully() {
    //given
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.STARTED)
        .build();
    ObjectVersion objectVersion = ObjectVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();
    Mockito.when(objectVersionRepository.findById(1000L)).thenReturn(Optional.of(objectVersion));

    ObjectWorkflowEntityVersion objectWorkflowVersion = ObjectWorkflowEntityVersion.builder()
        .workflowId(workflowEvent.getWorkflowId())
        .workflowProcessingStatus(WorkflowProcessingStatus.getProcessingStatus(workflowEvent.getWorkflowStatus()))
        .objectVersion(objectVersion)
        .build();

    //when
    workflowProcessingService.processWorkflow(workflowEvent);
    //then
    verify(objectVersionRepository).findById(1000L);
    verify(objectWorkflowRepository).save(objectWorkflowVersion);
    verify(objectVersionRepository).save(objectVersion);

  }

  @Test
  public void shouldNotProcessWorkflowWhenObjectNotFound() {
    //given
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.STARTED)
        .build();

    Mockito.when(objectVersionRepository.findById(1000L)).thenReturn(Optional.empty());

    //when
    assertThrows(IdNotFoundException.class, () -> {
      workflowProcessingService.processWorkflow(workflowEvent);
    });

  }

  @Test
  public void shouldNotProcessWorkflowWhenWorkflowStatusNotImplemenmted() {
    //given
    WorkflowEvent workflowEvent = WorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.HEARING)
        .build();
    ObjectVersion objectVersion = ObjectVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();
    Mockito.when(objectVersionRepository.findById(1000L)).thenReturn(Optional.of(objectVersion));

    //when
    assertThrows(IllegalStateException.class, () -> {
      workflowProcessingService.processWorkflow(workflowEvent);
    });

  }

  public interface ObjectVersionRepository extends JpaRepository<ObjectVersion, Long> {

  }

  public interface ObjectWorkflowRepository extends JpaRepository<ObjectWorkflowEntityVersion, Long> {

  }

  public static class ObjectWorkflowProcessingService extends
      BaseWorkflowProcessingService<ObjectVersion, ObjectWorkflowEntityVersion> {

    public ObjectWorkflowProcessingService(JpaRepository<ObjectVersion, Long> objectVersionRepository,
        JpaRepository<ObjectWorkflowEntityVersion, Long> objectWorkflowRepository) {
      super(objectVersionRepository, objectWorkflowRepository);
    }

    @Override
    protected ObjectWorkflowEntityVersion buildObjectVersionWorkflow(WorkflowEvent workflowEvent, ObjectVersion object) {
      return ObjectWorkflowEntityVersion.builder()
          .objectVersion(object)
          .workflowId(workflowEvent.getWorkflowId())
          .workflowProcessingStatus(IN_PROGRESS)
          .build();
    }
  }

  @Data
  @SuperBuilder
  public static class ObjectVersion extends BaseVersion {

    private LocalDate validFrom;
    private LocalDate validTo;

    @Override
    public LocalDate getValidFrom() {
      return this.validFrom;
    }

    @Override
    public LocalDate getValidTo() {
      return this.validTo;
    }
  }

  @Data
  @SuperBuilder
  public static class ObjectWorkflowEntityVersion extends BaseWorkflowEntity {

    private ObjectVersion objectVersion;
  }

}