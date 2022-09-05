package ch.sbb.line.directory.entity;

import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.enumaration.PaymentType;
import ch.sbb.line.directory.enumaration.SublineType;
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
    "status", "sublineType", "paymentType", "numer", "businessOrganisation", "longName",
    "description", "editionTime", "creationTime"})
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

  @JsonProperty("numer")
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
    SublineVersionCsvModel lineVersionCsvModel = new SublineVersionCsvModel();
    lineVersionCsvModel.setSwissSublineNumber(sublineVersion.getSwissSublineNumber());
    lineVersionCsvModel.setSwissSublineNumber(sublineVersion.getSwissSublineNumber());
    lineVersionCsvModel.setSlnid(sublineVersion.getSlnid());
    lineVersionCsvModel.setStatus(sublineVersion.getStatus());
    lineVersionCsvModel.setSublineType(sublineVersion.getSublineType());
    lineVersionCsvModel.setPaymentType(sublineVersion.getPaymentType());
    lineVersionCsvModel.setNumber(sublineVersion.getNumber());
    lineVersionCsvModel.setLongName(sublineVersion.getLongName());
    lineVersionCsvModel.setDescription(sublineVersion.getDescription());
    lineVersionCsvModel.setValidFrom(sublineVersion.getValidFrom());
    lineVersionCsvModel.setValidTo(sublineVersion.getValidTo());
    lineVersionCsvModel.setBusinessOrganisation(sublineVersion.getBusinessOrganisation());
    lineVersionCsvModel.setEditionTime(sublineVersion.getEditionDate());
    lineVersionCsvModel.setCreationTime(sublineVersion.getCreationDate());
    return lineVersionCsvModel;
  }

}
