package ch.sbb.workflow.sepodi.termination.service;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.client.SePoDiAdminClient;
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
  private final SePoDiAdminClient sePoDiAdminClient;

  public TerminationStopPointWorkflow startTerminationWorkflow(TerminationStopPointWorkflowModel model) {
    if (repository.existsTerminationStopPointWorkflowBySloid(model.getSloid())) {
      throw new IllegalStateException("Termination Stop Point workflow already exists");
    }
    sePoDiAdminClient.postServicePointTerminationStatusUpdate(model.getSloid(), model.getVersionId());

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
