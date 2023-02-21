package ch.sbb.line.directory.workflow.service;

import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.getProcessingStatus;

import ch.sbb.atlas.base.service.model.workflow.WorkflowEvent;
import ch.sbb.atlas.base.service.model.workflow.WorkflowStatus;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.atlas.workflow.repository.ObjectWorkflowRepository;
import ch.sbb.atlas.workflow.service.BaseWorkflowProcessingService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import java.util.Optional;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class LineWorkflowProcessingService extends
    BaseWorkflowProcessingService<LineVersion, LineVersionWorkflow, LineVersionSnapshot> {


  public LineWorkflowProcessingService(JpaRepository<LineVersion, Long> objectRepository,
      ObjectWorkflowRepository<LineVersionWorkflow> objectWorkflowRepository,
      JpaRepository<LineVersionSnapshot, Long> objectVerionSnapshotRepositroy) {
    super(objectRepository, objectWorkflowRepository, objectVerionSnapshotRepositroy);
  }

  @PreAuthorize("@userAdministrationService.hasUserPermissionsToCreate(#lineVersion, T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).LIDI)")
  public WorkflowStatus processLineWorkflow(WorkflowEvent lineWorkflowEvent, LineVersion lineVersion) {
    log.info("Started Workflow processing: {}", lineWorkflowEvent);
    LineVersionSnapshot lineVersionSnapshot = buildLineVersionSnapshot(lineWorkflowEvent, lineVersion);
    WorkflowStatus workflowStatus =  processWorkflow(lineWorkflowEvent, lineVersion, lineVersionSnapshot);
    log.info("Ended Workflow processing: {}", lineWorkflowEvent);
    return workflowStatus;
  }

  @Override
  protected LineVersionWorkflow buildObjectVersionWorkflow(WorkflowEvent workflowEvent, LineVersion object) {
    Optional<LineVersionWorkflow> existingLineRelation = objectWorkflowRepository.findByWorkflowId(workflowEvent.getWorkflowId());
    WorkflowProcessingStatus workflowProcessingStatus = getProcessingStatus(workflowEvent.getWorkflowStatus());

    if (existingLineRelation.isPresent()) {
      existingLineRelation.get().setWorkflowProcessingStatus(workflowProcessingStatus);
      return existingLineRelation.get();
    }

    return LineVersionWorkflow.builder()
        .workflowId(workflowEvent.getWorkflowId())
        .lineVersion(object)
        .workflowProcessingStatus(workflowProcessingStatus)
        .build();
  }

  private LineVersionSnapshot buildLineVersionSnapshot(WorkflowEvent workflowEvent, LineVersion lineVersion) {
    return LineVersionSnapshot.builder()
        .parentObjectId(lineVersion.getId())
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
        .alternativeName(lineVersion.getAlternativeName())
        .combinationName(lineVersion.getCombinationName())
        .longName(lineVersion.getLongName())
        .colorFontRgb(lineVersion.getColorFontRgb())
        .colorBackRgb(lineVersion.getColorBackRgb())
        .colorFontCmyk(lineVersion.getColorFontCmyk())
        .colorBackCmyk(lineVersion.getColorBackCmyk())
        .icon(lineVersion.getIcon())
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
