package ch.sbb.workflow.sepodi.termination.repository;

import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Redacted
public interface TerminationStopPointWorkflowRepository extends JpaRepository<TerminationStopPointWorkflow, Long> {

  @Query(value = """
      select tspw.* from termination_stop_point_workflow tspw
      where tspw.sloid = :sloid
      """, nativeQuery = true)
  Optional<TerminationStopPointWorkflow> findTerminationStopPointWorkflowBySloid(String sloid);

  boolean existsTerminationStopPointWorkflowBySloid(String sloid);
}
