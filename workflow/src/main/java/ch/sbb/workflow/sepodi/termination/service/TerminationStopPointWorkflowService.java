package ch.sbb.workflow.sepodi.termination.service;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.mapper.TerminationStopPointWorkflowMapper;
import ch.sbb.workflow.sepodi.termination.model.TerminationStopPointWorkflowModel;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TerminationStopPointWorkflowService {

  private final TerminationStopPointWorkflowRepository repository;

  public TerminationStopPointWorkflow startTerminationWorkflow(TerminationStopPointWorkflowModel model) {
    TerminationStopPointWorkflow terminationStopPointWorkflow = TerminationStopPointWorkflowMapper.toEntity(model);
    terminationStopPointWorkflow.setStatus(TerminationWorkflowStatus.STARTED);
    terminationStopPointWorkflow.setNovaTerminationDate(terminationStopPointWorkflow.getBoTerminationDate());
    terminationStopPointWorkflow.setInfoPlusTerminationDate(terminationStopPointWorkflow.getBoTerminationDate());
    return repository.save(terminationStopPointWorkflow);
  }

  @Redacted
  public TerminationStopPointWorkflow getTerminationWorkflow(Long id) {
    return repository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

}
