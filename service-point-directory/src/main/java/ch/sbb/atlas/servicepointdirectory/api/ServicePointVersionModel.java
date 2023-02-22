package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointWithoutTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "ServicePointVersion")
public class ServicePointVersionModel extends BaseVersionModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @NotNull
  @Valid
  private ServicePointNumber number;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "SwissLocation ID", example = "ch:1:sloid:18771")
  private String sloid;

  @Schema(description = "Designation Long", example = "Biel/Bienne Bözingenfeld/Champs-de-Boujean")
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String designationLong;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_30)
  @Schema(description = "Official Designation", example = "Biel/Bienne Bözingenfeld/Champ")
  private String designationOfficial;

  @Size(max = AtlasFieldLengths.LENGTH_6)
  @Schema(description = "AbbreviationDidok", example = "BIBD")
  private String abbreviation;

  @NotNull
  @Schema(description = "Status")
  private ServicePointStatus statusDidok3;

  @NotNull
  @Schema(accessMode = AccessMode.READ_ONLY)
  private CodeAndDesignation statusDidok3Information;

  private boolean operatingPoint;

  private boolean operatingPointWithTimetable;

  private boolean freightServicePoint;

  @Size(max = AtlasFieldLengths.LENGTH_10)
  @Schema(description = "SortCodeOfDestinationStation - only for FreightServicePoint", example = "1234")
  private String sortCodeOfDestinationStation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Schema(description = "SBOID of the associated BusinessOrganisation", example = "ch:1:sboid:100001")
  private String businessOrganisation;

  @Schema(description = "ServicePoint Categories")
  private List<Category> categories;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private List<CodeAndDesignation> categoriesInformation;

  @Schema(description = "OperatingPointType")
  private OperatingPointType operatingPointType;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private CodeAndDesignation operatingPointTypeInformation;

  @Schema(description = "OperatingPointWithoutTimetableType")
  private OperatingPointWithoutTimetableType operatingPointWithoutTimetableType;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private CodeAndDesignation operatingPointWithoutTimetableTypeInformation;

  @Schema(description = "OperatingPointTechnicalTimetableType")
  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private CodeAndDesignation operatingPointTechnicalTimetableTypeInformation;

  @Schema(description = "OperatingPointTrafficPointType")
  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private CodeAndDesignation operatingPointTrafficPointTypeInformation;

  @Schema(description = "ServicePoint is OperatingPointRouteNetwork", example = "false")
  private boolean operatingPointRouteNetwork;

  @Valid
  private ServicePointNumber operatingPointKilometerMaster;

  @Schema(description = "Means of transport. Mandatory for StopPoints")
  private List<MeanOfTransport> meansOfTransport;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private List<CodeAndDesignation> meansOfTransportInformation;

  @Schema(description = "Type of the StopPoint")
  private StopPointType stopPointType;

  @Schema(accessMode = AccessMode.READ_ONLY)
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
  @Schema(description = "ServicePoint is OperatingPoint")
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

  @AssertTrue(message = "FreightServicePoint in CH needs sortCodeOfDestinationStation")
  public boolean isValidFreightServicePoint() {
    return !(getNumber().getCountry() == Country.SWITZERLAND && freightServicePoint && !getValidFrom().isBefore(LocalDate.now()))
        || StringUtils.isNotBlank(sortCodeOfDestinationStation);
  }

  @AssertTrue(message = "At most one of OperatingPointWithoutTimetableType, OperatingPointTechnicalTimetableType, "
      + "OperatingPointTrafficPointType may be set")
  public boolean isValidType() {
    long mutualTypes = Stream.of(
            getOperatingPointWithoutTimetableType() != null,
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
