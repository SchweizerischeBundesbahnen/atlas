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

  @Singular(ignoreNullCollections = true)
  private List<String> sloids = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<Integer> numbers = new ArrayList<>();
  @Singular(value = "numberShort", ignoreNullCollections = true)
  private List<Integer> numbersShort = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<String> abbreviations = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<String> businessOrganisationSboids = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<Country> countries = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<OperatingPointTechnicalTimetableType> operatingPointTechnicalTimetableTypes = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<Category> categories = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<OperatingPointType> operatingPointTypes = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<StopPointType> stopPointTypes = new ArrayList<>();
  @Singular(value = "meanOfTransport", ignoreNullCollections = true)
  private List<MeanOfTransport> meansOfTransport = new ArrayList<>();
  @Singular(ignoreNullCollections = true)
  private List<Status> statusRestrictions = new ArrayList<>();

  private Boolean operatingPoint;
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
