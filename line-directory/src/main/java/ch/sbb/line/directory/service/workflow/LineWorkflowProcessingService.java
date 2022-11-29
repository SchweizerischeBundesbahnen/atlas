package ch.sbb.line.directory.service.workflow;

import ch.sbb.atlas.base.service.aspect.annotation.RunAsUser;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.kafka.model.workflow.event.LineWorkflowEvent;
import ch.sbb.atlas.workflow.model.WorkflowProcessingStatus;
import ch.sbb.atlas.workflow.repository.ObjectWorkflowRepository;
import ch.sbb.atlas.workflow.service.BaseWorkflowProcessingService;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.entity.LineVersionWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static ch.sbb.atlas.base.service.aspect.FakeUserType.KAFKA;
import static ch.sbb.atlas.workflow.model.WorkflowProcessingStatus.getProcessingStatus;

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

  @RunAsUser(fakeUserType = KAFKA)
  public void processLineWorkflow(LineWorkflowEvent lineWorkflowEvent) {
    log.info("Started Workflow processing: {}", lineWorkflowEvent);
    LineVersion lineVersion = objectVersionRepository.findById(lineWorkflowEvent.getBusinessObjectId())
        .orElseThrow(() -> new IdNotFoundException(lineWorkflowEvent.getBusinessObjectId()));
    LineVersionSnapshot lineVersionSnapshot = buildLineVersionSnapshot(lineWorkflowEvent, lineVersion);

    processWorkflow(lineWorkflowEvent, lineVersionSnapshot);
    log.info("Ended Workflow processing: {}", lineWorkflowEvent);
  }

  @Override
  protected LineVersionWorkflow buildObjectVersionWorkflow(LineWorkflowEvent lineWorkflowEvent, LineVersion object) {
    Optional<LineVersionWorkflow> existingLineRelation = objectWorkflowRepository.findByWorkflowId(lineWorkflowEvent.getWorkflowId());
    WorkflowProcessingStatus workflowProcessingStatus = getProcessingStatus(lineWorkflowEvent.getWorkflowStatus());

    if (existingLineRelation.isPresent()) {
      existingLineRelation.get().setWorkflowProcessingStatus(workflowProcessingStatus);
      return existingLineRelation.get();
    }

    return LineVersionWorkflow.builder()
        .workflowId(lineWorkflowEvent.getWorkflowId())
        .lineVersion(object)
        .workflowProcessingStatus(workflowProcessingStatus)
        .build();
  }

  private LineVersionSnapshot buildLineVersionSnapshot(LineWorkflowEvent lineWorkflowEvent, LineVersion lineVersion) {
    LineVersionSnapshot lineVersionSnapshot = LineVersionSnapshot.builder()
        .parentObjectId(lineVersion.getId())
        .workflowId(lineWorkflowEvent.getWorkflowId())
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
        .build();
    return lineVersionSnapshot;
  }
}
