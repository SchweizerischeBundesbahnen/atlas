package ch.sbb.workflow.service.lidi;

import ch.sbb.workflow.entity.LineWorkflow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LineWorkflowSubject {

  private static final String LINE_START_WORKFLOW_SUBJECT = "Antrag prüfen zu / vérifier la demande de  / controllare la "
      + "richiesta per: ";
  private static final String LINE_APPROVED_WORKFLOW_SUBJECT = "Antrag genehmigt / demande approuvée / richiesta approvata: ";
  private static final String LINE_REJECTED_WORKFLOW_SUBJECT = "Antrag zurückgewiesen / Demande rejetée / Domanda respinta: ";

  String getSubject(LineWorkflow lineWorkflow) {
    return switch (lineWorkflow.getStatus()) {
      case STARTED -> LineWorkflowSubject.LINE_START_WORKFLOW_SUBJECT;
      case APPROVED -> LineWorkflowSubject.LINE_APPROVED_WORKFLOW_SUBJECT;
      case REJECTED -> LineWorkflowSubject.LINE_REJECTED_WORKFLOW_SUBJECT;
      default -> throw new IllegalArgumentException();
    };
  }

}
