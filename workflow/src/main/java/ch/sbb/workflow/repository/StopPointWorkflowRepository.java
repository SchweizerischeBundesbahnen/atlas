package ch.sbb.workflow.repository;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.StopPointWorkflow;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StopPointWorkflowRepository extends JpaRepository<StopPointWorkflow, Long>, JpaSpecificationExecutor<StopPointWorkflow> {

  List<StopPointWorkflow> findAllByVersionIdAndStatus(Long businessObjectId, WorkflowStatus status);

}
