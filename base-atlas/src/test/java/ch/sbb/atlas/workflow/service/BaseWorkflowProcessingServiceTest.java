package ch.sbb.atlas.workflow.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.workflow.model.BaseVersionSnapshot;
import ch.sbb.atlas.workflow.model.BaseWorkflowEntity;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.repository.ObjectWorkflowRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

public class BaseWorkflowProcessingServiceTest {

  @Mock
  private ObjectVersionRepository objectVersionRepository;

  @Mock
  private ObjectWorkflowRepository<ObjectWorkflowEntityVersion> objectWorkflowRepository;

  @Mock
  private ObjectVersionSnapshotRepository objectVersionSnapshotRepository;

  private ObjectWorkflowProcessingService workflowProcessingService;

  @BeforeEach
   void init() {
    MockitoAnnotations.openMocks(this);
    workflowProcessingService = new ObjectWorkflowProcessingService(objectVersionRepository, objectWorkflowRepository,
        objectVersionSnapshotRepository);
  }

  @Test
   void shouldProcessWorkflowSuccessfully() {
    //given
    WorkflowEvent workflowEvent = BaseWorkflowProcessingServiceTest.WorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.ADDED)
        .build();
    ObjectVersion objectVersion = ObjectVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();
    when(objectVersionRepository.findById(1000L)).thenReturn(Optional.of(objectVersion));

    ObjectWorkflowEntityVersion objectWorkflowVersion = ObjectWorkflowEntityVersion.builder()
        .workflowId(workflowEvent.getWorkflowId())
        .workflowProcessingStatus(WorkflowProcessingStatus.getProcessingStatus(workflowEvent.getWorkflowStatus()))
        .objectVersion(objectVersion)
        .build();

    ObjectVersionSnapshot objectVersionSnapshot = ObjectVersionSnapshot.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();

    //when
    workflowProcessingService.processWorkflow(workflowEvent, objectVersion, objectVersionSnapshot);
    //then
    verify(objectWorkflowRepository).save(objectWorkflowVersion);
    verify(objectVersionRepository).save(objectVersion);
    verify(objectVersionSnapshotRepository).save(any(ObjectVersionSnapshot.class));

  }

  @Test
   void shouldUpdateObjectStatusToValidated() {
    //given
    WorkflowEvent workflowEvent = BaseWorkflowProcessingServiceTest.WorkflowEvent.builder()
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
        .workflowId(workflowEvent.getWorkflowId())
        .workflowProcessingStatus(WorkflowProcessingStatus.getProcessingStatus(workflowEvent.getWorkflowStatus()))
        .objectVersion(objectVersion)
        .build();

    ObjectVersionSnapshot objectVersionSnapshot = ObjectVersionSnapshot.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();

    //when
    workflowProcessingService.processWorkflow(workflowEvent, objectVersion, objectVersionSnapshot);
    //then
    verify(objectWorkflowRepository).save(objectWorkflowVersion);
    verify(objectVersionRepository).save(objectVersion);
    assertThat(objectVersion.getStatus()).isEqualTo(Status.VALIDATED);
  }

  @Test
   void shouldUpdateObjectStatusToDraft() {
    //given
    WorkflowEvent workflowEvent = BaseWorkflowProcessingServiceTest.WorkflowEvent.builder()
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
        .workflowId(workflowEvent.getWorkflowId())
        .workflowProcessingStatus(WorkflowProcessingStatus.getProcessingStatus(workflowEvent.getWorkflowStatus()))
        .objectVersion(objectVersion)
        .build();

    ObjectVersionSnapshot objectVersionSnapshot = ObjectVersionSnapshot.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();

    //when
    workflowProcessingService.processWorkflow(workflowEvent, objectVersion, objectVersionSnapshot);
    //then
    verify(objectWorkflowRepository).save(objectWorkflowVersion);
    verify(objectVersionRepository).save(objectVersion);
    assertThat(objectVersion.getStatus()).isEqualTo(Status.DRAFT);
  }

  @Test
   void shouldNotProcessWorkflowWhenWorkflowStatusNotImplemenmted() {
    //given
    WorkflowEvent workflowEvent = BaseWorkflowProcessingServiceTest.WorkflowEvent.builder()
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
        () -> workflowProcessingService.processWorkflow(workflowEvent, objectVersion, objectVersionSnapshot));

  }

  @Test
   void shouldNotUpdateObjectStatusIfRevoked() {
    //given
    WorkflowEvent workflowEvent = BaseWorkflowProcessingServiceTest.WorkflowEvent.builder()
        .workflowId(1000L)
        .businessObjectId(1000L)
        .workflowStatus(WorkflowStatus.ADDED)
        .build();
    ObjectVersion objectVersion = ObjectVersion.builder()
        .status(Status.REVOKED)
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();
    when(objectVersionRepository.findById(1000L)).thenReturn(Optional.of(objectVersion));

    ObjectWorkflowEntityVersion objectWorkflowVersion = ObjectWorkflowEntityVersion.builder()
        .workflowId(workflowEvent.getWorkflowId())
        .workflowProcessingStatus(WorkflowProcessingStatus.getProcessingStatus(workflowEvent.getWorkflowStatus()))
        .objectVersion(objectVersion)
        .build();

    ObjectVersionSnapshot objectVersionSnapshot = ObjectVersionSnapshot.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 2, 1))
        .build();

    //when
    workflowProcessingService.processWorkflow(workflowEvent, objectVersion, objectVersionSnapshot);
    //then
    verify(objectWorkflowRepository).save(objectWorkflowVersion);
    verify(objectVersionRepository).save(objectVersion);
    assertThat(objectVersion.getStatus()).isEqualTo(Status.REVOKED);
  }

   interface ObjectVersionRepository extends JpaRepository<ObjectVersion, Long> {

  }

   interface ObjectVersionSnapshotRepository extends JpaRepository<ObjectVersionSnapshot, Long> {

  }

   static class ObjectWorkflowProcessingService extends
      BaseWorkflowProcessingService<ObjectVersion, ObjectWorkflowEntityVersion, ObjectVersionSnapshot> {

     ObjectWorkflowProcessingService(JpaRepository<ObjectVersion, Long> objectVersionRepository,
        ObjectWorkflowRepository<ObjectWorkflowEntityVersion> objectWorkflowRepository,
        JpaRepository<ObjectVersionSnapshot, Long> objectVersionSnapshotRepository) {
      super(objectVersionRepository, objectWorkflowRepository, objectVersionSnapshotRepository);
    }

    @Override
    protected ObjectWorkflowEntityVersion buildObjectVersionWorkflow(
        ch.sbb.atlas.workflow.model.WorkflowEvent workflowEvent, ObjectVersion object) {
      return ObjectWorkflowEntityVersion.builder()
          .objectVersion(object)
          .workflowId(workflowEvent.getWorkflowId())
          .workflowProcessingStatus(WorkflowProcessingStatus.IN_PROGRESS)
          .build();
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @SuperBuilder
   static class ObjectVersion extends BaseVersion {

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
   static class ObjectVersionSnapshot extends BaseVersionSnapshot {

    private LocalDate validFrom;
    private LocalDate validTo;

    private Status status;

     LocalDate getValidFrom() {
      return this.validFrom;
    }

     LocalDate getValidTo() {
      return this.validTo;
    }

    @Override
    public Status getStatus() {
      return status;
    }

    @Override
    public void setStatus(Status status) {
      this.status = status;
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @SuperBuilder
   static class ObjectWorkflowEntityVersion extends BaseWorkflowEntity {

    private ObjectVersion objectVersion;
  }
  
  @SuperBuilder
   static class WorkflowEvent extends ch.sbb.atlas.workflow.model.WorkflowEvent {
    
  }

}