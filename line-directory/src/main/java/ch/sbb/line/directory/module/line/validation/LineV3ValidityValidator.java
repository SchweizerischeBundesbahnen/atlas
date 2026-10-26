package ch.sbb.line.directory.module.line.validation;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.exception.LineV3MemberValidityException;
import ch.sbb.line.directory.module.line.exception.LineV3MembersOutsideValidityException;
import ch.sbb.line.directory.module.line.exception.OrderlyLineValidityException;
import ch.sbb.line.directory.module.line.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LineV3ValidityValidator {

  private static final int TEMPORARY_LINE_MAX_VALIDITY_IN_DAYS = 14;
  private static final int ORDERLY_LINE_MIN_VALIDITY_IN_DAYS = 15;

  private final LineVersionV3Repository lineVersionV3Repository;

  public void validateLineTypeDurationLimits(List<LineVersionV3> lineVersions) {
    LineType lineType = lineVersions.getFirst().getLineType();
    DateRange totalRange = totalValidity(lineVersions);
    long validityInDays = totalRange.getValidityInDays();

    if (lineType == LineType.TEMPORARY && validityInDays > TEMPORARY_LINE_MAX_VALIDITY_IN_DAYS) {
      throw new TemporaryLineValidationException(totalRange.getFrom(), totalRange.getTo());
    }
    if (lineType == LineType.ORDERLY && validityInDays < ORDERLY_LINE_MIN_VALIDITY_IN_DAYS) {
      throw new OrderlyLineValidityException(totalRange.getFrom(), totalRange.getTo());
    }
  }

  public void validateMemberWithinReferencedLineValidity(List<LineVersionV3> memberVersions) {
    String referencedLineSlnid = memberVersions.getFirst().getReferencedLineSlnid();
    List<LineVersionV3> referencedLineVersions = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(referencedLineSlnid);
    if (referencedLineVersions.isEmpty()) {
      throw new SlnidNotFoundException(referencedLineSlnid);
    }
    DateRange referencedLineRange = totalValidity(referencedLineVersions);
    boolean isOutside = memberVersions.stream()
        .anyMatch(member -> !DateRange.fromVersionable(member).isDateRangeContainedIn(referencedLineRange));
    if (isOutside) {
      throw new LineV3MemberValidityException(referencedLineRange);
    }
  }

  public void validateMembersWithinLineValidity(List<LineVersionV3> lineVersions) {
    String lineSlnid = lineVersions.getFirst().getSlnid();
    DateRange lineRange = totalValidity(lineVersions);
    List<String> memberSlnidsOutside = lineVersionV3Repository.findAllByReferencedLineSlnid(lineSlnid).stream()
        .filter(member -> !DateRange.fromVersionable(member).isDateRangeContainedIn(lineRange))
        .map(LineVersionV3::getSlnid)
        .distinct()
        .toList();
    if (!memberSlnidsOutside.isEmpty()) {
      throw new LineV3MembersOutsideValidityException(lineSlnid, lineRange, memberSlnidsOutside);
    }
  }

  private static DateRange totalValidity(List<LineVersionV3> versions) {
    LocalDate minValidFrom = versions.stream().map(LineVersionV3::getValidFrom).min(Comparator.naturalOrder()).orElseThrow();
    LocalDate maxValidTo = versions.stream().map(LineVersionV3::getValidTo).max(Comparator.naturalOrder()).orElseThrow();
    return new DateRange(minValidFrom, maxValidTo);
  }

}
