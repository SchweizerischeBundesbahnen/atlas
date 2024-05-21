package ch.sbb.workflow.workflow;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.LineWorkflow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<LineWorkflow, Long> {

  List<LineWorkflow> findAllByBusinessObjectIdAndStatus(Long businessObjectId, WorkflowStatus status);

}
