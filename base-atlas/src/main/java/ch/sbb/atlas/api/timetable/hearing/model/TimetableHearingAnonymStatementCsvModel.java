package ch.sbb.atlas.api.timetable.hearing.model;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"timetableHearingStatementId", "cantonAbbreviation", "status", "timetableFieldNumber",
    "timetableFieldNumberDescription", "stopPlace", "transportCompanyAbbreviations", "transportCompanyDescriptions", "statement",
    "documentsPresent", "timetableHearingYear", "topic"})
public class TimetableHearingAnonymStatementCsvModel extends BaseTimetableHearingStatementCsv {

  private String statement;

  public static TimetableHearingAnonymStatementCsvModel fromModelAnonymized(
      TimetableHearingStatementModelV2 model
  ) {
    return TimetableHearingAnonymStatementCsvModel.builder()
        .statement(Boolean.TRUE.equals(model.getStatementAnonymous()) ? model.getStatement() : model.getAnonymousStatement())
        .cantonAbbreviation(model.getSwissCanton().getAbbreviation())
        .timetableFieldNumber(model.getTimetableFieldNumber())
        .timetableFieldNumberDescription(model.getTimetableFieldDescription())
        .stopPlace(model.getStopPlace())
        .timetableHearingStatementId(model.getId())
        .transportCompanyAbbreviations(
            model.getResponsibleTransportCompanies().stream()
                .map(TimetableHearingStatementResponsibleTransportCompanyModel::getAbbreviation)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(",")))
        .transportCompanyDescriptions(
            model.getResponsibleTransportCompanies().stream()
                .map(TimetableHearingStatementResponsibleTransportCompanyModel::getBusinessRegisterName)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(",")))
        .documentsPresent(!model.getDocuments().isEmpty())
        .status(model.getStatementStatus())
        .timetableHearingYear(model.getTimetableYear())
        .topic(model.getTopic())
        .build();
  }

}
