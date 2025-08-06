package ch.sbb.workflow.sepodi.termination.service;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TerminationWorkflowSubject {

  public static final String START_TERMINATION_WORKFLOW_SUBJECT = """
      Aufhebungsantrag erstellt / Demande de terminaison créée / Richiesta di eliminazione creata
      """;

  public static final String TARIFF_STOP_NOT_APPROVED_SUBJECT = """
      Aufhebungsantrag abgelehnt / Demande de terminaison refusé/ Richiesta di eliminazione rifituato
      """;
  
  public static final String TARIFF_STOP_APPROVED_SUBJECT = """
      Haltestellenaufhebung bestätigt / Suppression de l'arrêt confirmée / Confermata la soppressione della fermata
      """;

  public static final String ABORT_TERMINATION_SUBJECT = """
      Aufhebungsantrag abgebrochen / Demande de terminaison annulé / Richiesta di eliminazione annullato
      """;

}
