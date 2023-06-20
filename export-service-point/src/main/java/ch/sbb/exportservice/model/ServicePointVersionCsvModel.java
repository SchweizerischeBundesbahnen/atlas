package ch.sbb.exportservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
public class ServicePointVersionCsvModel {

  private Integer numberShort;

  private Integer uicCountryCode;

  private String sloid;

  private Integer number;

  private Integer checkDigit;

  private String validFrom;

  private String validTo;

  private String designationOfficial;

  private String designationLong;

  private String abbreviation;

  private boolean operatingPoint;

  private boolean operatingPointWithTimetable;

  private boolean stopPoint;

  private String stopPointTypeCode;

  private boolean freightServicePoint;

  private boolean trafficPoint;

  private boolean borderPoint;

  private boolean hasGeolocation;

  private String isoCoutryCode;

  private String cantonAbbreviation;

  private String districtName;

  private Integer districtFsoName;

  private String municipalityName;

  private Integer fsoNumber;

  private String localityName;

  private String operatingPointTypeCode;

  private String operatingPointTechnicalTimetableTypeCode;

  private String meansOfTransportCode;

  private String categoriesCode;

  private String operatingPointTrafficPointTypeCode;

  private boolean operatingPointRouteNetwork;

  private boolean operatingPointKilometer;

  private Integer operatingPointKilometerMasterNumber;

  private String sortCodeOfDestinationStation;

  private String sboid;

  private Integer businessOrganisationOrganisationNumber;

  private String businessOrganisationAbbreviationDe;

  private String businessOrganisationAbbreviationFr;

  private String businessOrganisationAbbreviationIt;

  private String businessOrganisationAbbreviationEn;

  private String businessOrganisationDescriptionDe;

  private String businessOrganisationDescriptionFr;

  private String businessOrganisationDescriptionIt;

  private String businessOrganisationDescriptionEn;

  private String fotComment;

  private Double lv95East;

  private Double lv95North;

  private Double wgs84East;

  private Double wgs84North;

  private Double wgs84WebEast;

  private Double wgs84WebNorth;

  private Double height;

  private String creationDate;

  private String editionDate;

  private String statusDidok3;

}
