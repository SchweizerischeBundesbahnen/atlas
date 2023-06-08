package ch.sbb.business.organisation.directory.model.csv;

import ch.sbb.atlas.api.bodi.SboidToSaidConverter;
import ch.sbb.atlas.export.model.VersionCsvModel;
import ch.sbb.atlas.kafka.model.Status;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
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
@JsonPropertyOrder({"sboid", "said", "validFrom", "validTo", "organisationNumber", "status",
    "descriptionDe", "descriptionFr", "descriptionIt", "descriptionEn", "abbreviationDe",
    "abbreviationFr", "abbreviationIt", "abbreviationEn", "businessTypesId", "businessTypesDe",
    "businessTypesIt", "businessTypesFr", "creationTime", "editionTime"})
public class BusinessOrganisationVersionCsvModel implements VersionCsvModel {

  @JsonProperty("sboid")
  private String sboid;

  @JsonProperty("said")
  private String said;

  @JsonProperty("validFrom")
  private LocalDate validFrom;

  @JsonProperty("validTo")
  private LocalDate validTo;

  @JsonProperty("organisationNumber")
  private Integer organisationNumber;

  @JsonProperty("status")
  private Status status;

  @JsonProperty("descriptionDe")
  private String descriptionDe;

  @JsonProperty("descriptionFr")
  private String descriptionFr;

  @JsonProperty("descriptionIt")
  private String descriptionIt;

  @JsonProperty("descriptionEn")
  private String descriptionEn;

  @JsonProperty("abbreviationDe")
  private String abbreviationDe;

  @JsonProperty("abbreviationFr")
  private String abbreviationFr;

  @JsonProperty("abbreviationIt")
  private String abbreviationIt;

  @JsonProperty("abbreviationEn")
  private String abbreviationEn;

  @JsonProperty("businessTypesId")
  private String businessTypesId;

  @JsonProperty("businessTypesDe")
  private String businessTypesDe;

  @JsonProperty("businessTypesFr")
  private String businessTypesFr;

  @JsonProperty("businessTypesIt")
  private String businessTypesIt;

  @JsonProperty("editionTime")
  private LocalDateTime editionTime;

  @JsonProperty("creationTime")
  private LocalDateTime creationTime;

  public static BusinessOrganisationVersionCsvModel toCsvModel(
      BusinessOrganisationVersion version) {

    BusinessOrganisationVersionCsvModel model = new BusinessOrganisationVersionCsvModel();
    model.setSboid(version.getSboid());
    model.setSaid(SboidToSaidConverter.toSaid(version.getSboid()));
    model.setValidFrom(version.getValidFrom());
    model.setValidTo(version.getValidTo());
    model.setOrganisationNumber(version.getOrganisationNumber());
    model.setStatus(version.getStatus());
    model.setDescriptionDe(version.getDescriptionDe());
    model.setDescriptionFr(version.getDescriptionFr());
    model.setDescriptionIt(version.getDescriptionIt());
    model.setDescriptionEn(version.getDescriptionEn());
    model.setAbbreviationDe(version.getAbbreviationDe());
    model.setAbbreviationFr(version.getAbbreviationFr());
    model.setAbbreviationIt(version.getAbbreviationIt());
    model.setAbbreviationEn(version.getAbbreviationEn());
    model.setBusinessTypesId(
        version.getBusinessTypes()
            .stream()
            .map(businessType -> String.valueOf(businessType.getId()))
            .collect(Collectors.joining(",")));
    model.setBusinessTypesDe(
        version.getBusinessTypes()
            .stream()
            .map(BusinessType::getTypeDe)
            .collect(Collectors.joining(",")));
    model.setBusinessTypesFr(
        version.getBusinessTypes()
            .stream()
            .map(BusinessType::getTypeFr)
            .collect(Collectors.joining(",")));
    model.setBusinessTypesIt(
        version.getBusinessTypes()
            .stream()
            .map(BusinessType::getTypeIt)
            .collect(Collectors.joining(",")));
    model.setEditionTime(version.getEditionDate());
    model.setCreationTime(version.getCreationDate());
    return model;
  }

}
