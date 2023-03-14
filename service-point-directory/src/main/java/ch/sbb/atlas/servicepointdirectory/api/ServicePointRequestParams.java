package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ServicePointRequestParams {

  @Schema(description = "Unique key for service points which is used in the customer information.	")
  @Singular(ignoreNullCollections = true)
  private List<String> sloids = new ArrayList<>();

  @Schema(description = "DiDok-Number formerly known as UIC-Code, combination of uicCountryCode and numberShort")
  @Singular(ignoreNullCollections = true)
  private List<Integer> numbers = new ArrayList<>();

  @Schema(description ="List of UIC Country codes", example ="85", defaultValue = "85")
  @Singular(ignoreNullCollections = true)
  private List<Integer> uicCountryCodes = new ArrayList<>();

  @Schema(description ="List of Iso Country codes", example ="CH")
  @Singular(ignoreNullCollections = true)
  private List<String> isoCountryCodes = new ArrayList<>();

  @Schema(description = "Number of a service point which is provided by DiDok for Switzerland. It is part of the unique key for"
      + " service points.")
  @Singular(value = "numberShort", ignoreNullCollections = true)
  private List<Integer> numbersShort = new ArrayList<>();

  @Schema(description = "abbreviation of the service point")
  @Singular(ignoreNullCollections = true)
  private List<String> abbreviations = new ArrayList<>();

  @Schema(description = "Swiss Bussines Organisation ID of the business organisation")
  @Singular(ignoreNullCollections = true)
  private List<String> businessOrganisationSboids = new ArrayList<>();

  @Schema(description = "Country allocated the service point number and is to be interpreted organisationally, not "
      + "territorially.")
  @Singular(ignoreNullCollections = true)
  private List<Country> countries = new ArrayList<>();

  @Schema(description = "All service points relevant for timetable planning")
  @Singular(ignoreNullCollections = true)
  private List<OperatingPointTechnicalTimetableType> operatingPointTechnicalTimetableTypes = new ArrayList<>();

  @Schema(description = "Assignment of service points to defined business cases")
  @Singular(ignoreNullCollections = true)
  private List<Category> categories = new ArrayList<>();

  @Schema(description = "detailed intended use of a operating point")
  @Singular(ignoreNullCollections = true)
  private List<OperatingPointType> operatingPointTypes = new ArrayList<>();

  @Schema(description = "indicates for which type of traffic (e.g. regular traffic) a stop was recorded.")
  @Singular(ignoreNullCollections = true)
  private List<StopPointType> stopPointTypes = new ArrayList<>();

  @Schema(description = "filter on the meanOfTransport")
  @Singular(value = "meanOfTransport", ignoreNullCollections = true)
  private List<MeanOfTransport> meansOfTransport = new ArrayList<>();

  @Schema(description = "filter on the Satus of a servicePoint")
  @Singular(ignoreNullCollections = true)
  private List<Status> statusRestrictions = new ArrayList<>();

  @Schema(description = "filter on operation Points only")
  private Boolean operatingPoint;
  @Schema(description = "filter on operation Points with Timetables only")
  private Boolean withTimetable;

  @Schema(description = "ValidOn. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate validOn;

  @Schema(description = "[fromDate] >= validFrom. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate fromDate;

  @Schema(description = "[toDate] <= validTo. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate toDate;

  @Schema(description = "CreatedAfter>=creationDate. DateTime format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  private LocalDateTime createdAfter;
  @Schema(description = "ModifiedAfter>=editionDate. DateTime format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  private LocalDateTime modifiedAfter;

  public List<ServicePointNumber> getServicePointNumbers() {
    return numbers.stream().map(ServicePointNumber::ofNumberWithoutCheckDigit).toList();
  }
}
