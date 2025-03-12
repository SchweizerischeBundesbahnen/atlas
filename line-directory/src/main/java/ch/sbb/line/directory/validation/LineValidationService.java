package ch.sbb.line.directory.validation;

import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.exception.LineConflictException;
import ch.sbb.line.directory.exception.LineTypeOrderlyException;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class LineValidationService {

  private static final int TEMPORARY_LINE_MAX_VALIDITY_IN_DAYS = 14;

  private final LineVersionRepository lineVersionRepository;
  private final CoverageValidationService coverageValidationService;
  private final SharedBusinessOrganisationService sharedBusinessOrganisationService;

  public void validateLinePreconditionBusinessRule(LineVersion lineVersion) {
    validateLineConflict(lineVersion);
    sharedBusinessOrganisationService.validateSboidExists(lineVersion.getBusinessOrganisation());
  }

  public void validateLineAfterVersioningBusinessRule(LineVersion lineVersion) {
    List<LineVersion> savedLine = lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    validateTemporaryLinesDuration(savedLine);

    coverageValidationService.validateLineSublineCoverage(lineVersion);
  }

  public void validateNotRevoked(LineVersion lineVersion) {
    if (lineVersion.getStatus() == Status.REVOKED) {
      throw new RevokedException(lineVersion.getSlnid());
    }
  }

  void validateLineConflict(LineVersion lineVersion) {
    if (lineVersion.getLineType() == LineType.ORDERLY) {
      List<LineVersion> swissLineNumberOverlaps = lineVersionRepository.findSwissLineNumberOverlaps(
          lineVersion);
      if (!swissLineNumberOverlaps.isEmpty()) {
        throw new LineConflictException(lineVersion, swissLineNumberOverlaps);
      }
    }
  }

  void validateTemporaryLinesDuration(List<LineVersion> savedLine) {
    if (LineType.TEMPORARY.equals(savedLine.getFirst().getLineType())) {

      LocalDate minValidFrom = savedLine.getFirst().getValidFrom();
      LocalDate maxValidTo = savedLine.getLast().getValidTo();

      long validityInDays = ChronoUnit.DAYS.between(minValidFrom, maxValidTo);
      if (validityInDays > TEMPORARY_LINE_MAX_VALIDITY_IN_DAYS) {
        throw new TemporaryLineValidationException(minValidFrom, maxValidTo);
      }
    }
  }

  public void dynamicBeanValidation(LineVersion lineVersion) {
    if (lineVersion.getLineType() != LineType.ORDERLY && (lineVersion.getConcessionType() != null
        || lineVersion.getSwissLineNumber() != null)) {
      throw new LineTypeOrderlyException(lineVersion.getLineType());
    }
    if (lineVersion.getLineType() == LineType.ORDERLY && (lineVersion.getConcessionType() == null
        || lineVersion.getSwissLineNumber() == null)) {
      throw new LineTypeOrderlyException(lineVersion.getLineType());
    }
  }

}
