package ch.sbb.workflow.sepodi.termination.repository;

import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;

@Redacted
public interface TerminationStopPointWorkflowRepository extends JpaRepository<TerminationStopPointWorkflow, Long> {

}
