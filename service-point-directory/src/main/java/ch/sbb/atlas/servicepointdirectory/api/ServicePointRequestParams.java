package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.base.service.model.api.AtlasApiConstants;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPointType;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
public class ServicePointRequestParams {

  @Singular(ignoreNullCollections = true)
  private List<String> sloids;
  @Singular(ignoreNullCollections = true)
  private List<Integer> numbers;
  @Singular(value = "numberShort", ignoreNullCollections = true)
  private List<Integer> numbersShort;
  @Singular(ignoreNullCollections = true)
  private List<String> abbreviations;
  @Singular(ignoreNullCollections = true)
  private List<String> businessOrganisationSboids;
  @Singular(ignoreNullCollections = true)
  private List<Country> countries;
  @Singular(ignoreNullCollections = true)
  private List<Category> categories;
  @Singular(ignoreNullCollections = true)
  private List<OperatingPointType> operatingPointTypes;
  @Singular(ignoreNullCollections = true)
  private List<StopPointType> stopPointTypes;
  @Singular(value = "meanOfTransport", ignoreNullCollections = true)
  private List<MeanOfTransport> meansOfTransport;
  @Singular(ignoreNullCollections = true)
  private List<Status> statusRestrictions;

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
