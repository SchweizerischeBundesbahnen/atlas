package ch.sbb.atlas.api.timetable.hearing.model;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementSenderModelV2;
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
    "statementAnonymous", "anonymousStatement", "documentsPresent", "firstName", "lastName", "organisation", "street",
    "zipAndCity", "emails", "timetableHearingYear", "publicComment", "internalComment", "topic"})
public class TimetableHearingStatementCsvModel extends BaseTimetableHearingStatementCsv {

  private String statement;
  private String firstName;
  private String lastName;
  private String organisation;
  private String street;
  private String zipAndCity;
  private String emails;

  private Boolean statementAnonymous;
  private String anonymousStatement;
  private String publicComment;
  private String internalComment;

  private static TimetableHearingStatementCsvModel.TimetableHearingStatementCsvModelBuilder baseBuilder(
      TimetableHearingStatementModelV2 statementModelV2
  ) {
    return TimetableHearingStatementCsvModel.builder()
        .cantonAbbreviation(statementModelV2.getSwissCanton().getAbbreviation())
        .timetableFieldNumber(statementModelV2.getTimetableFieldNumber())
        .timetableFieldNumberDescription(statementModelV2.getTimetableFieldDescription())
        .stopPlace(statementModelV2.getStopPlace())
        .timetableHearingStatementId(statementModelV2.getId())
        .transportCompanyAbbreviations(
            statementModelV2.getResponsibleTransportCompanies().stream()
                .map(TimetableHearingStatementResponsibleTransportCompanyModel::getAbbreviation)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(",")))
        .transportCompanyDescriptions(
            statementModelV2.getResponsibleTransportCompanies().stream()
                .map(TimetableHearingStatementResponsibleTransportCompanyModel::getBusinessRegisterName)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(",")))
        .documentsPresent(!statementModelV2.getDocuments().isEmpty())
        .status(statementModelV2.getStatementStatus())
        .timetableHearingYear(statementModelV2.getTimetableYear())
        .statementAnonymous(statementModelV2.getStatementAnonymous())
        .topic(statementModelV2.getTopic());
  }
  
  public static TimetableHearingStatementCsvModel fromModel(TimetableHearingStatementModelV2 timetableHearingStatementModelV2) {
    TimetableHearingStatementSenderModelV2 sender = timetableHearingStatementModelV2.getStatementSender();

    return baseBuilder(timetableHearingStatementModelV2)
        .firstName(sender.getFirstName())
        .lastName(sender.getLastName())
        .organisation(sender.getOrganisation())
        .street(sender.getStreet())
        .zipAndCity(getZipAndCity(sender.getZip(), sender.getCity()))
        .emails(sender.getEmails().stream().sorted().collect(Collectors.joining(",")))
        .statement(timetableHearingStatementModelV2.getStatement())
        .anonymousStatement(timetableHearingStatementModelV2.getAnonymousStatement())
        .publicComment(timetableHearingStatementModelV2.getPublicComment())
        .internalComment(timetableHearingStatementModelV2.getInternalComment())
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
