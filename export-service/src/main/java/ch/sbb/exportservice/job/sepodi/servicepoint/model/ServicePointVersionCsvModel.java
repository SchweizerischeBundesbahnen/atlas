package ch.sbb.exportservice.job.sepodi.servicepoint.model;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
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

  private StopPointType stopPointType;

  private boolean freightServicePoint;

  private boolean trafficPoint;

  private boolean borderPoint;

  private boolean hasGeolocation;

  private String isoCountryCode;

  private String cantonName;

  private Integer cantonFsoNumber;

  private String cantonAbbreviation;

  private String districtName;

  private Integer districtFsoNumber;

  private String municipalityName;

  private Integer fsoNumber;

  private String localityName;

  private OperatingPointType operatingPointType;

  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  private String meansOfTransport;

  private String categories;

  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  private boolean operatingPointRouteNetwork;

  private boolean operatingPointKilometer;

  private Integer operatingPointKilometerMasterNumber;

  private String sortCodeOfDestinationStation;

  private String businessOrganisation;

  private Integer businessOrganisationNumber;

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

  private Double height;

  private String creationDate;

  private String editionDate;

  private Status status;

}
