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
import ch.sbb.line.directory.repository.SublineCoverageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CoverageService {

  private final SublineCoverageRepository sublineCoverageRepository;

  public SublineCoverage getSublineCoverageBySlnidAndLineModelType(String slnid) {
    return getSublineCoverageBySlnidAndModelType(slnid, LINE);
  }

  public SublineCoverage getSublineCoverageBySlnidAndSublineModelType(String slnid) {
    return getSublineCoverageBySlnidAndModelType(slnid, SUBLINE);
  }

  private SublineCoverage getSublineCoverageBySlnidAndModelType(String slnid, ModelType modelType) {
    SublineCoverage sublineCoverage = sublineCoverageRepository.findSublineCoverageBySlnidAndModelType(
        slnid, modelType);
    if (sublineCoverage == null) {
      throw new SlnidNotFoundException(slnid);
    }
    return sublineCoverage;
  }

  public void updateSublineCoverageByLine(boolean isCompletelyCovered, LineVersion lineVersion) {
    updateSublineCoverage(isCompletelyCovered, lineVersion.getSlnid(), LINE);
  }

  public void updateSublineCoverageBySubline(boolean isCompletelyCovered,
      SublineVersion sublineVersion) {
    updateSublineCoverage(isCompletelyCovered, sublineVersion.getSlnid(), SUBLINE);
  }

  private void updateSublineCoverage(boolean isCompletelyCovered, String slnid,
      ModelType modelType) {
    SublineCoverage sublineCoverageBySlnid = sublineCoverageRepository.findSublineCoverageBySlnidAndModelType(
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
      sublineCoverageRepository.save(sublineCoverageBySlnid);
    }else{
      if(isCompletelyCovered){
        SublineCoverage sublineCoverageComplete = buildCompleteLineRangeSmallerThenSublineRange(
            slnid,
            modelType);
        sublineCoverageRepository.save(sublineCoverageComplete);
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
        sublineCoverageRepository.save(sublineCoverageIncomplete);
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
