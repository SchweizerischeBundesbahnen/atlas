package ch.sbb.line.directory.service;

import static ch.sbb.line.directory.enumaration.ModelType.LINE;
import static ch.sbb.line.directory.enumaration.ModelType.SUBLINE;
import static ch.sbb.line.directory.enumaration.SublineCoverageType.COMPLETE;
import static ch.sbb.line.directory.enumaration.SublineCoverageType.INCOMPLETE;
import static ch.sbb.line.directory.enumaration.ValidationErrorType.LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;

import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineCoverage;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.ModelType;
import ch.sbb.line.directory.exception.NotFoundException.SlnidNotFoundException;
import ch.sbb.line.directory.repository.SublineCoverageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SublineCoverageService {

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

  public void updateSublineCoverageByLine(boolean validationIssueResult, LineVersion lineVersion) {
    updateSublineCoverage(validationIssueResult, lineVersion.getSlnid(), LINE);
  }

  public void updateSublineCoverageBySubline(boolean validationIssueResult,
      SublineVersion sublineVersion) {
    updateSublineCoverage(validationIssueResult, sublineVersion.getSlnid(), SUBLINE);
  }

  private void updateSublineCoverage(boolean validationIssueResult, String slnid,
      ModelType modelType) {
    SublineCoverage sublineCoverageBySlnid = sublineCoverageRepository.findSublineCoverageBySlnidAndModelType(
        slnid, modelType);

    if (validationIssueResult) {
      SublineCoverage sublineCoverageIncomplete = buildIncompleteLineRangeSmallerThenSublineRange(
          slnid, modelType);
      if (sublineCoverageBySlnid != null) {
        sublineCoverageIncomplete.setId(sublineCoverageBySlnid.getId());
      }
      sublineCoverageRepository.save(sublineCoverageIncomplete);
    } else {
      if (sublineCoverageBySlnid != null) {
        sublineCoverageBySlnid.setSublineCoverageType(COMPLETE);
        sublineCoverageBySlnid.setValidationErrorType(null);
        sublineCoverageRepository.save(sublineCoverageBySlnid);
      } else {
        SublineCoverage sublineCoverageComplete = buildCompleteLineRangeSmallerThenSublineRange(
            slnid,
            modelType);
        sublineCoverageRepository.save(sublineCoverageComplete);
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
