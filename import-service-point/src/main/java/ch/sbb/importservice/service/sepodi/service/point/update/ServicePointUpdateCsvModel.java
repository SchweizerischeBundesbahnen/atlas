package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.BulkImportContainer;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.importservice.service.sepodi.service.point.update.ServicePointUpdateCsvModel.Fields;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonPropertyOrder({Fields.numberShort, Fields.uicCountryCode,
    Fields.sloid, Fields.number, Fields.checkDigit, Fields.validFrom, Fields.validTo, Fields.designationOfficial,
    Fields.designationLong, Fields.abbreviation, Fields.operatingPoint, Fields.operatingPointWithTimetable, Fields.stopPoint,
    Fields.stopPointType, Fields.freightServicePoint, Fields.trafficPoint,
    Fields.borderPoint, Fields.hasGeolocation, Fields.isoCountryCode,
    Fields.cantonName, Fields.cantonFsoNumber, Fields.cantonAbbreviation,
    Fields.districtName, Fields.districtFsoNumber, Fields.municipalityName, Fields.fsoNumber,
    Fields.localityName, Fields.operatingPointType, Fields.operatingPointTechnicalTimetableType,
    Fields.meansOfTransport, Fields.categories, Fields.operatingPointTrafficPointType,
    Fields.operatingPointRouteNetwork, Fields.operatingPointKilometer, Fields.operatingPointKilometerMasterNumber,
    Fields.sortCodeOfDestinationStation, Fields.businessOrganisation, Fields.businessOrganisationNumber,
    Fields.businessOrganisationAbbreviationDe, Fields.businessOrganisationAbbreviationFr,
    Fields.businessOrganisationAbbreviationIt, Fields.businessOrganisationAbbreviationEn,
    Fields.businessOrganisationDescriptionDe, Fields.businessOrganisationDescriptionFr,
    Fields.businessOrganisationDescriptionIt, Fields.businessOrganisationDescriptionEn, Fields.fotComment, Fields.lv95East,
    Fields.lv95North, Fields.wgs84East, Fields.wgs84North,
    Fields.height, Fields.creationDate, Fields.editionDate, Fields.status
})
public class ServicePointUpdateCsvModel implements BulkImportContainer {

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
