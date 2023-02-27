package ch.sbb.line.directory.model.csv;

import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.SublineVersion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"slnid", "mainlineSlnid", "validFrom", "validTo", "swissSublineNumber",
    "status", "sublineType", "paymentType", "number", "businessOrganisation", "longName",
    "description", "creationTime", "editionTime"})
public class SublineVersionCsvModel implements VersionCsvModel {

  @JsonProperty("slnid")
  private String slnid;

  @JsonProperty("mainlineSlnid")
  private String mainlineSlnid;

  @JsonProperty("validFrom")
  private LocalDate validFrom;

  @JsonProperty("validTo")
  private LocalDate validTo;

  @JsonProperty("swissSublineNumber")
  private String swissSublineNumber;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("sublineType")
  private SublineType sublineType;

  @JsonProperty("paymentType")
  private PaymentType paymentType;

  @JsonProperty("number")
  private String number;

  @JsonProperty("businessOrganisation")
  private String businessOrganisation;

  @JsonProperty("longName")
  private String longName;

  @JsonProperty("description")
  private String description;

  @JsonProperty("editionTime")
  private LocalDateTime editionTime;

  @JsonProperty("creationTime")
  private LocalDateTime creationTime;

  public static SublineVersionCsvModel toCsvModel(SublineVersion sublineVersion) {
    SublineVersionCsvModel sublineVersionCsvModel = new SublineVersionCsvModel();
    sublineVersionCsvModel.setSwissSublineNumber(sublineVersion.getSwissSublineNumber());
    sublineVersionCsvModel.setSwissSublineNumber(sublineVersion.getSwissSublineNumber());
    sublineVersionCsvModel.setSlnid(sublineVersion.getSlnid());
    sublineVersionCsvModel.setStatus(sublineVersion.getStatus());
    sublineVersionCsvModel.setSublineType(sublineVersion.getSublineType());
    sublineVersionCsvModel.setPaymentType(sublineVersion.getPaymentType());
    sublineVersionCsvModel.setNumber(sublineVersion.getNumber());
    sublineVersionCsvModel.setLongName(sublineVersion.getLongName());
    sublineVersionCsvModel.setDescription(sublineVersion.getDescription());
    sublineVersionCsvModel.setValidFrom(sublineVersion.getValidFrom());
    sublineVersionCsvModel.setValidTo(sublineVersion.getValidTo());
    sublineVersionCsvModel.setBusinessOrganisation(sublineVersion.getBusinessOrganisation());
    sublineVersionCsvModel.setEditionTime(sublineVersion.getEditionDate());
    sublineVersionCsvModel.setCreationTime(sublineVersion.getCreationDate());
    return sublineVersionCsvModel;
  }

}
