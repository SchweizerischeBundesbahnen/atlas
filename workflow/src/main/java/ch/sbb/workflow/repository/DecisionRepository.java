package ch.sbb.workflow.repository;

import ch.sbb.workflow.entity.Decision;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, Long> {

  Decision findDecisionByExaminantId(Long personId);
  
  @Query(value = """
      select d.* from decision d
      join person p on p.id = d.examinant_id or p.id = d.fot_overrider_id
      join stop_point_workflow spw on spw.id = p.stop_point_workflow_id
      where spw.id = :workflowId;
      """, nativeQuery = true)
  Set<Decision> findDecisionByWorkflowId(@Param("workflowId") Long workflowId);

}
