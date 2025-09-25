package ch.sbb.workflow.module.sepodi.termination.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.atlas.workflow.termination.TerminationStopPointFeatureTogglingService;
import ch.sbb.workflow.module.sepodi.termination.api.TerminationStopPointWorkflowApiV1;
import ch.sbb.workflow.module.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.module.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.module.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationExaminants;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationExaminants.InfoPlus;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationExaminants.Nova;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowFilterParams;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.module.sepodi.termination.model.TerminationStopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.module.sepodi.termination.service.TerminationStopPointWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TerminationStopPointWorkflowV1Controller implements TerminationStopPointWorkflowApiV1 {

  private final TerminationStopPointWorkflowService service;
  private final TerminationStopPointFeatureTogglingService terminationStopPointFeatureTogglingService;
  private final TerminationExaminants terminationExaminants;

  @Override
  public Container<TerminationStopPointWorkflowModel> getTerminationStopPointWorkflows(Pageable pageable,
      TerminationStopPointWorkflowFilterParams filterParams) {
    TerminationStopPointWorkflowSearchRestrictions terminationStopPointWorkflowSearchRestrictions =
        TerminationStopPointWorkflowSearchRestrictions.builder()
            .pageable(pageable)
            .filterParams(filterParams)
            .build();
    Page<TerminationStopPointWorkflow> workflows = service.getTerminationWorkflows(
        terminationStopPointWorkflowSearchRestrictions);

    return Container.<TerminationStopPointWorkflowModel>builder()
        .objects(workflows.stream().map(TerminationStopPointWorkflowMapper::toModel).toList())
        .totalCount(workflows.getTotalElements())
        .build();
  }

  @Redacted
  @Override
  public TerminationStopPointWorkflowModel getTerminationStopPointWorkflow(Long id) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    InfoPlus infoPlus = terminationExaminants.getInfoPlus();
    Nova nova = terminationExaminants.getNova();
    return TerminationStopPointWorkflowMapper.toModel(service.getTerminationWorkflow(id), infoPlus, nova);
  }


  /**
   * Permission check on ServicePointVersion#updateStopPointTerminationStatus
   */
  @Redacted
  @Override
  public TerminationStopPointWorkflowModel startTerminationStopPointWorkflow(
      StartTerminationStopPointWorkflowModel workflowModel) {
    terminationStopPointFeatureTogglingService.checkIsFeatureEnabled();
    return TerminationStopPointWorkflowMapper.toModel(service.startTerminationWorkflow(workflowModel));
  }

}
