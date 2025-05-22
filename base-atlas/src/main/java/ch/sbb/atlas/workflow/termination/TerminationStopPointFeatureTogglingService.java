package ch.sbb.atlas.workflow.termination;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Setter
@Slf4j
public class TerminationStopPointFeatureTogglingService {

  @Value("${termination-workflow-enabled}")
  private boolean terminationWorkflowEnabled;

  public void checkIsFeatureEnabled() {
    if (!terminationWorkflowEnabled) {
      throw new UnsupportedOperationException("TerminationWorkflow is not enabled");
    }
  }

}
