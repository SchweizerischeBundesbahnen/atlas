package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.validation.DatesValidator;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
public abstract class ServicePointVersionModel extends BaseVersionModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
      + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String sloid;

  @Schema(description = "Long designation of a location. Used primarily in customer information. "
      + "Not all systems can process names of this length.", example = "Biel/Bienne Bözingenfeld/Champs-de-Boujean")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String designationLong;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_30)
  @Schema(description = "Official designation of a location that must be used by all recipients"
      , example = "Biel/Bienne Bözingenfeld/Champ", maxLength = 30)
  private String designationOfficial;

  @Size(max = AtlasFieldLengths.LENGTH_6)
  @Schema(description = "Location abbreviation. Mainly used by the railways. Abbreviations may not be used as a code for "
      + "identifying locations.", example = "BIBD", maxLength = 6)
  private String abbreviation;

  @NotNull
  @Schema(description = "Status, Code of status of the service point, useful for specific business tasks.")
  private ServicePointStatus statusDidok3;

  @NotNull
  @Schema(accessMode = AccessMode.READ_ONLY)
  private CodeAndDesignation statusDidok3Information;

  @Schema(description = "Indicates if this a operatingPoint.")
  private boolean operatingPoint;

  @Schema(description = "Indicates if this a operatingPoint including Timetables.")
  private boolean operatingPointWithTimetable;

  @Schema(description = "Indicates if this a Service Point for freights.")
  private boolean freightServicePoint;

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @Schema(description = "SortCodeOfDestinationStation - only for FreightServicePoint", example = "1234")
  private String sortCodeOfDestinationStation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Schema(description = "SBOID of the associated BusinessOrganisation", example = "ch:1:sboid:100001")
  private String businessOrganisation;

  @Schema(description = "ServicePoint Categories: Assignment of service points to defined business cases.")
  private List<Category> categories;

  @Schema(accessMode = AccessMode.READ_ONLY, description = "Details to the categories.")
  private List<CodeAndDesignation> categoriesInformation;

  @Schema(description = "OperatingPointType, Specifies the detailed intended use of a operating point.")
  private OperatingPointType operatingPointType;

  @Schema(accessMode = AccessMode.READ_ONLY, description = "Details to the operationPointType.")
  private CodeAndDesignation operatingPointTypeInformation;

  @Schema(description = "OperatingPointTechnicalTimetableType, all service points relevant for timetable planning and "
      + "publication. ")
  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  @Schema(accessMode = AccessMode.READ_ONLY, description = "Details to the OperatingPointTechnicalTimetableType.")
  private CodeAndDesignation operatingPointTechnicalTimetableTypeInformation;

  @Schema(description = "OperatingPointTrafficPointType, Specifies the detailed intended use of a traffic point.")
  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private CodeAndDesignation operatingPointTrafficPointTypeInformation;

  @Schema(description = "ServicePoint is OperatingPointRouteNetwork", example = "false")
  private boolean operatingPointRouteNetwork;

  @Min(value = 1000000, message = "Minimum value for number.")
  @Max(value = 9999999, message = "Maximum value for number.")
  @Schema(description = "Reference to a operatingPointRouteNetwork. OperatingPointKilometer are always related to a "
      + "operatingPointRouteNetwork")
  private Integer operatingPointKilometerMasterNumber;

  @Valid
  @Schema(description = "Reference to a operatingPointRouteNetwork. OperatingPointKilometer are always related to a "
      + "operatingPointRouteNetwork")
  private ServicePointNumber operatingPointKilometerMaster;

  @Schema(description = "Means of transport. Indicates for which means of transport a stop is intended/equipped. Mandatory for "
      + "StopPoints")
  private List<MeanOfTransport> meansOfTransport;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private List<CodeAndDesignation> meansOfTransportInformation;

  @Schema(description = "Type of the StopPoint, Indicates for which type of traffic (e.g. regular traffic) a stop was recorded. ")
  private StopPointType stopPointType;

  @Schema(accessMode = AccessMode.READ_ONLY, description = "Details to the StopPointType.")
  private CodeAndDesignation stopPointTypeInformation;

  @Size(max = AtlasFieldLengths.LENGTH_1500)
  @Schema(description = "FotComment", example = "Good Service Point.")
  private String fotComment;

  private ServicePointGeolocationModel servicePointGeolocation;

  @Schema(description = "Status", accessMode = AccessMode.READ_ONLY)
  private Status status;

  @NotNull
  private LocalDate validFrom;
  @NotNull
  private LocalDate validTo;

  @JsonInclude
  @Schema(description = "ServicePoint has a Geolocation")
  public boolean isHasGeolocation() {
    return servicePointGeolocation != null;
  }

  @JsonInclude
  @Schema(description = "ServicePoint is OperatingPoint, Operating points refers to the totality of operationally used service "
      + "points. These are not necessarily traffic-relevant service points. ")
  public boolean isOperatingPoint() {
    return operatingPointType != null || isTrafficPoint();
  }

  @JsonInclude
  @Schema(description = "ServicePoint is OperatingPoint with timetable")
  public boolean isOperatingPointWithTimetable() {
    return operatingPointType == null || operatingPointType.hasTimetable();
  }

  @JsonInclude
  @Schema(description = "ServicePoint is StopPoint")
  public boolean isStopPoint() {
    return !getMeansOfTransport().isEmpty();
  }

  @JsonInclude
  @Schema(description = "ServicePoint is FreightServicePoint")
  public boolean isFreightServicePoint() {
    return StringUtils.isNotBlank(sortCodeOfDestinationStation);
  }

  @JsonInclude
  @Schema(description = "ServicePoint is FareStop", example = "false")
  public boolean isFareStop() {
    return operatingPointTrafficPointType == OperatingPointTrafficPointType.TARIFF_POINT;
  }

  @JsonInclude
  @Schema(description = "ServicePoint is TrafficPoint")
  public boolean isTrafficPoint() {
    return isStopPoint() || isFreightServicePoint() || isFareStop();
  }

  @JsonInclude
  @Schema(description = "ServicePoint is BorderPoint", example = "false")
  public boolean isBorderPoint() {
    return operatingPointTechnicalTimetableType == OperatingPointTechnicalTimetableType.COUNTRY_BORDER;
  }

  @JsonInclude
  @Schema(description = "ServicePoint is OperatingPointKilometer")
  public boolean isOperatingPointKilometer() {
    return operatingPointKilometerMaster != null;
  }

  @AssertTrue(message = "StopPointType only allowed for StopPoint")
  boolean isValidStopPointWithType() {
    return isStopPoint() || stopPointType == null;
  }

  @AssertTrue(message = "At most one of OperatingPointWithoutTimetableType, OperatingPointTechnicalTimetableType, "
      + "OperatingPointTrafficPointType may be set")
  public boolean isValidType() {
    long mutualTypes = Stream.of(
            getOperatingPointTechnicalTimetableType() != null,
            getOperatingPointTrafficPointType() != null)
        .filter(i -> i)
        .count();
    return mutualTypes <= 1;
  }

  public List<MeanOfTransport> getMeansOfTransport() {
    if (meansOfTransport == null) {
      return new ArrayList<>();
    }
    return meansOfTransport;
  }

  public List<Category> getCategories() {
    if (categories == null) {
      return new ArrayList<>();
    }
    return categories;
  }

}
