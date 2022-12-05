package ch.sbb.atlas.workflow.service;

import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.entity.BaseVersion;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.kafka.model.workflow.event.LineWorkflowEvent;
import ch.sbb.atlas.kafka.model.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.model.BaseVersionSnapshot;
import ch.sbb.atlas.workflow.model.BaseWorkflowEntity;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.atlas.workflow.repository.ObjectWorkflowRepository;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

public class BaseWorkflowEntityProcessingServiceTest {

  @Mock
  private ObjectVersionRepository objectVersionRepository;

  @Mock
  private ObjectWorkflowRepository<ObjectWorkflowEntityVersion> objectWorkflowRepository;

  @Mock
  private ObjectVersionSnapshotRepository objectVersionSnapshotRepository;

  private ObjectWorkflowProcessingService workflowProcessingService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    workflowProcessingService = new ObjectWorkflowProcessingService(objectVersionRepository, objectWorkflowRepository);
  }

  @Test
  public void shouldProcessWorkflowSuccessfully() {
    //given
    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.STARTED)
        .build();
    ObjectVersion objectVersion = ObjectVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();
    when(objectVersionRepository.findById(1000L)).thenReturn(Optional.of(objectVersion));

    ObjectWorkflowEntityVersion objectWorkflowVersion = ObjectWorkflowEntityVersion.builder()
        .workflowId(lineWorkflowEvent.getWorkflowId())
        .workflowProcessingStatus(WorkflowProcessingStatus.getProcessingStatus(lineWorkflowEvent.getWorkflowStatus()))
        .objectVersion(objectVersion)
        .build();

    ObjectVersionSnapshot objectVersionSnapshot = ObjectVersionSnapshot.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();

    //when
    workflowProcessingService.processWorkflow(lineWorkflowEvent, objectVersionSnapshot);
    //then
    verify(objectVersionRepository).findById(1000L);
    verify(objectWorkflowRepository).save(objectWorkflowVersion);
    verify(objectVersionRepository).save(objectVersion);
    verify(objectVersionSnapshotRepository).save(any(ObjectVersionSnapshot.class));

  }

  @Test
  public void shouldUpdateObjectStatusToValidated() {
    //given
    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.APPROVED)
        .build();
    ObjectVersion objectVersion = ObjectVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();
    when(objectVersionRepository.findById(1000L)).thenReturn(Optional.of(objectVersion));

    ObjectWorkflowEntityVersion objectWorkflowVersion = ObjectWorkflowEntityVersion.builder()
        .workflowId(lineWorkflowEvent.getWorkflowId())
        .workflowProcessingStatus(WorkflowProcessingStatus.getProcessingStatus(lineWorkflowEvent.getWorkflowStatus()))
        .objectVersion(objectVersion)
        .build();

    //when
    workflowProcessingService.processWorkflow(lineWorkflowEvent);
    //then
    verify(objectVersionRepository).findById(1000L);
    verify(objectWorkflowRepository).save(objectWorkflowVersion);
    verify(objectVersionRepository).save(objectVersion);
    assertThat(objectVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }

  @Test
  public void shouldUpdateObjectStatusToDraft() {
    //given
    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.REJECTED)
        .build();
    ObjectVersion objectVersion = ObjectVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();
    when(objectVersionRepository.findById(1000L)).thenReturn(Optional.of(objectVersion));

    ObjectWorkflowEntityVersion objectWorkflowVersion = ObjectWorkflowEntityVersion.builder()
        .workflowId(lineWorkflowEvent.getWorkflowId())
        .workflowProcessingStatus(WorkflowProcessingStatus.getProcessingStatus(lineWorkflowEvent.getWorkflowStatus()))
        .objectVersion(objectVersion)
        .build();

    //when
    workflowProcessingService.processWorkflow(lineWorkflowEvent);
    //then
    verify(objectVersionRepository).findById(1000L);
    verify(objectWorkflowRepository).save(objectWorkflowVersion);
    verify(objectVersionRepository).save(objectVersion);
    assertThat(objectVersion.getStatus()).isEqualTo(Status.DRAFT);
  }

  @Test
  public void shouldNotProcessWorkflowWhenObjectNotFound() {
    //given
    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.STARTED)
        .build();

    ObjectVersionSnapshot objectVersionSnapshot = ObjectVersionSnapshot.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();

    when(objectVersionRepository.findById(1000L)).thenReturn(Optional.empty());

    //when
    assertThrows(IdNotFoundException.class,
        () -> workflowProcessingService.processWorkflow(lineWorkflowEvent, objectVersionSnapshot));

  }

  @Test
  public void shouldNotProcessWorkflowWhenWorkflowStatusNotImplemenmted() {
    //given
    LineWorkflowEvent lineWorkflowEvent = LineWorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.HEARING)
        .build();
    ObjectVersion objectVersion = ObjectVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();
    when(objectVersionRepository.findById(1000L)).thenReturn(Optional.of(objectVersion));

    ObjectVersionSnapshot objectVersionSnapshot = ObjectVersionSnapshot.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();

    //when
    assertThrows(IllegalStateException.class,
        () -> workflowProcessingService.processWorkflow(lineWorkflowEvent, objectVersionSnapshot));

  }

  public interface ObjectVersionRepository extends JpaRepository<ObjectVersion, Long> {

  }

  public interface ObjectVersionSnapshotRepository extends JpaRepository<ObjectVersionSnapshot, Long> {

  }

  public interface ObjectWorkflowRepository extends JpaRepository<ObjectWorkflowEntityVersion, Long> {

  }

  public static class ObjectWorkflowProcessingService extends
      BaseWorkflowProcessingService<ObjectVersion, ObjectWorkflowEntityVersion, ObjectVersionSnapshot> {

    public ObjectWorkflowProcessingService(JpaRepository<ObjectVersion, Long> objectVersionRepository,
        ObjectWorkflowRepository<ObjectWorkflowEntityVersion> objectWorkflowRepository,
        JpaRepository<ObjectVersionSnapshot, Long> objectVerionsSnapshotRepository) {
      super(objectVersionRepository, objectWorkflowRepository, objectVerionsSnapshotRepository);
    }

    @Override
    protected ObjectWorkflowEntityVersion buildObjectVersionWorkflow(LineWorkflowEvent lineWorkflowEvent, ObjectVersion object) {
      return ObjectWorkflowEntityVersion.builder()
          .objectVersion(object)
          .workflowId(lineWorkflowEvent.getWorkflowId())
          .workflowProcessingStatus(IN_PROGRESS)
          .build();
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
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
  @EqualsAndHashCode(callSuper = false)
  @SuperBuilder
  public static class ObjectVersionSnapshot extends BaseVersionSnapshot {

    private LocalDate validFrom;
    private LocalDate validTo;

    public LocalDate getValidFrom() {
      return this.validFrom;
    }

    public LocalDate getValidTo() {
      return this.validTo;
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @SuperBuilder
  public static class ObjectWorkflowEntityVersion extends BaseWorkflowEntity {

    private ObjectVersion objectVersion;
  }

}