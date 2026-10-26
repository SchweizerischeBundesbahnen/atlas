package ch.sbb.line.directory.module.line.validation;

import ch.sbb.atlas.api.lidi.enumaration.v3.LineType;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.line.directory.exception.RevokedException;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.entity.LineVersionV3.Fields;
import ch.sbb.line.directory.module.line.exception.LineV3ConflictException;
import ch.sbb.line.directory.module.line.exception.LineV3FieldNotUpdatableException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberConcessionException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberTypeMismatchException;
import ch.sbb.line.directory.module.line.exception.LineV3MemberValidityException;
import ch.sbb.line.directory.module.line.exception.LineV3MembersOutsideValidityException;
import ch.sbb.line.directory.module.line.exception.LineV3TypeNotAllowedException;
import ch.sbb.line.directory.module.line.exception.LineV3TypeOrderlyException;
import ch.sbb.line.directory.module.line.exception.OrderlyLineValidityException;
import ch.sbb.line.directory.module.line.exception.ReferencedLineHasParentException;
import ch.sbb.line.directory.module.line.exception.ReferencedLineRevokedException;
import ch.sbb.line.directory.module.line.exception.TemporaryLineValidationException;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Business rules of {@link LineVersionV3}. A version is a LINE when referencedLineSlnid is null, otherwise a member of the
 * referenced line (nesting depth max. 1; the reference is immutable, so a line with members can never become a member).
 */
@Service
@RequiredArgsConstructor
public class LineVersionV3ValidationService {

  private static final int TEMPORARY_LINE_MAX_VALIDITY_IN_DAYS = 14;
  private static final int ORDERLY_LINE_MIN_VALIDITY_IN_DAYS = 15;

  private static final Set<LineType> ALLOWED_LINE_TYPES = Set.of(LineType.ORDERLY, LineType.DISPOSITION, LineType.TEMPORARY,
      LineType.OPERATIONAL);
  private static final Map<LineType, Set<LineType>> ALLOWED_MEMBER_TYPES = new EnumMap<>(LineType.class);

  static {
    ALLOWED_MEMBER_TYPES.put(LineType.ORDERLY, Set.of(LineType.CONCESSION, LineType.TECHNICAL));
    ALLOWED_MEMBER_TYPES.put(LineType.DISPOSITION, Set.of(LineType.DISPOSITION));
    ALLOWED_MEMBER_TYPES.put(LineType.TEMPORARY, Set.of(LineType.TEMPORARY));
    ALLOWED_MEMBER_TYPES.put(LineType.OPERATIONAL, Set.of(LineType.OPERATIONAL));
  }

  private final LineVersionV3Repository lineVersionV3Repository;
  private final SharedBusinessOrganisationService sharedBusinessOrganisationService;

  public void validatePreconditionBusinessRules(LineVersionV3 version) {
    if (isMember(version)) {
      validateReferencedLine(version);
      validateMemberConcessionRules(version);
    } else {
      validateLineType(version);
    }
    validateSwissLineNumberConflict(version);
    sharedBusinessOrganisationService.validateSboidExists(version.getBusinessOrganisation());
  }

  public void validateValidityConstraints(List<LineVersionV3> versionsOfSlnid) {
    if (isMember(versionsOfSlnid.getFirst())) {
      validateMemberValidityWithinReferencedLine(versionsOfSlnid);
    } else {
      validateTotalValidityDuration(versionsOfSlnid);
    }
  }

  /**
   * D-2 from the parent side: after a LINE was versioned, every version of its members must still lie within the line's
   * overall validity (min validFrom .. max validTo).
   *
   * @param parentVersions all saved versions of one LINE (referencedLineSlnid is null)
   */
  public void validateMembersWithinParentValidity(List<LineVersionV3> parentVersions) {
    String parentSlnid = parentVersions.getFirst().getSlnid();
    DateRange parentRange = totalValidity(parentVersions);
    List<String> memberSlnidsOutside = lineVersionV3Repository.findAllByReferencedLineSlnid(parentSlnid).stream()
        .filter(member -> !DateRange.fromVersionable(member).isDateRangeContainedIn(parentRange))
        .map(LineVersionV3::getSlnid)
        .distinct()
        .toList();
    if (!memberSlnidsOutside.isEmpty()) {
      throw new LineV3MembersOutsideValidityException(parentSlnid, parentRange, memberSlnidsOutside);
    }
  }

  public void validateNotRevoked(LineVersionV3 current) {
    if (current.getStatus() == Status.REVOKED) {
      throw new RevokedException(current.getSlnid());
    }
  }

  public void validateUpdate(LineVersionV3 current, LineVersionV3 edited) {
    if (isMember(current) || current.getLineType() == LineType.ORDERLY) {
      return;
    }
    if (edited.getSwissLineNumber() != null) {
      throw new LineV3FieldNotUpdatableException(edited.getSwissLineNumber(), Fields.swissLineNumber, current.getLineType());
    }
    if (edited.getConcessionType() != null) {
      throw new LineV3FieldNotUpdatableException(edited.getConcessionType().name(), Fields.concessionType,
          current.getLineType());
    }
  }

  private static boolean isMember(LineVersionV3 version) {
    return version.getReferencedLineSlnid() != null;
  }

  private void validateLineType(LineVersionV3 line) {
    if (!ALLOWED_LINE_TYPES.contains(line.getLineType())) {
      throw new LineV3TypeNotAllowedException(line.getLineType());
    }
    boolean orderly = line.getLineType() == LineType.ORDERLY;
    boolean bothSet = line.getConcessionType() != null && line.getSwissLineNumber() != null;
    boolean noneSet = line.getConcessionType() == null && line.getSwissLineNumber() == null;
    if (orderly ? !bothSet : !noneSet) {
      throw new LineV3TypeOrderlyException(line.getLineType());
    }
  }

  private void validateReferencedLine(LineVersionV3 member) {
    List<LineVersionV3> referencedLineVersions = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(
        member.getReferencedLineSlnid());
    if (referencedLineVersions.isEmpty()) {
      throw new SlnidNotFoundException(member.getReferencedLineSlnid());
    }
    LineVersionV3 referencedLine = OverviewDisplayBuilder.getPrioritizedVersion(referencedLineVersions);
    if (isMember(referencedLine)) {
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

  private static void validateMemberConcessionRules(LineVersionV3 member) {
    boolean concessionMember = member.getLineType() == LineType.CONCESSION;

    boolean concessionTypeMissing = concessionMember && member.getConcessionType() == null;
    boolean concessionTypeNotAllowed = !concessionMember && member.getConcessionType() != null;
    if (concessionTypeMissing || concessionTypeNotAllowed) {
      throw LineV3MemberConcessionException.concessionType();
    }

    boolean swissLineNumberMissing = concessionMember && member.getSwissLineNumber() == null;
    boolean swissLineNumberNotAllowed = !concessionMember && member.getSwissLineNumber() != null;
    if (swissLineNumberMissing || swissLineNumberNotAllowed) {
      throw LineV3MemberConcessionException.swissLineNumber();
    }
  }

  private void validateSwissLineNumberConflict(LineVersionV3 version) {
    if (version.getLineType() == LineType.ORDERLY || version.getLineType() == LineType.CONCESSION) {
      List<LineVersionV3> overlaps = lineVersionV3Repository.findSwissLineNumberOverlaps(version);
      if (!overlaps.isEmpty()) {
        throw new LineV3ConflictException(version, overlaps);
      }
    }
  }

  private void validateMemberValidityWithinReferencedLine(List<LineVersionV3> memberVersions) {
    String referencedLineSlnid = memberVersions.getFirst().getReferencedLineSlnid();
    List<LineVersionV3> referencedLineVersions = lineVersionV3Repository.findAllBySlnidOrderByValidFrom(referencedLineSlnid);
    if (referencedLineVersions.isEmpty()) {
      throw new SlnidNotFoundException(referencedLineSlnid);
    }
    DateRange referencedLineRange = totalValidity(referencedLineVersions);
    boolean outside = memberVersions.stream()
        .anyMatch(member -> !DateRange.fromVersionable(member).isDateRangeContainedIn(referencedLineRange));
    if (outside) {
      throw new LineV3MemberValidityException(referencedLineRange);
    }
  }

  private static void validateTotalValidityDuration(List<LineVersionV3> versionsOfSlnid) {
    LineType lineType = versionsOfSlnid.getFirst().getLineType();
    DateRange totalRange = totalValidity(versionsOfSlnid);
    long validityInDays = totalRange.getValidityInDays();

    if (lineType == LineType.TEMPORARY && validityInDays > TEMPORARY_LINE_MAX_VALIDITY_IN_DAYS) {
      throw new TemporaryLineValidationException(totalRange.getFrom(), totalRange.getTo());
    }
    if (lineType == LineType.ORDERLY && validityInDays < ORDERLY_LINE_MIN_VALIDITY_IN_DAYS) {
      throw new OrderlyLineValidityException(totalRange.getFrom(), totalRange.getTo());
    }
  }

  private static DateRange totalValidity(List<LineVersionV3> versions) {
    LocalDate minValidFrom = versions.stream().map(LineVersionV3::getValidFrom).min(Comparator.naturalOrder()).orElseThrow();
    LocalDate maxValidTo = versions.stream().map(LineVersionV3::getValidTo).max(Comparator.naturalOrder()).orElseThrow();
    return new DateRange(minValidFrom, maxValidTo);
  }

}
