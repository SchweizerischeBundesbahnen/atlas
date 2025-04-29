package ch.sbb.workflow.sepodi.termination.repository;

import ch.sbb.atlas.redact.Redacted;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Redacted
public interface TerminationStopPointWorkflowRepository extends JpaRepository<TerminationStopPointWorkflow, Long> {

  //
  //  @Query(value = """
  //      select tspw.* from termination_stop_point_workflow tspw
  //      where tspw.sloid = :sloid
  //            and tspw.version_id = :version_id
  //            and tspw.status = :workflowStatus
  //      """, nativeQuery = true)
  //  Optional<TerminationStopPointWorkflow> findTerminationStopPointWorkflowBySloidVersionIdAndStatus(String sloid, Long
  //  versionId
  //      , TerminationWorkflowStatus workflowStatus);
  //
  List<TerminationStopPointWorkflow> findTerminationStopPointWorkflowBySloidAndVersionIdAndStatus(String sloid, Long versionId
      , TerminationWorkflowStatus workflowStatus);

  boolean existsTerminationStopPointWorkflowBySloid(String sloid);
}
