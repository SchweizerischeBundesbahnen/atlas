package ch.sbb.line.directory.module.line.service;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * V3 port of {@link ch.sbb.line.directory.module.subline.service.SublineShorteningService}: shortening a line (only validity
 * changed) shortens its members (rows with {@code referencedLineSlnid == line slnid}) as far as allowed.
 */
@RequiredArgsConstructor
@Service
public class LineV3ShorteningService {

  private final LineVersionV3Repository lineVersionV3Repository;

  public AffectedLineV3Members checkAffectedMembers(LineVersionV3 lineVersion, LineVersionV3 editedVersion) {
    List<String> allowedMembers = new ArrayList<>();
    List<String> notAllowedMembers = new ArrayList<>();

    if (isOnlyValidityChanged(lineVersion, editedVersion) && isShortening(lineVersion, editedVersion)) {
      List<LineVersionV3> lineVersions = findAllVersionsBySlnid(lineVersion.getSlnid());
      Map<String, List<LineVersionV3>> memberVersions = getAllMembersByParentSlnid(lineVersion.getSlnid());

      AffectedMembersData data = new AffectedMembersData(lineVersion, editedVersion, VersionRange.of(lineVersions),
          memberVersions, allowedMembers, notAllowedMembers);

      if (!memberVersions.isEmpty()) {
        if (lineVersions.size() > 1) {
          processLineVersion(data);
        } else if (lineVersions.size() == 1) {
          processSingleLineVersion(data);
        }
      }
    }
    return new AffectedLineV3Members(allowedMembers, notAllowedMembers);
  }

  public List<LineV3MemberShortening> checkAndPrepareToShortMembers(LineVersionV3 currentVersion,
      LineVersionV3 editedVersion) {
    if (!isOnlyValidityChanged(currentVersion, editedVersion)) {
      return new ArrayList<>();
    }
    AffectedLineV3Members affectedMembers = checkAffectedMembers(currentVersion, editedVersion);
    if (!affectedMembers.hasAllowedMembers()) {
      return new ArrayList<>();
    }
    return prepareMembersToShort(currentVersion, editedVersion, affectedMembers.allowedMembers());
  }

  public boolean isShortening(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    return isShorteningValidFrom(currentVersion, editedVersion) || isShorteningValidTo(currentVersion, editedVersion);
  }

  public boolean isOnlyValidityChanged(LineVersionV3 current, LineVersionV3 edited) {
    return (isValidFromChanged(current, edited) || isValidToChanged(current, edited))
        && areNonValidityFieldsEqual(current, edited);
  }

  public Map<String, List<LineVersionV3>> getAllMembersByParentSlnid(String parentSlnid) {
    return lineVersionV3Repository.findAllByReferencedLineSlnid(parentSlnid).stream()
        .collect(Collectors.groupingBy(LineVersionV3::getSlnid, LinkedHashMap::new, Collectors.toList()));
  }

  private void processLineVersion(AffectedMembersData data) {
    boolean isOnlyValidToChanged = isOnlyValidToChanged(data.lineVersion(), data.editedVersion());
    boolean isOnlyValidFromChanged = isOnlyValidFromChanged(data.lineVersion(), data.editedVersion());

    if (isOnlyValidFromChanged && isMatchingVersion(data.lineVersion(), data.lineVersionRange().oldestVersion())) {
      processMembers(data, (edited, range, versionCount) -> isShorteningAllowedValidFrom(edited, range));
    } else if (isOnlyValidToChanged && isMatchingVersion(data.lineVersion(), data.lineVersionRange().latestVersion())) {
      processMembers(data, (edited, range, versionCount) -> isShorteningAllowedValidTo(edited, range));
    }
  }

  private void processSingleLineVersion(AffectedMembersData data) {
    processMembers(data, this::isMemberShorteningAllowed);
  }

  private void processMembers(AffectedMembersData data, ShorteningAllowedRule shorteningAllowedRule) {
    LineVersionV3 editedVersion = data.editedVersion();
    for (List<LineVersionV3> memberVersions : data.memberVersions().values()) {
      String memberSlnid = memberVersions.getFirst().getSlnid();
      VersionRange range = getOldestAndLatestVersion(memberSlnid);
      if (isMemberValidityAffectedByUpdatedParent(editedVersion.getValidFrom(), editedVersion.getValidTo(), range)) {
        if (shorteningAllowedRule.isAllowed(editedVersion, range, memberVersions.size())) {
          data.allowedMembers().add(memberSlnid);
        } else {
          data.notAllowedMembers().add(memberSlnid);
        }
      }
    }
  }

  private boolean isMemberShorteningAllowed(LineVersionV3 editedVersion, VersionRange range, int versionCount) {
    if (versionCount == 1) {
      return true;
    }
    return isShorteningAllowedValidFrom(editedVersion, range) && isShorteningAllowedValidTo(editedVersion, range);
  }

  private List<LineV3MemberShortening> prepareMembersToShort(LineVersionV3 lineVersion, LineVersionV3 editedVersion,
      List<String> membersToShort) {
    List<LineV3MemberShortening> membersToUpdate = new ArrayList<>();
    List<LineVersionV3> lineVersions = findAllVersionsBySlnid(lineVersion.getSlnid());
    VersionRange lineVersionRange = VersionRange.of(lineVersions);

    if (lineVersions.size() == 1 && isBothValidityChanged(lineVersion, editedVersion)) {
      membersToShort.forEach(memberSlnid -> processBothValidityChanged(memberSlnid, lineVersion, editedVersion,
          membersToUpdate));
    }

    if (isMatchingVersion(lineVersion, lineVersionRange.oldestVersion()) && isOnlyValidFromChanged(lineVersion,
        editedVersion)) {
      membersToShort.forEach(memberSlnid -> processOnlyValidFromChanged(memberSlnid, editedVersion, membersToUpdate));
    }

    if (isMatchingVersion(lineVersion, lineVersionRange.latestVersion()) && isOnlyValidToChanged(lineVersion,
        editedVersion)) {
      membersToShort.forEach(memberSlnid -> processOnlyValidToChanged(memberSlnid, editedVersion, membersToUpdate));
    }

    return membersToUpdate;
  }

  private void processBothValidityChanged(String memberSlnid, LineVersionV3 lineVersion, LineVersionV3 editedVersion,
      List<LineV3MemberShortening> membersToUpdate) {
    List<LineVersionV3> memberVersions = findAllVersionsBySlnid(memberSlnid);
    VersionRange range = VersionRange.of(memberVersions);

    if (memberVersions.size() > 1) {
      if (isShorteningValidFrom(lineVersion, editedVersion)) {
        LineVersionV3 editedMember = cloneVersion(range.oldestVersion());
        editedMember.setValidFrom(editedVersion.getValidFrom());
        membersToUpdate.add(new LineV3MemberShortening(cloneVersion(range.oldestVersion()), editedMember));
      }
      if (isShorteningValidTo(lineVersion, editedVersion)) {
        LineVersionV3 editedMember = cloneVersion(range.latestVersion());
        editedMember.setValidTo(editedVersion.getValidTo());
        membersToUpdate.add(new LineV3MemberShortening(cloneVersion(range.latestVersion()), editedMember));
      }
    } else {
      LineVersionV3 editedMember = cloneVersion(range.oldestVersion());
      if (isShorteningValidFrom(lineVersion, editedVersion)) {
        editedMember.setValidFrom(editedVersion.getValidFrom());
      }
      if (isShorteningValidTo(lineVersion, editedVersion)) {
        editedMember.setValidTo(editedVersion.getValidTo());
      }
      membersToUpdate.add(new LineV3MemberShortening(cloneVersion(range.oldestVersion()), editedMember));
    }
  }

  private void processOnlyValidFromChanged(String memberSlnid, LineVersionV3 editedVersion,
      List<LineV3MemberShortening> membersToUpdate) {
    LineVersionV3 oldestVersion = getOldestAndLatestVersion(memberSlnid).oldestVersion();
    LineVersionV3 editedMember = cloneVersion(oldestVersion);
    editedMember.setValidFrom(editedVersion.getValidFrom());
    membersToUpdate.add(new LineV3MemberShortening(cloneVersion(oldestVersion), editedMember));
  }

  private void processOnlyValidToChanged(String memberSlnid, LineVersionV3 editedVersion,
      List<LineV3MemberShortening> membersToUpdate) {
    LineVersionV3 latestVersion = getOldestAndLatestVersion(memberSlnid).latestVersion();
    LineVersionV3 editedMember = cloneVersion(latestVersion);
    editedMember.setValidTo(editedVersion.getValidTo());
    membersToUpdate.add(new LineV3MemberShortening(cloneVersion(latestVersion), editedMember));
  }

  private static boolean isMatchingVersion(LineVersionV3 currentVersion, LineVersionV3 versionFromRange) {
    return Objects.equals(currentVersion.getId(), versionFromRange.getId());
  }

  private static boolean isShorteningValidFrom(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    return editedVersion.getValidFrom().isAfter(currentVersion.getValidFrom());
  }

  private static boolean isShorteningValidTo(LineVersionV3 currentVersion, LineVersionV3 editedVersion) {
    return editedVersion.getValidTo().isBefore(currentVersion.getValidTo());
  }

  private static boolean isShorteningAllowedValidTo(LineVersionV3 editedVersion, VersionRange memberRange) {
    return !editedVersion.getValidTo().isBefore(memberRange.latestVersion().getValidFrom());
  }

  private static boolean isShorteningAllowedValidFrom(LineVersionV3 editedVersion, VersionRange memberRange) {
    return !editedVersion.getValidFrom().isAfter(memberRange.oldestVersion().getValidTo());
  }

  private static boolean isMemberValidityAffectedByUpdatedParent(LocalDate validFrom, LocalDate validTo,
      VersionRange memberRange) {
    DateRange memberDateRange = new DateRange(memberRange.oldestVersion().getValidFrom(),
        memberRange.latestVersion().getValidTo());
    return !memberDateRange.isDateRangeContainedIn(new DateRange(validFrom, validTo));
  }

  /**
   * Compares all business fields a client can edit. Immutable fields ({@code slnid}, {@code referencedLineSlnid},
   * {@code lineType}) are ignored, as V2 ignores {@code lineType}.
   */
  private static boolean areNonValidityFieldsEqual(LineVersionV3 current, LineVersionV3 edited) {
    return Objects.equals(edited.getSwissLineNumber(), current.getSwissLineNumber())
        && Objects.equals(edited.getBusinessOrganisation(), current.getBusinessOrganisation())
        && Objects.equals(edited.getComment(), current.getComment())
        && Objects.equals(edited.getConcessionType(), current.getConcessionType())
        && Objects.equals(edited.getOfferCategory(), current.getOfferCategory())
        && Objects.equals(edited.getShortNumber(), current.getShortNumber())
        && Objects.equals(edited.getDescription(), current.getDescription())
        && Objects.equals(edited.getLongName(), current.getLongName())
        && Objects.equals(edited.getNumber(), current.getNumber())
        && Objects.equals(edited.getLinienId(), current.getLinienId())
        && Objects.equals(edited.getFinancing(), current.getFinancing())
        && Objects.equals(edited.getCablewayNumber(), current.getCablewayNumber())
        && Objects.equals(edited.getCablewayType(), current.getCablewayType())
        && Objects.equals(edited.getCablewayName(), current.getCablewayName())
        && Objects.equals(nullToEmpty(edited.getMeansOfTransport()), nullToEmpty(current.getMeansOfTransport()));
  }

  private static <T> Set<T> nullToEmpty(Set<T> set) {
    return set == null ? Set.of() : new HashSet<>(set);
  }

  private static boolean isValidFromChanged(LineVersionV3 current, LineVersionV3 edited) {
    return !Objects.equals(edited.getValidFrom(), current.getValidFrom());
  }

  private static boolean isValidToChanged(LineVersionV3 current, LineVersionV3 edited) {
    return !Objects.equals(edited.getValidTo(), current.getValidTo());
  }

  private static boolean isBothValidityChanged(LineVersionV3 current, LineVersionV3 edited) {
    return isValidFromChanged(current, edited) && isValidToChanged(current, edited)
        && areNonValidityFieldsEqual(current, edited);
  }

  private static boolean isOnlyValidToChanged(LineVersionV3 current, LineVersionV3 edited) {
    return isValidToChanged(current, edited) && !isValidFromChanged(current, edited)
        && areNonValidityFieldsEqual(current, edited);
  }

  private static boolean isOnlyValidFromChanged(LineVersionV3 current, LineVersionV3 edited) {
    return isValidFromChanged(current, edited) && !isValidToChanged(current, edited)
        && areNonValidityFieldsEqual(current, edited);
  }

  private VersionRange getOldestAndLatestVersion(String slnid) {
    return VersionRange.of(findAllVersionsBySlnid(slnid));
  }

  private List<LineVersionV3> findAllVersionsBySlnid(String slnid) {
    return lineVersionV3Repository.findAllBySlnidOrderByValidFrom(slnid);
  }

  private static LineVersionV3 cloneVersion(LineVersionV3 version) {
    return version.toBuilder()
        .meansOfTransport(version.getMeansOfTransport() == null ? null : new HashSet<>(version.getMeansOfTransport()))
        .build();
  }

  private record VersionRange(LineVersionV3 oldestVersion, LineVersionV3 latestVersion) {

    static VersionRange of(List<LineVersionV3> versionsOrderedByValidFrom) {
      return new VersionRange(versionsOrderedByValidFrom.getFirst(), versionsOrderedByValidFrom.getLast());
    }
  }

  private record AffectedMembersData(LineVersionV3 lineVersion, LineVersionV3 editedVersion, VersionRange lineVersionRange,
                                     Map<String, List<LineVersionV3>> memberVersions, List<String> allowedMembers,
                                     List<String> notAllowedMembers) {

  }

  @FunctionalInterface
  private interface ShorteningAllowedRule {

    boolean isAllowed(LineVersionV3 editedVersion, VersionRange memberRange, int memberVersionCount);
  }
}
