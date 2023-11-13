package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.model.Versionable;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "LoadingPointVersion")
public abstract class LoadingPointVersionModel extends BaseVersionModel implements DatesValidator, Versionable {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @NotNull
  @Schema(description = "Loading Point Number", example = "4201")
  @Min(ServicePointConstants.LOADING_POINT_NUMBER_MIN)
  @Max(ServicePointConstants.LOADING_POINT_NUMBER_MAX)
  private Integer number;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_12)
  @Schema(description = "Designation", example = "Piazzale")
  private String designation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_35)
  @Schema(description = "Designation Long", example = "Piazzale")
  private String designationLong;

  @Schema(description = "Is a connectionPoint")
  private boolean connectionPoint;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  @Schema(description = "Optimistic locking version - instead of ETag HTTP Header (see RFC7232:Section 2.3)", example = "5")
  private Integer etagVersion;
}
