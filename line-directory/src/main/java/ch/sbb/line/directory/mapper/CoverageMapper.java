package ch.sbb.line.directory.mapper;

import ch.sbb.atlas.api.line.CoverageModel;
import ch.sbb.line.directory.entity.Coverage;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CoverageMapper {

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
