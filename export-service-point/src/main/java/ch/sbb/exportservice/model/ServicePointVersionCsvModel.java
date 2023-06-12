package ch.sbb.exportservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePointVersionCsvModel {

  @JsonProperty("numberShort")
  private Integer numberShort;

  @JsonProperty("uicCountryCode")
  private Integer uicCountryCode;

  @JsonProperty("sloid")
  private String sloid;

  @JsonProperty("number")
  private Integer number;

  @JsonProperty("checkDigit")
  private Integer checkDigit;

  @JsonProperty("validFrom")
  private String validFrom;

  @JsonProperty("validTo")
  private String validTo;

  @JsonProperty("designationOfficial")
  private String designationOfficial;

  @JsonProperty("designationLong")
  private String designationLong;

  @JsonProperty("abbreviation")
  private String abbreviation;

  @JsonProperty("operatingPoint")
  private boolean operatingPoint;

  @JsonProperty("operatingPointWithTimetable")
  private boolean operatingPointWithTimetable;

  @JsonProperty("stopPoint")
  private boolean stopPoint;

  @JsonProperty("stopPointTypeCode")
  private String stopPointTypeCode;

  @JsonProperty("freightServicePoint")
  private boolean freightServicePoint;

  @JsonProperty("trafficPoint")
  private boolean trafficPoint;

  @JsonProperty("borderPoint")
  private boolean borderPoint;

  @JsonProperty("hasGeolocation")
  private boolean hasGeolocation;

  @JsonProperty("isoCoutryCode")
  private String isoCoutryCode;

  @JsonProperty("cantonAbbreviation")
  private String cantonAbbreviation;

  @JsonProperty("districtName")
  private String districtName;

  @JsonProperty("districtFsoName")
  private Integer districtFsoName;

  @JsonProperty("municipalityName")
  private String municipalityName;

  @JsonProperty("fsoNumber")
  private Integer fsoNumber;

  @JsonProperty("localityName")
  private String localityName;

  @JsonProperty("operatingPointTypeCode")
  private String operatingPointTypeCode;

  @JsonProperty("operatingPointTechnicalTimetableTypeCode")
  private String operatingPointTechnicalTimetableTypeCode;

  @JsonProperty("meansOfTransportCode")
  private String meansOfTransportCode;

  @JsonProperty("categoriesCode")
  private String categoriesCode;

  @JsonProperty("operatingPointTrafficPointTypeCode")
  private String operatingPointTrafficPointTypeCode;

  @JsonProperty("operatingPointRouteNetwork")
  private boolean operatingPointRouteNetwork;

  @JsonProperty("operatingPointKilometer")
  private boolean operatingPointKilometer;

  @JsonProperty("operatingPointKilometerMasterNumber")
  private Integer operatingPointKilometerMasterNumber;

  @JsonProperty("sortCodeOfDestinationStation")
  private String sortCodeOfDestinationStation;

  @JsonProperty("sboid")
  private String sboid;

  @JsonProperty("businessOrganisationOrganisationNumber")
  private Integer businessOrganisationOrganisationNumber;

  @JsonProperty("businessOrganisationAbbreviationDe")
  private String businessOrganisationAbbreviationDe;

  @JsonProperty("businessOrganisationAbbreviationFr")
  private String businessOrganisationAbbreviationFr;

  @JsonProperty("businessOrganisationAbbreviationIt")
  private String businessOrganisationAbbreviationIt;

  @JsonProperty("businessOrganisationAbbreviationEn")
  private String businessOrganisationAbbreviationEn;

  @JsonProperty("businessOrganisationDescriptionDe")
  private String businessOrganisationDescriptionDe;

  @JsonProperty("businessOrganisationDescriptionFr")
  private String businessOrganisationDescriptionFr;

  @JsonProperty("businessOrganisationDescriptionIt")
  private String businessOrganisationDescriptionIt;

  @JsonProperty("businessOrganisationDescriptionEn")
  private String businessOrganisationDescriptionEn;

  @JsonProperty("fotComment")
  private String fotComment;

  @JsonProperty("lv95East")
  private Double lv95East;

  @JsonProperty("lv95North")
  private Double lv95North;

  @JsonProperty("wgs84East")
  private Double wgs84East;

  @JsonProperty("wgs84North")
  private Double wgs84North;

  @JsonProperty("wgs84WebEast")
  private Double wgs84WebEast;

  @JsonProperty("wgs84WebNorth")
  private Double wgs84WebNorth;

}
