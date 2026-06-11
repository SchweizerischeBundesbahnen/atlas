package ch.sbb.workflow.module.lidi.tth.model;

import ch.sbb.atlas.api.timetable.hearing.model.TimetableHearingAnonymStatementCsvModel;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossier;
import ch.sbb.workflow.module.lidi.tth.entity.TthDossierQuestion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"dossierId", "dossierStatus", "dossierTopic", "questionForTU", "answerFromTU",
    "timetableHearingStatementId", "timetableFieldNumber", "timetableFieldNumberDescription", "stopPlace",
    "transportCompanyAbbreviation", "transportCompanyName", "statement", "documentsPresent", "timetableYear", "canton"
})
public class TthDossierTuCsvModel {

  private Long dossierId;
  private DossierStatus dossierStatus;
  private String dossierTopic;
  private String questionForTU;
  private String answerFromTU;
  private Long timetableHearingStatementId;
  private String timetableFieldNumber;
  private String timetableFieldNumberDescription;
  private String stopPlace;
  private String transportCompanyAbbreviation;
  private String transportCompanyName;
  private String statement;
  private Boolean documentsPresent;
  private Long timetableYear;
  private SwissCanton canton;

  public static TthDossierTuCsvModel fromDossierAndStatement(TthDossier dossier,
      TimetableHearingAnonymStatementCsvModel statement) {
    Optional<TthDossierQuestion> question = dossier.getDossierQuestions().stream().findFirst();
    return TthDossierTuCsvModel.builder()
        .dossierId(dossier.getId())
        .dossierStatus(dossier.getDossierStatus())
        .dossierTopic(dossier.getTopic())
        .questionForTU(question.map(TthDossierQuestion::getQuestion).orElse(null))
        .answerFromTU(question.map(TthDossierQuestion::getAnswerToCanton).orElse(null))
        .timetableHearingStatementId(statement.getTimetableHearingStatementId())
        .timetableFieldNumber(statement.getTimetableFieldNumber())
        .timetableFieldNumberDescription(statement.getTimetableFieldNumberDescription())
        .stopPlace(statement.getStopPlace())
        .transportCompanyAbbreviation(statement.getTransportCompanyAbbreviations())
        .transportCompanyName(statement.getTransportCompanyDescriptions())
        .statement(statement.getStatement())
        .documentsPresent(statement.getDocumentsPresent())
        .timetableYear(statement.getTimetableHearingYear())
        .canton(dossier.getSwissCanton())
        .build();
  }
}
