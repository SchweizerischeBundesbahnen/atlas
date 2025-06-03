package ch.sbb.workflow.sepodi.termination.repository;

import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Redacted
public interface TerminationStopPointWorkflowRepository extends JpaRepository<TerminationStopPointWorkflow, Long>,
    JpaSpecificationExecutor<TerminationStopPointWorkflow> {

  List<TerminationStopPointWorkflow> findTerminationStopPointWorkflowBySloidAndVersionIdAndStatus(String sloid, Long versionId,
      TerminationWorkflowStatus workflowStatus);

  Optional<TerminationStopPointWorkflow> findTerminationStopPointWorkflowBySloid(String sloid);

}
