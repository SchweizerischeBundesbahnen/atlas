package ch.sbb.line.directory.workflow.service;

import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.IN_PROGRESS;
import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.getProcessingStatus;

import ch.sbb.atlas.workflow.model.WorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.workflow.service.BaseWorkflowProcessingService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import ch.sbb.line.directory.repository.LineVersionWorkflowRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class LineWorkflowProcessingService extends
    BaseWorkflowProcessingService<LineVersion, LineVersionWorkflow, LineVersionSnapshot> {

  private final LineVersionWorkflowRepository lineVersionWorkflowRepository;

  public LineWorkflowProcessingService(JpaRepository<LineVersion, Long> objectRepository,
      LineVersionWorkflowRepository lineVersionWorkflowRepository,
      JpaRepository<LineVersionSnapshot, Long> objectVerionSnapshotRepositroy) {
    super(objectRepository, lineVersionWorkflowRepository, objectVerionSnapshotRepositroy);
    this.lineVersionWorkflowRepository = lineVersionWorkflowRepository;
  }

  @PreAuthorize("""
      @businessOrganisationBasedUserAdministrationService.hasUserPermissionsToCreate(#lineVersion, T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).LIDI)""")
  public WorkflowStatus processLineWorkflow(WorkflowEvent lineWorkflowEvent, LineVersion lineVersion) {
    log.info("Started Workflow processing: {}", lineWorkflowEvent);
    LineVersionSnapshot lineVersionSnapshot = buildLineVersionSnapshot(lineWorkflowEvent, lineVersion);
    WorkflowStatus workflowStatus = processWorkflow(lineWorkflowEvent, lineVersion, lineVersionSnapshot);
    log.info("Ended Workflow processing: {}", lineWorkflowEvent);
    return workflowStatus;
  }

  @Override
  protected LineVersionWorkflow buildObjectVersionWorkflow(WorkflowEvent workflowEvent, LineVersion object) {
    Optional<LineVersionWorkflow> existingLineRelation = objectWorkflowRepository.findByWorkflowId(workflowEvent.getWorkflowId());
    WorkflowProcessingStatus workflowProcessingStatus = getProcessingStatus(workflowEvent.getWorkflowStatus());

    checkThatOnlyOneWorkflowIsInProgress(object, workflowProcessingStatus);

    if (existingLineRelation.isPresent()) {
      existingLineRelation.get().setLineVersion(object);
      existingLineRelation.get().setWorkflowProcessingStatus(workflowProcessingStatus);
      return existingLineRelation.get();
    }

    return LineVersionWorkflow.builder()
        .workflowId(workflowEvent.getWorkflowId())
        .lineVersion(object)
        .workflowProcessingStatus(workflowProcessingStatus)
        .build();
  }

  private void checkThatOnlyOneWorkflowIsInProgress(LineVersion object, WorkflowProcessingStatus newStatus) {
    boolean hasWorkflowInProgress = lineVersionWorkflowRepository.findAllByLineVersion(object)
        .stream().anyMatch(i -> i.getWorkflowProcessingStatus() == IN_PROGRESS);

    if (newStatus == WorkflowProcessingStatus.IN_PROGRESS && hasWorkflowInProgress) {
      throw new IllegalStateException("There is max one workflow allowed to be in progress");
    }
  }

  private LineVersionSnapshot buildLineVersionSnapshot(WorkflowEvent workflowEvent, LineVersion lineVersion) {
    return LineVersionSnapshot.builder()
        .parentObjectId(lineVersion.getId())
        .concessionType(lineVersion.getConcessionType())
        .offerCategory(lineVersion.getOfferCategory())
        .shortNumber(lineVersion.getShortNumber())
        .workflowId(workflowEvent.getWorkflowId())
        .workflowStatus(workflowEvent.getWorkflowStatus())
        .validFrom(lineVersion.getValidFrom())
        .validTo(lineVersion.getValidTo())
        .status(lineVersion.getStatus())
        .swissLineNumber(lineVersion.getSwissLineNumber())
        .slnid(lineVersion.getSlnid())
        .lineType(lineVersion.getLineType())
        .paymentType(lineVersion.getPaymentType())
        .number(lineVersion.getNumber())
        .longName(lineVersion.getLongName())
        .description(lineVersion.getDescription())
        .businessOrganisation(lineVersion.getBusinessOrganisation())
        .comment(lineVersion.getComment())
        .creator(lineVersion.getCreator())
        .creationDate(lineVersion.getCreationDate())
        .editor(lineVersion.getEditor())
        .editionDate(lineVersion.getEditionDate())
        .version(lineVersion.getVersion())
        .build();
  }
}
