package ch.sbb.workflow.sepodi.termination;

import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TerminationHelper {

  public static LocalDate getTerminationDate(TerminationStopPointWorkflow workflow) {
    if (workflow.getBoTerminationDate().isEqual(workflow.getInfoPlusTerminationDate())
        && workflow.getBoTerminationDate().isEqual(workflow.getNovaTerminationDate())) {
      return workflow.getBoTerminationDate();
    }
    return workflow.getNovaTerminationDate();
  }

}
