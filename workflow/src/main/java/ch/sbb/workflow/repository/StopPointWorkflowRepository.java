package ch.sbb.workflow.repository;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.StopPointWorkflow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StopPointWorkflowRepository extends JpaRepository<StopPointWorkflow, Long>,
    JpaSpecificationExecutor<StopPointWorkflow> {

  List<StopPointWorkflow> findAllByVersionIdAndStatus(Long businessObjectId, WorkflowStatus status);

  @Query(value = """
      select spw.* from stop_point_workflow spw
      where spw.follow_up_workflow_id = :id
      """, nativeQuery = true)
  Optional<StopPointWorkflow> findStopPointWorkflowByFollowUpWorkflow(Long id);

  @Query(value = """
      select spw.* from stop_point_workflow spw
      join person on spw.id = person.stop_point_workflow_id
      join decision d on person.id = d.examinant_id
      where d.id = :decisionId
      """, nativeQuery = true)
  StopPointWorkflow findByDecisionId(Long decisionId);

  @Query(value = """
      select spw.* from stop_point_workflow spw
      join person on spw.id = person.stop_point_workflow_id
      where person.id = :personId
      """, nativeQuery = true)
  Optional<StopPointWorkflow> findByPersonId(Long personId);

  @Query(value = """
      select spw.* from stop_point_workflow spw
      where spw.status =:#{#status.toString()}
      """, nativeQuery = true)
  List<StopPointWorkflow> findWorkflowsByStatus(@Param("status") WorkflowStatus status);

}
