package ch.sbb.atlas.api.bodi;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.model.Status;
import io.swagger.v3.oas.annotations.Parameter;
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
public class BusinessOrganisationVersionRequestParams {

  @Parameter(description = "Search criteria strings will be looked up in specific columns")
  @Singular(value = "searchCriteria", ignoreNullCollections = true)
  private List<String> searchCriteria = new ArrayList<>();

  @Parameter(description = "Sboid based restriction")
  @Singular(ignoreNullCollections = true)
  private List<String> inSboids = new ArrayList<>();

  @Parameter(description = "Status based restriction", example = "85")
  @Singular(ignoreNullCollections = true)
  private List<Status> statusChoices = new ArrayList<>();

  @Parameter(description = "ValidOn. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate validOn;

  @Parameter(description = "[fromDate] >= validFrom. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate fromDate;

  @Parameter(description = "[toDate] <= validTo. Date format: " + AtlasApiConstants.DATE_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_FORMAT_PATTERN)
  private LocalDate toDate;

  @Parameter(description = "creationDate >= [createdAfter]. DateTime format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  private LocalDateTime createdAfter;

  @Parameter(description = "editionDate >= [modifiedAfter]. DateTime format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)
  private LocalDateTime modifiedAfter;

}
