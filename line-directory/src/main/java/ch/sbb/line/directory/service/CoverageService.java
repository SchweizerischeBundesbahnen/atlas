package ch.sbb.line.directory.service;

import static ch.sbb.atlas.api.lidi.enumaration.CoverageType.COMPLETE;
import static ch.sbb.atlas.api.lidi.enumaration.CoverageType.INCOMPLETE;
import static ch.sbb.atlas.api.lidi.enumaration.ModelType.LINE;
import static ch.sbb.atlas.api.lidi.enumaration.ModelType.SUBLINE;
import static ch.sbb.atlas.api.lidi.enumaration.ValidationErrorType.LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;
import static ch.sbb.atlas.api.lidi.enumaration.ValidationErrorType.SUBLINE_RANGE_OUTSIDE;

import ch.sbb.line.directory.entity.Coverage;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.atlas.api.lidi.enumaration.ModelType;
import ch.sbb.atlas.api.lidi.enumaration.ValidationErrorType;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.repository.CoverageRepository;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CoverageService {

  private final CoverageRepository coverageRepository;

  public void deleteCoverageLine(String slnid) {
    deleteCoverage(slnid, ModelType.LINE);
  }

  public void deleteCoverageSubline(String slnid) {
    deleteCoverage(slnid, SUBLINE);
  }

  private void deleteCoverage(String slnid, ModelType subline) {
    Coverage coverageLine = coverageRepository.findSublineCoverageBySlnidAndModelType(
        slnid, subline);
    if (coverageLine != null) {
      coverageRepository.deleteById(coverageLine.getId());
    }
  }

  public Coverage getSublineCoverageBySlnidAndLineModelType(String slnid) {
    return getSublineCoverageBySlnidAndModelType(slnid, LINE);
  }

  public Coverage getSublineCoverageBySlnidAndSublineModelType(String slnid) {
    return getSublineCoverageBySlnidAndModelType(slnid, SUBLINE);
  }

  public void coverageComplete(LineVersion lineVersion, List<SublineVersion> sublineVersions) {
    updateLineSublineCoverage(lineVersion, sublineVersions, true);
  }

  public void coverageIncomplete(LineVersion lineVersion, List<SublineVersion> sublineVersions) {
    updateLineSublineCoverage(lineVersion, sublineVersions, false);
  }

  void updateLineSublineCoverage(LineVersion lineVersion,
      List<SublineVersion> sublineVersions, boolean isCompletelyCovered) {
    updateSublineCoverageByLine(isCompletelyCovered, lineVersion);
    for (SublineVersion sublineVersion : sublineVersions) {
      updateSublineCoverageBySubline(isCompletelyCovered, sublineVersion);
    }
  }

  private Coverage getSublineCoverageBySlnidAndModelType(String slnid, ModelType modelType) {
    Coverage coverage = coverageRepository.findSublineCoverageBySlnidAndModelType(
        slnid, modelType);
    if (coverage == null) {
      throw new SlnidNotFoundException(slnid);
    }
    return coverage;
  }

  private void updateSublineCoverageByLine(boolean isCompletelyCovered, LineVersion lineVersion) {
    updateLineSublineCoverage(isCompletelyCovered, lineVersion.getSlnid(), LINE,
        lineVersion.getValidFrom(), lineVersion.getValidTo());
  }

  private void updateSublineCoverageBySubline(boolean isCompletelyCovered,
      SublineVersion sublineVersion) {
    updateLineSublineCoverage(isCompletelyCovered, sublineVersion.getSlnid(), SUBLINE,
        sublineVersion.getValidFrom(), sublineVersion.getValidTo());
  }

  private void updateLineSublineCoverage(boolean isCompletelyCovered, String slnid,
      ModelType modelType, @NotNull LocalDate validFrom, @NotNull LocalDate validTo) {
    Coverage alreadyPersistedCoverage = coverageRepository.findSublineCoverageBySlnidAndModelType(
        slnid, modelType);
    if (alreadyPersistedCoverage != null) {
      updateAlreadyPersistedCoverage(isCompletelyCovered, modelType, alreadyPersistedCoverage,
          validFrom, validTo);
    } else {
      addCoverage(isCompletelyCovered, slnid, modelType, validFrom, validTo);
    }
  }

  private void addCoverage(boolean isCompletelyCovered, String slnid, ModelType modelType,
      @NotNull LocalDate validFrom, @NotNull LocalDate validTo) {
    if (isCompletelyCovered) {
      Coverage coverageComplete = buildCompleteLineRangeSmallerThenSublineRange(
          slnid, modelType, validFrom, validTo);
      coverageRepository.save(coverageComplete);
    } else {
      Coverage coverageIncomplete = buildIncompleteLineRangeSmallerThenSublineRange(
          slnid, modelType, validFrom, validTo);
      ValidationErrorType validationErrorType = getValidationErrorType(modelType);
      coverageIncomplete.setValidationErrorType(validationErrorType);
      coverageRepository.save(coverageIncomplete);
    }
  }

  private void updateAlreadyPersistedCoverage(boolean isCompletelyCovered, ModelType modelType,
      Coverage alreadyPersistedCoverage, @NotNull LocalDate validFrom, @NotNull LocalDate validTo) {
    alreadyPersistedCoverage.setValidFrom(validFrom);
    alreadyPersistedCoverage.setValidTo(validTo);
    if (isCompletelyCovered) {
      alreadyPersistedCoverage.setCoverageType(COMPLETE);
      alreadyPersistedCoverage.setValidationErrorType(null);
    } else {
      alreadyPersistedCoverage.setCoverageType(INCOMPLETE);
      ValidationErrorType validationErrorType = getValidationErrorType(modelType);
      alreadyPersistedCoverage.setValidationErrorType(validationErrorType);
    }
    coverageRepository.save(alreadyPersistedCoverage);
  }

  private ValidationErrorType getValidationErrorType(ModelType modelType) {
    ValidationErrorType validationErrorType;
    if (LINE == modelType) {
      validationErrorType = LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;
    } else {
      validationErrorType = SUBLINE_RANGE_OUTSIDE;
    }
    return validationErrorType;
  }

  private Coverage buildIncompleteLineRangeSmallerThenSublineRange(String slnid,
      ModelType modelType, @NotNull LocalDate validFrom, @NotNull LocalDate validTo) {
    return Coverage.builder()
                   .modelType(modelType)
                   .validFrom(validFrom)
                   .validTo(validTo)
                   .validationErrorType(LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE)
                   .coverageType(INCOMPLETE)
                   .slnid(slnid)
                   .build();
  }

  private Coverage buildCompleteLineRangeSmallerThenSublineRange(String slnid,
      ModelType modelType, @NotNull LocalDate validFrom, @NotNull LocalDate validTo) {
    return Coverage.builder()
                   .modelType(modelType)
                   .validFrom(validFrom)
                   .validTo(validTo)
                   .coverageType(COMPLETE)
                   .slnid(slnid)
                   .build();
  }

}
