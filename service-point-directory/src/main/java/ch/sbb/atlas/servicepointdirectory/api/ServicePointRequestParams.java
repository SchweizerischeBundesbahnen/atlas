package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.base.service.model.api.AtlasApiConstants;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class ServicePointRequestParams {

  private List<String> sloid;
  private List<Integer> number;
  private List<Integer> numberShort;
  private List<String> abbreviation;
  private List<String> businessOrganisationSboids;
  private List<Country> countries;
  private List<Category> categories;
  private List<OperatingPointType> operatingPointTypes;
  private List<TrafficPointElementType> trafficPointElementTypes;
  private List<MeanOfTransport> meansOfTransport;
  private boolean operatingPoint;
  private boolean withoutTimetable;
  private boolean withTimetable;

  @Schema(description = "ValidOn. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate validOn;
  @Schema(description = "ValidAfter>=ValidFrom. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate validAfter;
  @Schema(description = "ValidBefore>=ValidTo. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate validBefore;

  @Schema(description = "CreatedAfter>=creationDate. DateTime format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  private LocalDateTime createdAfter;
  @Schema(description = "ModifiedAfter>=editionDate. DateTime format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  private LocalDateTime modifiedAfter;
}
