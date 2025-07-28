package ch.sbb.workflow.sepodi.termination;

import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.model.TerminationInfoModel;
import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TerminationWorkflowHelper {

  public static LocalDate getTerminationDate(TerminationStopPointWorkflow workflow) {
    return calculateTerminationDate(workflow).getTerminationDate();
  }

  public static TerminationInfoModel calculateTerminationDate(TerminationStopPointWorkflow terminationWorkflow) {
    TerminationInfoModel infoModel = new TerminationInfoModel();
    infoModel.setWorkflowId(terminationWorkflow.getId());
    TerminationDecision infoPlusDecision = terminationWorkflow.getInfoPlusDecision();
    TerminationDecision novaDecision = terminationWorkflow.getNovaDecision();
    if (infoPlusDecision.getJudgement() == null && novaDecision.getJudgement() == null) {
      infoModel.setTerminationDate(terminationWorkflow.getBoTerminationDate());
    }
    if ((infoPlusDecision.getJudgement() != null && novaDecision.getJudgement() == null) ||
        (novaDecision.getJudgement() != null && infoPlusDecision.getJudgement() != null)) {
      if (infoPlusDecision.getJudgement() == JudgementType.YES) {
        infoModel.setTerminationDate(terminationWorkflow.getInfoPlusTerminationDate());
      } else {
        throw new IllegalStateException("When InfoPlus vote No, the termination process is done!");
      }
    }
    return infoModel;
  }

}
