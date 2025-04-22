package ch.sbb.workflow.sepodi.termination.controller;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.workflow.sepodi.termination.api.TerminationStopPointWorkflowApi;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.termination.model.StartTerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.service.TerminationStopPointWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TerminationStopPointWorkflowInternalController implements TerminationStopPointWorkflowApi {

  private final TerminationStopPointWorkflowService service;

  @Override
  public TerminationStopPointWorkflowModel getTerminationStopPointWorkflow(Long id) {
    return TerminationStopPointWorkflowMapper.toModel(service.getTerminationWorkflow(id));
  }

  @Override
  public TerminationStopPointWorkflowModel startTerminationStopPointWorkflow(
      StartTerminationStopPointWorkflowModel workflowModel) {
    ClientPersonModel infoPlusExaminant = ClientPersonModel.builder()
        .firstName("Info+")
        .lastName("Examinant")
        .personFunction("Examinant")
        .mail("info@examinant.com")
        .build();
    workflowModel.setInfoPlusExaminant(infoPlusExaminant);

    return TerminationStopPointWorkflowMapper.toModel(service.startTerminationWorkflow(workflowModel));
  }

}
