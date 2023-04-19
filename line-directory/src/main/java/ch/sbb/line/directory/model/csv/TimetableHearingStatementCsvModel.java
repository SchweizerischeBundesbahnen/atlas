package ch.sbb.line.directory.model.csv;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.export.model.VersionCsvModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimetableHearingStatementCsvModel implements VersionCsvModel {

  @JsonProperty("Kanton")
  private String cantonAbbreviation;

  @JsonProperty("Feld-Nr.")
  private String timetableFieldNumber;

  @JsonProperty("Fahrplanfeldbezeichnung")
  private String timetableFieldNumberDescription;

  @JsonProperty("Haltestelle")
  private String stopPoint;

  @JsonProperty("Abkürzung Transportunternehmung")
  private String transportCompanyAbbreviations;

  @JsonProperty("Name Transportunternehmung")
  private String transportCompanyDescriptions;

  @JsonProperty("Stellungnahme")
  private String statement;

  @JsonProperty("Anhang")
  private Boolean documentsPresent;

  @JsonProperty("Begründung")
  private String justification;

  @JsonProperty("Vorname")
  private String firstName;

  @JsonProperty("Nachname")
  private String lastName;

  @JsonProperty("Organisation")
  private String organisation;

  @JsonProperty("Strasse")
  private String street;

  @JsonProperty("PLZ/Ort")
  private Integer zip;

  @JsonProperty("E-Mail")
  private String email;

  @JsonProperty("Bearbeiter")
  private String editor;

  @JsonProperty("Zuletzt bearbeitet")
  private LocalDateTime editionDate;

  @JsonProperty("Fahrplanjahr")
  private Long timetableHearingYear;

  public static TimetableHearingStatementCsvModel fromModel(TimetableHearingStatementModel timetableHearingStatementModel) {
    return TimetableHearingStatementCsvModel.builder()
        .cantonAbbreviation(timetableHearingStatementModel.getSwissCanton() == null ? null :
            timetableHearingStatementModel.getSwissCanton().getAbbreviation())
        .timetableFieldNumber(timetableHearingStatementModel.getTimetableFieldNumber())
        .timetableFieldNumberDescription(timetableHearingStatementModel.getTimetableFieldDescription())
        .stopPoint(timetableHearingStatementModel.getStopPlace())
        .transportCompanyAbbreviations(timetableHearingStatementModel.getResponsibleTransportCompanies().stream().map(
            TimetableHearingStatementResponsibleTransportCompanyModel::getAbbreviation).collect(Collectors.joining(",")))
        .transportCompanyDescriptions(timetableHearingStatementModel.getResponsibleTransportCompanies().stream().map(
            TimetableHearingStatementResponsibleTransportCompanyModel::getBusinessRegisterName).collect(Collectors.joining(",")))
        .statement(timetableHearingStatementModel.getStatement())
        .documentsPresent(!timetableHearingStatementModel.getDocuments().isEmpty())
        .justification(timetableHearingStatementModel.getJustification())
        .firstName(timetableHearingStatementModel.getStatementSender().getFirstName())
        .lastName(timetableHearingStatementModel.getStatementSender().getLastName())
        .organisation(timetableHearingStatementModel.getStatementSender().getOrganisation())
        .street(timetableHearingStatementModel.getStatementSender().getStreet())
        .zip(timetableHearingStatementModel.getStatementSender().getZip())
        .email(timetableHearingStatementModel.getStatementSender().getEmail())
        .editor(timetableHearingStatementModel.getEditor())
        .editionDate(timetableHearingStatementModel.getEditionDate())
        .timetableHearingYear(timetableHearingStatementModel.getTimetableYear())
        .build();
  }
}
