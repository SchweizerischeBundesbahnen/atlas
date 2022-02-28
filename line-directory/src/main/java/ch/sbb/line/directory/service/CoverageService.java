package ch.sbb.line.directory.service;

import static ch.sbb.line.directory.enumaration.ModelType.LINE;
import static ch.sbb.line.directory.enumaration.ModelType.SUBLINE;
import static ch.sbb.line.directory.enumaration.SublineCoverageType.COMPLETE;
import static ch.sbb.line.directory.enumaration.SublineCoverageType.INCOMPLETE;
import static ch.sbb.line.directory.enumaration.ValidationErrorType.LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;
import static ch.sbb.line.directory.enumaration.ValidationErrorType.SUBLINE_RANGE_OUTSIDE;

import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineCoverage;
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

  public SublineCoverage getSublineCoverageBySlnidAndLineModelType(String slnid) {
    return getSublineCoverageBySlnidAndModelType(slnid, LINE);
  }

  public SublineCoverage getSublineCoverageBySlnidAndSublineModelType(String slnid) {
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

  private SublineCoverage getSublineCoverageBySlnidAndModelType(String slnid, ModelType modelType) {
    SublineCoverage sublineCoverage = coverageRepository.findSublineCoverageBySlnidAndModelType(
        slnid, modelType);
    if (sublineCoverage == null) {
      throw new SlnidNotFoundException(slnid);
    }
    return sublineCoverage;
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
    SublineCoverage sublineCoverageBySlnid = coverageRepository.findSublineCoverageBySlnidAndModelType(
        slnid, modelType);
    if (sublineCoverageBySlnid != null) {
      if (isCompletelyCovered) {
        sublineCoverageBySlnid.setSublineCoverageType(COMPLETE);
        sublineCoverageBySlnid.setValidationErrorType(null);
      } else {
        sublineCoverageBySlnid.setSublineCoverageType(INCOMPLETE);
        if (LINE == modelType) {
          sublineCoverageBySlnid.setValidationErrorType(LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE);
        } else {
          sublineCoverageBySlnid.setValidationErrorType(SUBLINE_RANGE_OUTSIDE);
        }
      }
      coverageRepository.save(sublineCoverageBySlnid);
    } else {
      if (isCompletelyCovered) {
        SublineCoverage sublineCoverageComplete = buildCompleteLineRangeSmallerThenSublineRange(
            slnid,
            modelType);
        coverageRepository.save(sublineCoverageComplete);
      } else {
        ValidationErrorType validationErrorType;
        if (LINE == modelType) {
          validationErrorType = LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;
        } else {
          validationErrorType = SUBLINE_RANGE_OUTSIDE;
        }
        SublineCoverage sublineCoverageIncomplete = buildIncompleteLineRangeSmallerThenSublineRange(
            slnid, modelType);
        sublineCoverageIncomplete.setValidationErrorType(validationErrorType);
        coverageRepository.save(sublineCoverageIncomplete);
      }
    }
  }

  private SublineCoverage buildIncompleteLineRangeSmallerThenSublineRange(String slnid,
      ModelType modelType) {
    return SublineCoverage.builder()
                          .modelType(modelType)
                          .validationErrorType(LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE)
                          .sublineCoverageType(INCOMPLETE)
                          .slnid(slnid)
                          .build();
  }

  private SublineCoverage buildCompleteLineRangeSmallerThenSublineRange(String slnid,
      ModelType modelType) {
    return SublineCoverage.builder()
                          .modelType(modelType)
                          .sublineCoverageType(COMPLETE)
                          .slnid(slnid)
                          .build();
  }

}
