package ch.sbb.line.directory.model.csv;

import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.kafka.model.Status;
import ch.sbb.line.directory.entity.TimetableFieldLineRelation;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"ttfnid", "validFrom", "validTo", "status", "swissTimetableFieldNumber",
    "number", "businessOrganisation", "description", "comment", "lineRelations", "creationTime", "editionTime"})
public class TimetableFieldNumberVersionCsvModel implements VersionCsvModel {

  @JsonProperty("ttfnid")
  private String ttfnid;

  @JsonProperty("validFrom")
  private LocalDate validFrom;

  @JsonProperty("validTo")
  private LocalDate validTo;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("swissTimetableFieldNumber")
  private String swissTimetableFieldNumber;

  @JsonProperty("number")
  private String number;

  @JsonProperty("businessOrganisation")
  private String businessOrganisation;

  @JsonProperty("description")
  private String description;

  @JsonProperty("comment")
  private String comment;

  @JsonProperty("lineRelations")
  private String lineRelations;

  @JsonProperty("editionTime")
  private LocalDateTime editionTime;

  @JsonProperty("creationTime")
  private LocalDateTime creationTime;

  public static TimetableFieldNumberVersionCsvModel toCsvModel(
      TimetableFieldNumberVersion timetableFieldNumberVersion) {
    TimetableFieldNumberVersionCsvModel timetableFieldNumberVersionCsvModel = new TimetableFieldNumberVersionCsvModel();
    timetableFieldNumberVersionCsvModel.setTtfnid(timetableFieldNumberVersion.getTtfnid());
    timetableFieldNumberVersionCsvModel.setValidFrom(timetableFieldNumberVersion.getValidFrom());
    timetableFieldNumberVersionCsvModel.setValidTo(timetableFieldNumberVersion.getValidTo());
    timetableFieldNumberVersionCsvModel.setStatus(timetableFieldNumberVersion.getStatus());
    timetableFieldNumberVersionCsvModel.setSwissTimetableFieldNumber(
        timetableFieldNumberVersion.getSwissTimetableFieldNumber());
    timetableFieldNumberVersionCsvModel.setNumber(timetableFieldNumberVersion.getNumber());
    timetableFieldNumberVersionCsvModel.setBusinessOrganisation(
        timetableFieldNumberVersion.getBusinessOrganisation());
    timetableFieldNumberVersionCsvModel.setDescription(
        timetableFieldNumberVersion.getDescription());
    timetableFieldNumberVersionCsvModel.setComment(timetableFieldNumberVersion.getComment());
    timetableFieldNumberVersionCsvModel.setLineRelations(
        timetableFieldNumberVersion.getLineRelations().stream()
            .map(TimetableFieldLineRelation::getSlnid)
            .collect(Collectors.joining(",")));
    timetableFieldNumberVersionCsvModel.setEditionTime(
        timetableFieldNumberVersion.getEditionDate());
    timetableFieldNumberVersionCsvModel.setCreationTime(
        timetableFieldNumberVersion.getCreationDate());
    return timetableFieldNumberVersionCsvModel;
  }

}
