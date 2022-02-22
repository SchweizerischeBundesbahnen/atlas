package ch.sbb.line.directory.service;

import static ch.sbb.line.directory.enumaration.ModelType.LINE;
import static ch.sbb.line.directory.enumaration.SublineCoverageType.COMPLETE;
import static ch.sbb.line.directory.enumaration.SublineCoverageType.INCOMPLETE;
import static ch.sbb.line.directory.enumaration.ValidationErrorType.LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE;

import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineCoverage;
import ch.sbb.line.directory.repository.SublineCoverageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SublineCoverageService {

  private final SublineCoverageRepository sublineCoverageRepository;

  public void updateSublineCoverage(boolean validationIssueResult, LineVersion lineVersion) {
    SublineCoverage sublineCoverageBySlnid = sublineCoverageRepository.findSublineCoverageBySlnid(
        lineVersion.getSlnid());

    if (validationIssueResult) {
      SublineCoverage sublineCoverageIncomplete = buildIncompleteLineRangeSmallerThenSublineRange(
          lineVersion);
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
            lineVersion);
        sublineCoverageRepository.save(sublineCoverageComplete);
      }
    }
  }

  private SublineCoverage buildIncompleteLineRangeSmallerThenSublineRange(LineVersion lineVersion) {
    return SublineCoverage.builder()
                          .modelType(LINE)
                          .validationErrorType(LINE_RANGE_SMALLER_THEN_SUBLINE_RANGE)
                          .sublineCoverageType(INCOMPLETE)
                          .slnid(lineVersion.getSlnid())
                          .build();
  }

  private SublineCoverage buildCompleteLineRangeSmallerThenSublineRange(LineVersion lineVersion) {
    return SublineCoverage.builder()
                          .modelType(LINE)
                          .sublineCoverageType(COMPLETE)
                          .slnid(lineVersion.getSlnid())
                          .build();
  }
}
