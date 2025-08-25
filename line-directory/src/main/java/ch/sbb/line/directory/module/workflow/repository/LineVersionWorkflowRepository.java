package ch.sbb.line.directory.module.workflow.repository;

import ch.sbb.atlas.workflow.repository.ObjectWorkflowRepository;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.workflow.entity.LineVersionWorkflow;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface LineVersionWorkflowRepository extends ObjectWorkflowRepository<LineVersionWorkflow> {

  List<LineVersionWorkflow> findAllByLineVersion(LineVersion lineVersion);
}
