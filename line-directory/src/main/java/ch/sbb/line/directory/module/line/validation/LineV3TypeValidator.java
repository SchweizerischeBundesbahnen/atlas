package ch.sbb.line.directory.module.line.validation;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import ch.sbb.line.directory.module.line.exception.LineV3ConflictException;
import ch.sbb.line.directory.module.line.exception.LineV3MandatoryFieldException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberFieldNotAllowedException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberTypeMismatchException;
import ch.sbb.line.directory.module.line.exception.LineV3TypeNotAllowedException;
import ch.sbb.line.directory.module.line.exception.LineV3TypeOrderlyException;
import ch.sbb.line.directory.module.line.exception.ReferencedLineHasParentException;
import ch.sbb.line.directory.module.line.exception.ReferencedLineRevokedException;
import ch.sbb.line.directory.module.line.service.LineVersionV3ReadService;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LineV3TypeValidator {

  private static final Set<LineType> ALLOWED_LINE_TYPES = Set.of(LineType.ORDERLY, LineType.DISPOSITION, LineType.TEMPORARY,
      LineType.OPERATIONAL);
  private static final Map<LineType, Set<LineType>> ALLOWED_MEMBER_TYPES = new EnumMap<>(LineType.class);

  static {
    ALLOWED_MEMBER_TYPES.put(LineType.ORDERLY, Set.of(LineType.CONCESSION, LineType.TECHNICAL));
    ALLOWED_MEMBER_TYPES.put(LineType.DISPOSITION, Set.of(LineType.DISPOSITION));
    ALLOWED_MEMBER_TYPES.put(LineType.TEMPORARY, Set.of(LineType.TEMPORARY));
    ALLOWED_MEMBER_TYPES.put(LineType.OPERATIONAL, Set.of(LineType.OPERATIONAL));
  }

  private final LineVersionV3ReadService lineVersionV3ReadService;

  public void validateLineType(LineVersionV3 line) {
    if (!ALLOWED_LINE_TYPES.contains(line.getLineType())) {
      throw new LineV3TypeNotAllowedException(line.getLineType());
    }
    validateMandatoryLineFields(line);

    boolean isOrderly = line.getLineType() == LineType.ORDERLY;
    boolean isBothSet = line.getConcessionType() != null && line.getSwissLineNumber() != null;
    boolean isNoneSet = line.getConcessionType() == null && line.getSwissLineNumber() == null;

    if (isOrderly && !isBothSet) {
      throw new LineV3TypeOrderlyException(line.getLineType());
    }
    if (!isOrderly && !isNoneSet) {
      throw new LineV3TypeOrderlyException(line.getLineType());
    }
  }

  public void validateMemberType(LineVersionV3 member) {
    validateReferencedLine(member);
    validateMemberFieldsNotSet(member);
  }

  public void validateSwissLineNumberUniqueness(LineVersionV3 version) {
    if (version.getReferencedLineSlnid() != null || version.getLineType() != LineType.ORDERLY) {
      return;
    }
    List<LineVersionV3> overlaps = lineVersionV3ReadService.findSwissLineNumberOverlaps(version);
    if (!overlaps.isEmpty()) {
      throw new LineV3ConflictException(version, overlaps);
    }
  }

  private void validateReferencedLine(LineVersionV3 member) {
    List<LineVersionV3> referencedLineVersions = lineVersionV3ReadService.findAllBySlnidOrderByValidFrom(
        member.getReferencedLineSlnid());
    if (referencedLineVersions.isEmpty()) {
      throw new SlnidNotFoundException(member.getReferencedLineSlnid());
    }
    LineVersionV3 referencedLine = OverviewDisplayBuilder.getPrioritizedVersion(referencedLineVersions);
    if (referencedLine.getReferencedLineSlnid() != null) {
      throw new ReferencedLineHasParentException(member.getReferencedLineSlnid());
    }
    if (referencedLine.getStatus() == Status.REVOKED) {
      throw new ReferencedLineRevokedException(member.getReferencedLineSlnid());
    }
    Set<LineType> allowedMemberTypes = ALLOWED_MEMBER_TYPES.getOrDefault(referencedLine.getLineType(), Set.of());
    if (!allowedMemberTypes.contains(member.getLineType())) {
      throw new LineV3MemberTypeMismatchException(member.getLineType(), referencedLine.getLineType());
    }
  }

  private static void validateMandatoryLineFields(LineVersionV3 line) {
    Set<String> missingFields = new TreeSet<>();
    if (StringUtils.isBlank(line.getNumber())) {
      missingFields.add(Fields.number);
    }
    if (line.getOfferCategory() == null) {
      missingFields.add(Fields.offerCategory);
    }
    if (!missingFields.isEmpty()) {
      throw new LineV3MandatoryFieldException(missingFields);
    }
  }

  private static void validateMemberFieldsNotSet(LineVersionV3 member) {
    Set<String> forbiddenFields = new TreeSet<>();
    if (member.getNumber() != null) {
      forbiddenFields.add(Fields.number);
    }
    if (member.getSwissLineNumber() != null) {
      forbiddenFields.add(Fields.swissLineNumber);
    }
    if (member.getOfferCategory() != null) {
      forbiddenFields.add(Fields.offerCategory);
    }
    if (member.getConcessionType() != null) {
      forbiddenFields.add(Fields.concessionType);
    }
    if (!forbiddenFields.isEmpty()) {
      throw new LineV3MemberFieldNotAllowedException(forbiddenFields);
    }
  }

}
