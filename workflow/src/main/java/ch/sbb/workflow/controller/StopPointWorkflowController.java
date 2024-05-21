package ch.sbb.workflow.controller;

import ch.sbb.atlas.api.workflow.StopPointWorkflowStartModel;
import ch.sbb.workflow.api.StopPointWorkflowApiV1;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.mapper.StopPointWorkflowMapper;
import ch.sbb.workflow.service.StopPointWorkflowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPointWorkflowController implements StopPointWorkflowApiV1 {

  private final StopPointWorkflowService service;

  @Override
  public StopPointWorkflowStartModel getWorkflow(Long id) {
    return StopPointWorkflowMapper.toModel(service.getWorkflow(id));
  }

  @Override
  public List<StopPointWorkflowStartModel> getWorkflows() {
    return service.getWorkflows().stream().map(StopPointWorkflowMapper::toModel).toList();
  }

  @Override
  public StopPointWorkflowStartModel startWorkflow(StopPointWorkflowStartModel workflowStartModel) {
    log.info("Starting workflow");

    StopPointWorkflow stopPointWorkflow = service.startWorkflow(StopPointWorkflowMapper.toEntity(workflowStartModel));
    return StopPointWorkflowMapper.toModel(stopPointWorkflow);
  }

}
