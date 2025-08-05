package ch.sbb.atlas.api.model;

import ch.sbb.atlas.api.AtlasApiConstants;
import io.swagger.v3.oas.annotations.Parameter;
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
public class VersionedObjectDateRequestParams extends VersionedObjectValidityRequestParams {

  @Parameter(description = "creationDate >= [createdAfter]. DateTime format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN + ", "
      + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN_WITH_T + ", " + AtlasApiConstants.ISO_DATE_TIME_PARSE_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN, fallbackPatterns = {
      AtlasApiConstants.DATE_TIME_FORMAT_PATTERN_WITH_T, AtlasApiConstants.ISO_DATE_TIME_PARSE_PATTERN})
  private LocalDateTime createdAfter;

  @Parameter(description = "editionDate >= [modifiedAfter]. DateTime format: " + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN + ", "
      + AtlasApiConstants.DATE_TIME_FORMAT_PATTERN_WITH_T + ", " + AtlasApiConstants.ISO_DATE_TIME_PARSE_PATTERN)
  @DateTimeFormat(pattern = AtlasApiConstants.DATE_TIME_FORMAT_PATTERN, fallbackPatterns = {
      AtlasApiConstants.DATE_TIME_FORMAT_PATTERN_WITH_T, AtlasApiConstants.ISO_DATE_TIME_PARSE_PATTERN})
  private LocalDateTime modifiedAfter;

}
