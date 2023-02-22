package ch.sbb.atlas.api.lidi;

import ch.sbb.atlas.api.lidi.enumaration.CoverageType;
import ch.sbb.atlas.api.lidi.enumaration.ModelType;
import ch.sbb.atlas.api.lidi.enumaration.ValidationErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "Coverage")
public class CoverageModel {

  @Schema(description = "SLNID", accessMode = AccessMode.READ_ONLY, example = "ch:1:slnid:10001234")
  private String slnid;

  @Schema(description = "ModelType")
  private ModelType modelType;

  @Schema(description = "Valid from")
  private LocalDate validFrom;

  @Schema(description = "Valid to")
  private LocalDate validTo;

  @Schema(description = "CoverageType")
  private CoverageType coverageType;

  @Schema(description = "ValidationErrorType")
  private ValidationErrorType validationErrorType;

}
