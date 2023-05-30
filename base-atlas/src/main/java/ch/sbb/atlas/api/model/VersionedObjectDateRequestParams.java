package ch.sbb.atlas.api.model;

import ch.sbb.atlas.api.AtlasApiConstants;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class VersionedObjectDateRequestParams {

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
