package ch.sbb.line.directory.model.csv;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.export.model.VersionCsvModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
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
@JsonPropertyOrder({"cantonAbbreviation", "timetableFieldNumber", "timetableFieldNumberDescription", "stopPlace",
    "timetabeHearingStatementId", "transportCompanyAbbreviations", "transportCompanyDescriptions", "statement",
    "documentsPresent", "status", "justification", "firstName", "lastName", "organisation",
    "street", "zipAndCity", "email", "editor", "editionDate", "timetableHearingYear"})
public class TimetableHearingStatementCsvModel implements VersionCsvModel {

  private String cantonAbbreviation;
  private String timetableFieldNumber;
  private String timetableFieldNumberDescription;
  private String stopPlace;
  private Long timetabeHearingStatementId;
  private String transportCompanyAbbreviations;
  private String transportCompanyDescriptions;
  private String statement;
  private Boolean documentsPresent;
  private StatementStatus status;
  private String justification;
  private String firstName;
  private String lastName;
  private String organisation;
  private String street;
  private String zipAndCity;
  private String email;
  private Set<String> emails;
  private String editor;
  private LocalDateTime editionDate;
  private Long timetableHearingYear;

  public static TimetableHearingStatementCsvModel fromModel(TimetableHearingStatementModelV2 timetableHearingStatementModel) {

    return TimetableHearingStatementCsvModel.builder()
        .cantonAbbreviation(timetableHearingStatementModel.getSwissCanton().getAbbreviation())
        .timetableFieldNumber(timetableHearingStatementModel.getTimetableFieldNumber())
        .timetableFieldNumberDescription(timetableHearingStatementModel.getTimetableFieldDescription())
        .stopPlace(timetableHearingStatementModel.getStopPlace())
        .timetabeHearingStatementId(timetableHearingStatementModel.getId())
        .transportCompanyAbbreviations(
            timetableHearingStatementModel.getResponsibleTransportCompanies().stream()
                .map(TimetableHearingStatementResponsibleTransportCompanyModel::getAbbreviation)
                .filter(Objects::nonNull)
                .sorted().collect(Collectors.joining(",")))
        .transportCompanyDescriptions(timetableHearingStatementModel.getResponsibleTransportCompanies().stream()
            .map(TimetableHearingStatementResponsibleTransportCompanyModel::getBusinessRegisterName)
            .filter(Objects::nonNull)
            .sorted().collect(Collectors.joining(",")))
        .statement(timetableHearingStatementModel.getStatement())
        .documentsPresent(!timetableHearingStatementModel.getDocuments().isEmpty())
        .status(timetableHearingStatementModel.getStatementStatus())
        .justification(timetableHearingStatementModel.getJustification())
        .firstName(timetableHearingStatementModel.getStatementSender().getFirstName())
        .lastName(timetableHearingStatementModel.getStatementSender().getLastName())
        .organisation(timetableHearingStatementModel.getStatementSender().getOrganisation())
        .street(timetableHearingStatementModel.getStatementSender().getStreet())
        .zipAndCity(getZipAndCity(timetableHearingStatementModel.getStatementSender().getZip(),
            timetableHearingStatementModel.getStatementSender().getCity()))
        .emails(timetableHearingStatementModel.getStatementSender().getEmails())
        .editor(timetableHearingStatementModel.getEditor())
        .editionDate(timetableHearingStatementModel.getEditionDate())
        .timetableHearingYear(timetableHearingStatementModel.getTimetableYear())
        .build();
  }

  public static String getZipAndCity(Integer zip, String city) {
    if (zip == null) {
      return Objects.requireNonNullElse(city, "");
    } else if (city == null || city.isEmpty()) {
      return zip.toString();
    } else {
      return zip + "/" + city;
    }
  }

}
