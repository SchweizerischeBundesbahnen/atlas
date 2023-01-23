package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.api.BaseVersionModel;
import ch.sbb.atlas.base.service.model.validation.DatesValidator;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

  @JsonInclude
  @Schema(description = "ServicePoint has a Geolocation")
  public boolean isHasGeolocation() {
    return servicePointGeolocation != null;
  }

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

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
    return operatingPointType == OperatingPointType.TARIFF_POINT;
  }

  @JsonInclude
  @Schema(description = "ServicePoint is TrafficPoint")
  public boolean isTrafficPoint() {
    return isStopPoint() || isFreightServicePoint() || isFareStop();
  }

  @JsonInclude
  @Schema(description = "ServicePoint is BorderPoint", example = "false")
  public boolean isBorderPoint() {
    return operatingPointType == OperatingPointType.COUNTRY_BORDER;
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

  @AssertTrue(message = "OperatingPointType has to match operatingPoint Attributes")
  public boolean isValidOperatingPointType() {
    return operatingPointType == null || (isOperatingPoint()
        && isOperatingPointWithTimetable() == operatingPointType.hasTimetable());
  }

  @AssertTrue(message = "FreightServicePoint in CH needs sortCodeOfDestinationStation")
  public boolean isValidFreightServicePoint() {
    return !(getNumber().getCountry()==Country.SWITZERLAND && freightServicePoint && !getValidFrom().isBefore(LocalDate.now())) || StringUtils.isNotBlank(sortCodeOfDestinationStation);
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

  public static ServicePointVersionModel fromEntity(ServicePointVersion servicePointVersion) {
    return ServicePointVersionModel.builder()
        .id(servicePointVersion.getId())
        .number(servicePointVersion.getNumber())
        .sloid(servicePointVersion.getSloid())
        .designationLong(servicePointVersion.getDesignationLong())
        .designationOfficial(servicePointVersion.getDesignationOfficial())
        .abbreviation(servicePointVersion.getAbbreviation())
        .statusDidok3(servicePointVersion.getStatusDidok3())
        .statusDidok3Information(CodeAndDesignation.fromEnum(servicePointVersion.getStatusDidok3()))
        .operatingPoint(servicePointVersion.isOperatingPoint())
        .operatingPointWithTimetable(servicePointVersion.isOperatingPointWithTimetable())
        .freightServicePoint(servicePointVersion.isFreightServicePoint())
        .sortCodeOfDestinationStation(servicePointVersion.getSortCodeOfDestinationStation())
        .businessOrganisation(servicePointVersion.getBusinessOrganisation())
        .categories(new ArrayList<>(servicePointVersion.getCategories()))
        .categoriesInformation(servicePointVersion.getCategories().stream().map(CodeAndDesignation::fromEnum).toList())
        .operatingPointType(servicePointVersion.getOperatingPointType())
        .operatingPointTypeInformation(CodeAndDesignation.fromEnum(servicePointVersion.getOperatingPointType()))
        .operatingPointRouteNetwork(servicePointVersion.isOperatingPointRouteNetwork())
        .operatingPointKilometerMaster(servicePointVersion.getOperatingPointKilometerMaster())
        .meansOfTransport(new ArrayList<>(servicePointVersion.getMeansOfTransport()))
        .meansOfTransportInformation(
            servicePointVersion.getMeansOfTransport().stream().map(CodeAndDesignation::fromEnum).toList())
        .stopPointType(servicePointVersion.getStopPointType())
        .stopPointTypeInformation(CodeAndDesignation.fromEnum(servicePointVersion.getStopPointType()))
        .fotComment(servicePointVersion.getComment())
        .servicePointGeolocation(ServicePointGeolocationModel.fromEntity(servicePointVersion.getServicePointGeolocation()))
        .validFrom(servicePointVersion.getValidFrom())
        .validTo(servicePointVersion.getValidTo())
        .creationDate(servicePointVersion.getCreationDate())
        .creator(servicePointVersion.getCreator())
        .editionDate(servicePointVersion.getEditionDate())
        .editor(servicePointVersion.getEditor())
        .build();
  }

}
