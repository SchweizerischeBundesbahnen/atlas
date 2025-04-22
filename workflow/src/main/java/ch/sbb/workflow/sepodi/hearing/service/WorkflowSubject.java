package ch.sbb.workflow.sepodi.hearing.service;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class WorkflowSubject {

  public static final String START_WORKFLOW_SUBJECT = """
      Neue Anhörung zu Stationsnamen / Nouvelle audition portant sur un nom de station / Nome della stazione nuova audizione
      """;
  public static final String RESTART_WORKFLOW_SUBJECT = """
      Stationsnamen erneut überprüfen / Nom de station réexamen / Esaminare di nuovo il nome della stazione
      """;
  public static final String REJECT_WORKFLOW_SUBJECT = """
      Stationsname zurückgewiesen / Nom de station rejeté / Nome della stazione respinto
      """;
  public static final String PINCODE_SUBJECT = """
      Stationsnamen PIN-Code / Nom de station PIN-Code / Nome della stazione codice PIN
      """;
  public static final String APPROVED_WORKFLOW_SUBJECT = """
      Stationsnamen Anhörung abgeschlossen / Nom de station audition terminée / Audizione nome della stazione conclusa
      """;
  public static final String CANCEL_WORKFLOW_SUBJECT = """
      Anhörung abgebrochen / Audition du nom de la station annulée / Audizione del nome della stazione cancellata
      """;

}
