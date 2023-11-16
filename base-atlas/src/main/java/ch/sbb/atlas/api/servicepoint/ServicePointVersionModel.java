package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.atlas.validation.DatesValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
public abstract class ServicePointVersionModel extends BaseVersionModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @Schema(description = "Long designation of a location. Used primarily in customer information. "
          + "Not all systems can process names of this length.", example = "Biel/Bienne Bözingenfeld/Champs-de-Boujean")
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_50)
  private String designationLong;

  @NotNull
  @Size(min = 2, max = AtlasFieldLengths.LENGTH_30)
  @Schema(description = "Official designation of a location that must be used by all recipients"
          , example = "Biel/Bienne Bözingenfeld/Champ", maxLength = 30)
  private String designationOfficial;

  @Size(min = AtlasFieldLengths.LENGTH_2, max = AtlasFieldLengths.LENGTH_6)
  @Pattern(regexp = AtlasCharacterSetsRegex.ABBREVIATION_PATTERN)
  @Schema(description = "Location abbreviation. Mainly used by the railways. Abbreviations may not be used as a code for "
          + "identifying locations.", example = "BIBD", minLength = 2, maxLength = 6)
  private String abbreviation;

  @Schema(description = "Indicates if this a Service Point for freights.")
  private boolean freightServicePoint;

  @Size(max = 5)
  @Schema(description = "SortCodeOfDestinationStation - only for FreightServicePoint", example = "1234")
  private String sortCodeOfDestinationStation;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @Schema(description = "SBOID of the associated BusinessOrganisation", example = "ch:1:sboid:100001")
  private String businessOrganisation;

  @Schema(description = "ServicePoint Categories: Assignment of service points to defined business cases.")
  private List<Category> categories;

  @Schema(description = "OperatingPointType, Specifies the detailed intended use of a operating point.")
  private OperatingPointType operatingPointType;

  @Schema(description = "OperatingPointTechnicalTimetableType, all service points relevant for timetable planning and "
      + "publication. At most one of OperatingPointTechnicalTimetableType, OperatingPointTrafficPointType may be set")
  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  @Schema(description = "OperatingPointTrafficPointType, Specifies the detailed intended use of a traffic point." +
          "At most one of OperatingPointTechnicalTimetableType, OperatingPointTrafficPointType may be set")
  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  @Schema(description = "ServicePoint is OperatingPointRouteNetwork", example = "false")
  private boolean operatingPointRouteNetwork;

  @Schema(description = "Means of transport. Indicates for which means of transport a stop is intended/equipped. Mandatory for "
          + "StopPoints")
  private List<MeanOfTransport> meansOfTransport;

  @Schema(description = "Type of the StopPoint, Indicates for which type of traffic (e.g. regular traffic) a stop was recorded.")
  private StopPointType stopPointType;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;

  @JsonIgnore
  public boolean isRawServicePoint() {
    return getOperatingPointType() == null &&
        getOperatingPointTechnicalTimetableType() == null &&
        getMeansOfTransport().isEmpty() &&
        !isFreightServicePoint() &&
        getOperatingPointTrafficPointType() == null;
  }

  @JsonIgnore
  @AssertTrue(message = """
      ServicePoint rejected due to invalid type information.
      A ServicePoint might either have:
       - OperatingPointType
       - OperatingPointTechnicalTimetableType
       - OperatingPointTrafficPointType
       - MeansOfTransport or FreightServicePoint
      """)
  public boolean isValidType() {
    long mutualTypes = Stream.of(
            // Betriebspunkt
            getOperatingPointType() != null,
            // Reiner Betriebspunkt
            getOperatingPointTechnicalTimetableType() != null,
            // Haltestelle und/oder Bedienpunkt
            (!getMeansOfTransport().isEmpty() || isFreightServicePoint()),
            // Tarifhaltestelle
            getOperatingPointTrafficPointType() != null)
        .filter(i -> i)
        .count();
    // Dienststelle (eg. Verkaufsstelle) hat keines dieser Informationen
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