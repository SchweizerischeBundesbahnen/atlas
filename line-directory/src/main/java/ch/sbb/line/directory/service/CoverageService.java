package ch.sbb.line.directory.service;

import static ch.sbb.line.directory.enumaration.ModelType.LINE;
import static ch.sbb.line.directory.enumaration.ModelType.SUBLINE;
import static ch.sbb.line.directory.enumaration.CoverageType.COMPLETE;
import static ch.sbb.line.directory.enumaration.CoverageType.INCOMPLETE;
import static ch.sbb.line.directory.enumaration.ValidationErrorType.LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;
import static ch.sbb.line.directory.enumaration.ValidationErrorType.SUBLINE_RANGE_OUTSIDE;

import ch.sbb.line.directory.entity.Coverage;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.ModelType;
import ch.sbb.line.directory.enumaration.ValidationErrorType;
import ch.sbb.line.directory.exception.NotFoundException.SlnidNotFoundException;
import ch.sbb.line.directory.repository.CoverageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CoverageService {

  private final CoverageRepository coverageRepository;

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

  private void updateLineSublineCoverage(LineVersion lineVersion,
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
    updateSublineCoverage(isCompletelyCovered, lineVersion.getSlnid(), LINE);
  }

  private void updateSublineCoverageBySubline(boolean isCompletelyCovered,
      SublineVersion sublineVersion) {
    updateSublineCoverage(isCompletelyCovered, sublineVersion.getSlnid(), SUBLINE);
  }

  //TODO: refactor
  private void updateSublineCoverage(boolean isCompletelyCovered, String slnid,
      ModelType modelType) {
    Coverage coverageBySlnid = coverageRepository.findSublineCoverageBySlnidAndModelType(
        slnid, modelType);
    if (coverageBySlnid != null) {
      if (isCompletelyCovered) {
        coverageBySlnid.setCoverageType(COMPLETE);
        coverageBySlnid.setValidationErrorType(null);
      } else {
        coverageBySlnid.setCoverageType(INCOMPLETE);
        if (LINE == modelType) {
          coverageBySlnid.setValidationErrorType(LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE);
        } else {
          coverageBySlnid.setValidationErrorType(SUBLINE_RANGE_OUTSIDE);
        }
      }
      coverageRepository.save(coverageBySlnid);
    } else {
      if (isCompletelyCovered) {
        Coverage coverageComplete = buildCompleteLineRangeSmallerThenSublineRange(
            slnid,
            modelType);
        coverageRepository.save(coverageComplete);
      } else {
        ValidationErrorType validationErrorType;
        if (LINE == modelType) {
          validationErrorType = LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;
        } else {
          validationErrorType = SUBLINE_RANGE_OUTSIDE;
        }
        Coverage coverageIncomplete = buildIncompleteLineRangeSmallerThenSublineRange(
            slnid, modelType);
        coverageIncomplete.setValidationErrorType(validationErrorType);
        coverageRepository.save(coverageIncomplete);
      }
    }
  }

  private Coverage buildIncompleteLineRangeSmallerThenSublineRange(String slnid,
      ModelType modelType) {
    return Coverage.builder()
                   .modelType(modelType)
                   .validationErrorType(LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE)
                   .coverageType(INCOMPLETE)
                   .slnid(slnid)
                   .build();
  }

  private Coverage buildCompleteLineRangeSmallerThenSublineRange(String slnid,
      ModelType modelType) {
    return Coverage.builder()
                   .modelType(modelType)
                   .coverageType(COMPLETE)
                   .slnid(slnid)
                   .build();
  }

}
