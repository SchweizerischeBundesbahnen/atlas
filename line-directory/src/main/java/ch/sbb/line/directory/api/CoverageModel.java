package ch.sbb.line.directory.api;

import ch.sbb.line.directory.entity.Coverage;
import ch.sbb.line.directory.enumaration.CoverageType;
import ch.sbb.line.directory.enumaration.ModelType;
import ch.sbb.line.directory.enumaration.ValidationErrorType;
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

  public static CoverageModel toModel(Coverage coverage) {
    return CoverageModel.builder()
                        .slnid(coverage.getSlnid())
                        .modelType(coverage.getModelType())
                        .validFrom(coverage.getValidFrom())
                        .validTo(coverage.getValidTo())
                        .coverageType(coverage.getCoverageType())
                        .validationErrorType(coverage.getValidationErrorType())
                        .build();
  }

}
