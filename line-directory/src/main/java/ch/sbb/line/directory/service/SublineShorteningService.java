package ch.sbb.line.directory.service;

import ch.sbb.atlas.api.lidi.AffectedSublinesModel;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.model.AffectedSublinesData;
import ch.sbb.line.directory.model.LineVersionRange;
import ch.sbb.line.directory.model.SublineVersionRange;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SublineShorteningService {

  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;

  public AffectedSublinesModel checkAffectedSublines(LineVersion lineVersion, LocalDate validFrom, LocalDate validTo) {
    List<String> allowedSublines = new ArrayList<>();
    List<String> notAllowedSublines = new ArrayList<>();

    LineVersion editedVersion = copyLineVersion(lineVersion);
    editedVersion.setValidFrom(validFrom);
    editedVersion.setValidTo(validTo);

    if (isOnlyValidityChanged(lineVersion, editedVersion) && isShortening(lineVersion, editedVersion)) {
      List<LineVersion> lineVersions = getAllLineVersionsBySlnid(lineVersion.getSlnid());
      LineVersionRange lineVersionRange = getOldestAndLatestLineVersion(lineVersions);
      Map<String, List<SublineVersion>> sublineVersions = getAllSublinesByMainlineSlnid(lineVersion.getSlnid());

      AffectedSublinesData data = AffectedSublinesData.builder()
          .lineVersion(lineVersion)
          .editedVersion(editedVersion)
          .lineVersionRange(lineVersionRange)
          .sublineVersions(sublineVersions)
          .allowedSublines(allowedSublines)
          .notAllowedSublines(notAllowedSublines)
          .build();

      if (!isSublineVersionsEmpty(sublineVersions)) {
        if (lineVersions.size() > 1) {
          processLineVersion(data);
        } else if (lineVersions.size() == 1) {
          processSingleLineVersion(data);
        }
      }
    }

    boolean isAffectedSublinesEmpty = isAffectedSublinesEmpty(allowedSublines, notAllowedSublines);
    boolean hasAllowedSublinesOnly = hasAllowedSublinesOnly(allowedSublines);
    boolean hasNotAllowedSublinesOnly = hasNotAllowedSublinesOnly(notAllowedSublines);

    return AffectedSublinesModel.builder()
        .allowedSublines(allowedSublines)
        .notAllowedSublines(notAllowedSublines)
        .isAffectedSublinesEmpty(isAffectedSublinesEmpty)
        .hasAllowedSublinesOnly(hasAllowedSublinesOnly)
        .hasNotAllowedSublinesOnly(hasNotAllowedSublinesOnly)
        .build();
  }

  private void processLineVersion(AffectedSublinesData data) {
    boolean isOnlyValidToChanged = isOnlyValidToChanged(data.getLineVersion(), data.getEditedVersion());
    boolean isOnlyValidFromChanged = isOnlyValidFromChanged(data.getLineVersion(), data.getEditedVersion());

    if (isOnlyValidFromChanged && !isOnlyValidToChanged &&
        isMatchingVersion(data.getLineVersion(), data.getLineVersionRange().getOldestVersion())) {
      processSublineVersionsValidFrom(data);
    } else if (!isOnlyValidFromChanged && isOnlyValidToChanged &&
        isMatchingVersion(data.getLineVersion(), data.getLineVersionRange().getLatestVersion())) {
      processSublineVersionsValidTo(data);
    }
  }

  private boolean isSublineShorteningAllowed(LineVersion editedVersion, SublineVersionRange range, int versionCount) {
    if (versionCount <= 1) {
      return true;
    }
    return isShorteningAllowedValidFrom(editedVersion, range) && isShorteningAllowedValidTo(editedVersion, range);
  }

  private boolean isMatchingVersion(LineVersion currentLineVersion, LineVersion lineVersionFromRange) {
    return Objects.equals(currentLineVersion.getId(), lineVersionFromRange.getId());
  }

  private void processSingleLineVersion(AffectedSublinesData data) {
    for (List<SublineVersion> versions : data.getSublineVersions().values()) {
      SublineVersionRange range = getOldestAndLatestSublineVersion(versions);
      String slnid = range.getLatestVersion().getSlnid();
      if (isSublineShorteningAllowed(data.getEditedVersion(), range, versions.size())) {
        data.getAllowedSublines().add(slnid);
      } else {
        data.getNotAllowedSublines().add(slnid);
      }
    }
  }

  private void processSublineVersionsValidFrom(AffectedSublinesData data) {
    for (List<SublineVersion> versions : data.getSublineVersions().values()) {
      SublineVersionRange range = getOldestAndLatestSublineVersion(versions);
      if (isShorteningAllowedValidFrom(data.getEditedVersion(), range)) {
        data.getAllowedSublines().add(range.getLatestVersion().getSlnid());
      } else {
        data.getNotAllowedSublines().add(range.getLatestVersion().getSlnid());
      }
    }
  }

  private void processSublineVersionsValidTo(AffectedSublinesData data) {
    for (List<SublineVersion> versions : data.getSublineVersions().values()) {
      SublineVersionRange range = getOldestAndLatestSublineVersion(versions);
      if (isShorteningAllowedValidTo(data.getEditedVersion(), range)) {
        data.getAllowedSublines().add(range.getLatestVersion().getSlnid());
      } else {
        data.getNotAllowedSublines().add(range.getLatestVersion().getSlnid());
      }
    }
  }

  private List<SublineVersionRange> prepareSublinesToShort(LineVersion lineVersion, LineVersion editedVersion,
      List<String> sublinesToShort) {

    List<SublineVersionRange> sublinesToUpdate = new ArrayList<>();
    List<LineVersion> lineVersions = getAllLineVersionsBySlnid(lineVersion.getSlnid());
    LineVersionRange lineVersionRange = getOldestAndLatestLineVersion(lineVersions);

    boolean isOnlyValidToChanged = isOnlyValidToChanged(lineVersion, editedVersion);
    boolean isOnlyValidFromChanged = isOnlyValidFromChanged(lineVersion, editedVersion);

    if (isMatchingVersion(lineVersion, lineVersionRange.getOldestVersion()) && isOnlyValidFromChanged && !isOnlyValidToChanged) {
      for (String slnid : sublinesToShort) {
        List<SublineVersion> versions = findAllSublineVersionsBySlnid(slnid);
        SublineVersionRange sublineVersionRange = getOldestAndLatestSublineVersion(versions);

        SublineVersion oldVersion = cloneSublineVersion(sublineVersionRange.getOldestVersion());
        SublineVersion editedSublineVersion = cloneSublineVersion(sublineVersionRange.getOldestVersion());
        editedSublineVersion.setValidFrom(editedVersion.getValidFrom());
        SublineVersionRange sublineVersionToUpdate = new SublineVersionRange(oldVersion, editedSublineVersion);
        sublinesToUpdate.add(sublineVersionToUpdate);
      }
    }

    if (isMatchingVersion(lineVersion, lineVersionRange.getLatestVersion()) && !isOnlyValidFromChanged && isOnlyValidToChanged) {
      for (String slnid : sublinesToShort) {
        List<SublineVersion> versions = findAllSublineVersionsBySlnid(slnid);
        SublineVersionRange sublineVersionRange = getOldestAndLatestSublineVersion(versions);

        SublineVersion oldVersion = cloneSublineVersion(sublineVersionRange.getLatestVersion());
        SublineVersion editedSublineVersion = cloneSublineVersion(sublineVersionRange.getLatestVersion());
        editedSublineVersion.setValidTo(editedVersion.getValidTo());
        SublineVersionRange sublineVersionToUpdate = new SublineVersionRange(oldVersion, editedSublineVersion);
        sublinesToUpdate.add(sublineVersionToUpdate);
      }
    }
    return sublinesToUpdate;
  }

  public List<SublineVersionRange> checkAndPrepareToShortSublines(LineVersion currentVersion, LineVersion editedVersion) {
    boolean isOnlyValidityChanged = isOnlyValidityChanged(currentVersion, editedVersion);
    List<SublineVersionRange> sublinesToShort = new ArrayList<>();

    if (isOnlyValidityChanged) {
      AffectedSublinesModel affectedSublinesModel = checkAffectedSublines(currentVersion, editedVersion.getValidFrom(),
          editedVersion.getValidTo());

      if (!affectedSublinesModel.getAllowedSublines().isEmpty()) {
        sublinesToShort = prepareSublinesToShort(currentVersion, editedVersion, affectedSublinesModel.getAllowedSublines());
      }
    }
    return sublinesToShort;
  }

  private static boolean isShortening(LineVersion currentVersion, LineVersion editedVersion) {
    return (editedVersion.getValidFrom().isAfter(currentVersion.getValidFrom()) || editedVersion.getValidTo()
        .isBefore(currentVersion.getValidTo()));
  }

  private boolean isShorteningAllowedValidTo(LineVersion editedVersion, SublineVersionRange sublineVersionRange) {
    return (editedVersion.getValidTo().isAfter(sublineVersionRange.getLatestVersion().getValidFrom())
        || editedVersion.getValidTo()
        .isEqual(sublineVersionRange
            .getLatestVersion().getValidFrom()));
  }

  private boolean isShorteningAllowedValidFrom(LineVersion editedVersion, SublineVersionRange sublineVersionRange) {
    return (editedVersion.getValidFrom().isBefore(sublineVersionRange.getOldestVersion().getValidTo())
        || editedVersion.getValidFrom().isEqual(
        sublineVersionRange.getOldestVersion().getValidTo()));
  }

  public Map<String, List<SublineVersion>> getAllSublinesByMainlineSlnid(String mainlineSlnid) {
    List<SublineVersion> sublineVersions = findAllSublineVersionsByMainlineSlnid(mainlineSlnid);
    return sublineVersions.stream()
        .collect(Collectors.groupingBy(SublineVersion::getSlnid));
  }

  private static boolean areNonValidityFieldsEqual(LineVersion current, LineVersion edited) {
    return Objects.equals(edited.getSwissLineNumber(), current.getSwissLineNumber())
        && Objects.equals(edited.getBusinessOrganisation(), current.getBusinessOrganisation())
        && Objects.equals(edited.getComment(), current.getComment())
        && Objects.equals(edited.getConcessionType(), current.getConcessionType())
        && Objects.equals(edited.getOfferCategory(), current.getOfferCategory())
        && Objects.equals(edited.getShortNumber(), current.getShortNumber())
        && Objects.equals(edited.getDescription(), current.getDescription())
        && Objects.equals(edited.getLongName(), current.getLongName())
        && Objects.equals(edited.getNumber(), current.getNumber());
  }

  private static boolean isOnlyValidityChanged(LineVersion current, LineVersion edited) {
    boolean validToChanged = !Objects.equals(edited.getValidTo(), current.getValidTo());
    boolean validFromChanged = !Objects.equals(edited.getValidFrom(), current.getValidFrom());
    return (validToChanged || validFromChanged) && areNonValidityFieldsEqual(current, edited);
  }

  private static boolean isOnlyValidToChanged(LineVersion current, LineVersion edited) {
    boolean validToChanged = !Objects.equals(edited.getValidTo(), current.getValidTo());
    boolean validFromUnchanged = Objects.equals(edited.getValidFrom(), current.getValidFrom());
    return validToChanged && validFromUnchanged && areNonValidityFieldsEqual(current, edited);
  }

  private static boolean isOnlyValidFromChanged(LineVersion current, LineVersion edited) {
    boolean validFromChanged = !Objects.equals(edited.getValidFrom(), current.getValidFrom());
    boolean validToUnchanged = Objects.equals(edited.getValidTo(), current.getValidTo());
    return validFromChanged && validToUnchanged && areNonValidityFieldsEqual(current, edited);
  }

  private SublineVersionRange getOldestAndLatestSublineVersion(List<SublineVersion> sublines) {
    SublineVersion oldest = sublines.stream()
        .min(Comparator.comparing(SublineVersion::getValidFrom))
        .orElseThrow(() -> new NoSuchElementException("No sublines found"));
    SublineVersion latest = sublines.stream()
        .max(Comparator.comparing(SublineVersion::getValidTo))
        .orElseThrow(() -> new NoSuchElementException("No sublines found"));
    return new SublineVersionRange(oldest, latest);
  }

  private LineVersionRange getOldestAndLatestLineVersion(List<LineVersion> lines) {
    LineVersion oldest = lines.stream()
        .min(Comparator.comparing(LineVersion::getValidFrom))
        .orElseThrow(() -> new NoSuchElementException("No line found"));
    LineVersion latest = lines.stream()
        .max(Comparator.comparing(LineVersion::getValidTo))
        .orElseThrow(() -> new NoSuchElementException("No line found"));
    return new LineVersionRange(oldest, latest);
  }

  private boolean isSublineVersionsEmpty(Map<String, List<SublineVersion>> sublineVersions) {
    return sublineVersions.isEmpty();
  }

  private boolean isAffectedSublinesEmpty(List<String> allowedSublines, List<String> notAllowedSublines) {
    return allowedSublines.isEmpty() && notAllowedSublines.isEmpty();
  }

  private boolean hasAllowedSublinesOnly(List<String> allowedSublines) {
    return !allowedSublines.isEmpty();
  }

  private boolean hasNotAllowedSublinesOnly(List<String> notAllowedSublines) {
    return !notAllowedSublines.isEmpty();
  }

  private List<LineVersion> getAllLineVersionsBySlnid(String slnid) {
    return lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
  }

  private List<SublineVersion> findAllSublineVersionsByMainlineSlnid(String slnid) {
    return sublineVersionRepository.getSublineVersionByMainlineSlnid(slnid);
  }

  private List<SublineVersion> findAllSublineVersionsBySlnid(String slnid) {
    return sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
  }

  private static SublineVersion cloneSublineVersion(SublineVersion sublineVersion) {
    return SublineVersion.builder()
        .slnid(sublineVersion.getSlnid())
        .mainlineSlnid(sublineVersion.getMainlineSlnid())
        .id(sublineVersion.getId())
        .businessOrganisation(sublineVersion.getBusinessOrganisation())
        .longName(sublineVersion.getLongName())
        .sublineType(sublineVersion.getSublineType())
        .concessionType(sublineVersion.getConcessionType())
        .description(sublineVersion.getDescription())
        .swissSublineNumber(sublineVersion.getSwissSublineNumber())
        .validFrom(sublineVersion.getValidFrom())
        .validTo(sublineVersion.getValidTo())
        .creationDate(sublineVersion.getCreationDate())
        .creator(sublineVersion.getCreator())
        .editionDate(sublineVersion.getEditionDate())
        .editor(sublineVersion.getEditor())
        .status(sublineVersion.getStatus())
        .version(sublineVersion.getVersion())
        .build();
  }

  public LineVersion copyLineVersion(LineVersion lineVersion) {
    return LineVersion.builder()
        .id(lineVersion.getId())
        .status(lineVersion.getStatus())
        .lineType(lineVersion.getLineType())
        .slnid(lineVersion.getSlnid())
        .paymentType(lineVersion.getPaymentType())
        .number(lineVersion.getNumber())
        .alternativeName(lineVersion.getAlternativeName())
        .combinationName(lineVersion.getCombinationName())
        .longName(lineVersion.getLongName())
        .colorFontRgb(lineVersion.getColorFontRgb())
        .colorBackRgb(lineVersion.getColorBackRgb())
        .colorFontCmyk(lineVersion.getColorFontCmyk())
        .colorBackCmyk(lineVersion.getColorBackCmyk())
        .description(lineVersion.getDescription())
        .icon(lineVersion.getIcon())
        .validFrom(lineVersion.getValidFrom())
        .validTo(lineVersion.getValidTo())
        .businessOrganisation(lineVersion.getBusinessOrganisation())
        .comment(lineVersion.getComment())
        .swissLineNumber(lineVersion.getSwissLineNumber())
        .version(lineVersion.getVersion())
        .creator(lineVersion.getCreator())
        .creationDate(lineVersion.getCreationDate())
        .editor(lineVersion.getEditor())
        .editionDate(lineVersion.getEditionDate())
        .concessionType(lineVersion.getConcessionType())
        .offerCategory(lineVersion.getOfferCategory())
        .build();
  }

}
