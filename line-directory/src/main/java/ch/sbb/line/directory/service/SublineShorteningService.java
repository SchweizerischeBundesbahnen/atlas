package ch.sbb.line.directory.service;

import ch.sbb.atlas.api.lidi.AffectedSublinesModel;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
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
      List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
      Map<String, List<SublineVersion>> sublineVersions = getAllSublinesByMainlineSlnid(lineVersion.getSlnid());
      LineVersionRange lineVersionRange = getOldestAndLatestLine(lineVersions);

      boolean isOnlyValidToChanged = isOnlyValidToChanged(lineVersion, editedVersion);
      boolean isOnlyValidFromChanged = isOnlyValidFromChanged(lineVersion, editedVersion);

      if (lineVersions.size() == 1) {

        if (!sublineVersions.isEmpty()) {
          for (List<SublineVersion> versions : sublineVersions.values()) {
            SublineVersionRange sublineVersionValidityRange = getOldestAndLatestSubline(versions);

            if (versions.size() > 1) {
              if (isShorteningAllowedValidFrom(editedVersion, sublineVersionValidityRange)
                  || isShorteningAllowedValidTo(editedVersion, sublineVersionValidityRange)) {
                allowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
              } else {
                notAllowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
              }
            } else {
              allowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
            }
          }
        }
      }

      if (lineVersions.size() > 1) {
        if (Objects.equals(lineVersion.getId(), lineVersionRange.getOldestVersion().getId())) {

          if (isOnlyValidFromChanged && !isOnlyValidToChanged) {

            if (!sublineVersions.isEmpty()) {
              for (List<SublineVersion> versions : sublineVersions.values()) {
                SublineVersionRange sublineVersionValidityRange = getOldestAndLatestSubline(versions);

                if (isShorteningAllowedValidFrom(editedVersion, sublineVersionValidityRange)) {
                  allowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
                } else {
                  notAllowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
                }
              }
            }
          }
        }
        if (Objects.equals(lineVersion.getId(), lineVersionRange.getLatestVersion().getId())) {
          if (!isOnlyValidFromChanged && isOnlyValidToChanged) {

            if (!sublineVersions.isEmpty()) {
              for (List<SublineVersion> versions : sublineVersions.values()) {
                SublineVersionRange sublineVersionValidityRange = getOldestAndLatestSubline(versions);

                if (isShorteningAllowedValidTo(editedVersion, sublineVersionValidityRange)) {
                  allowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
                } else {
                  notAllowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
                }
              }
            }
          }
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

  private boolean isAffectedSublinesEmpty(List<String> allowedSublines, List<String> notAllowedSublines) {
    return allowedSublines.isEmpty() && notAllowedSublines.isEmpty();
  }

  private boolean hasAllowedSublinesOnly(List<String> allowedSublines) {
    return !allowedSublines.isEmpty();
  }

  private boolean hasNotAllowedSublinesOnly(List<String> notAllowedSublines) {
    return !notAllowedSublines.isEmpty();
  }

  private List<SublineVersionRange> prepareSublinesToShort(LineVersion lineVersion, LineVersion editedVersion,
      List<String> sublinesToShort) {

    List<SublineVersionRange> sublinesToUpdate = new ArrayList<>();
    List<LineVersion> lineVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid());
    LineVersionRange lineVersionRange = getOldestAndLatestLine(lineVersions);

    boolean isOnlyValidToChanged = isOnlyValidToChanged(lineVersion, editedVersion);
    boolean isOnlyValidFromChanged = isOnlyValidFromChanged(lineVersion, editedVersion);

    if (Objects.equals(lineVersion.getId(), lineVersionRange.getOldestVersion().getId())) {

      if (isOnlyValidFromChanged && !isOnlyValidToChanged) {
        for (String slnid : sublinesToShort) {
          List<SublineVersion> versions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
          SublineVersionRange sublineVersionRange = getOldestAndLatestSubline(versions);

          SublineVersion oldVersion = cloneSublineVersion(sublineVersionRange.getOldestVersion());
          SublineVersion editedSublineVersion = cloneSublineVersion(sublineVersionRange.getOldestVersion());
          editedSublineVersion.setValidFrom(editedVersion.getValidFrom());
          SublineVersionRange sublineVersionToUpdate = new SublineVersionRange(oldVersion, editedSublineVersion);
          sublinesToUpdate.add(sublineVersionToUpdate);
        }
      }
    }

    if (Objects.equals(lineVersion.getId(), lineVersionRange.getLatestVersion().getId())) {

      if (!isOnlyValidFromChanged && isOnlyValidToChanged) {
        for (String slnid : sublinesToShort) {
          List<SublineVersion> versions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
          SublineVersionRange sublineVersionRange = getOldestAndLatestSubline(versions);

          SublineVersion oldVersion = cloneSublineVersion(sublineVersionRange.getLatestVersion());
          SublineVersion editedSublineVersion = cloneSublineVersion(sublineVersionRange.getLatestVersion());
          editedSublineVersion.setValidTo(editedVersion.getValidTo());
          SublineVersionRange sublineVersionToUpdate = new SublineVersionRange(oldVersion, editedSublineVersion);
          sublinesToUpdate.add(sublineVersionToUpdate);
        }
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
    return editedVersion.getValidTo().isBefore(sublineVersionRange.getLatestVersion().getValidTo()) &&
        (editedVersion.getValidTo().isAfter(sublineVersionRange.getLatestVersion().getValidFrom()) || editedVersion.getValidTo()
            .isEqual(sublineVersionRange
                .getLatestVersion().getValidFrom()));
  }

  private boolean isShorteningAllowedValidFrom(LineVersion editedVersion, SublineVersionRange sublineVersionRange) {
    return (editedVersion.getValidFrom().isBefore(sublineVersionRange.getOldestVersion().getValidTo())
        || editedVersion.getValidFrom().isEqual(
        sublineVersionRange.getOldestVersion().getValidTo())) &&
        editedVersion.getValidFrom().isAfter(sublineVersionRange.getOldestVersion().getValidFrom());
  }

  public Map<String, List<SublineVersion>> getAllSublinesByMainlineSlnid(String mainlineSlnid) {
    List<SublineVersion> sublineVersions = sublineVersionRepository.getSublineVersionByMainlineSlnid(mainlineSlnid);
    return sublineVersions.stream()
        .collect(Collectors.groupingBy(SublineVersion::getSlnid));
  }

  private static boolean isOnlyValidityChanged(LineVersion currentVersion, LineVersion editedVersion) {
    return (!Objects.equals(editedVersion.getValidTo(), currentVersion.getValidTo())
        || !Objects.equals(editedVersion.getValidFrom(), currentVersion.getValidFrom()))
        && Objects.equals(editedVersion.getSwissLineNumber(), currentVersion.getSwissLineNumber())
        && Objects.equals(editedVersion.getBusinessOrganisation(), currentVersion.getBusinessOrganisation())
        && Objects.equals(editedVersion.getComment(), currentVersion.getComment())
        && Objects.equals(editedVersion.getConcessionType(), currentVersion.getConcessionType())
        && Objects.equals(editedVersion.getOfferCategory(), currentVersion.getOfferCategory())
        && Objects.equals(editedVersion.getShortNumber(), currentVersion.getShortNumber())
        && Objects.equals(editedVersion.getDescription(), currentVersion.getDescription())
        && Objects.equals(editedVersion.getLongName(), currentVersion.getLongName())
        && Objects.equals(editedVersion.getNumber(), currentVersion.getNumber());
  }

  private static boolean isOnlyValidToChanged(LineVersion currentVersion, LineVersion editedVersion) {
    return !Objects.equals(editedVersion.getValidTo(), currentVersion.getValidTo())
        && Objects.equals(editedVersion.getValidFrom(), currentVersion.getValidFrom())
        && Objects.equals(editedVersion.getSwissLineNumber(), currentVersion.getSwissLineNumber())
        && Objects.equals(editedVersion.getBusinessOrganisation(), currentVersion.getBusinessOrganisation())
        && Objects.equals(editedVersion.getComment(), currentVersion.getComment())
        && Objects.equals(editedVersion.getConcessionType(), currentVersion.getConcessionType())
        && Objects.equals(editedVersion.getOfferCategory(), currentVersion.getOfferCategory())
        && Objects.equals(editedVersion.getShortNumber(), currentVersion.getShortNumber())
        && Objects.equals(editedVersion.getDescription(), currentVersion.getDescription())
        && Objects.equals(editedVersion.getLongName(), currentVersion.getLongName())
        && Objects.equals(editedVersion.getNumber(), currentVersion.getNumber());
  }

  private static boolean isOnlyValidFromChanged(LineVersion currentVersion, LineVersion editedVersion) {
    return !Objects.equals(editedVersion.getValidFrom(), currentVersion.getValidFrom())
        && Objects.equals(editedVersion.getValidTo(), currentVersion.getValidTo())
        && Objects.equals(editedVersion.getSwissLineNumber(), currentVersion.getSwissLineNumber())
        && Objects.equals(editedVersion.getBusinessOrganisation(), currentVersion.getBusinessOrganisation())
        && Objects.equals(editedVersion.getComment(), currentVersion.getComment())
        && Objects.equals(editedVersion.getConcessionType(), currentVersion.getConcessionType())
        && Objects.equals(editedVersion.getOfferCategory(), currentVersion.getOfferCategory())
        && Objects.equals(editedVersion.getShortNumber(), currentVersion.getShortNumber())
        && Objects.equals(editedVersion.getDescription(), currentVersion.getDescription())
        && Objects.equals(editedVersion.getLongName(), currentVersion.getLongName())
        && Objects.equals(editedVersion.getNumber(), currentVersion.getNumber());
  }

  private SublineVersionRange getOldestAndLatestSubline(List<SublineVersion> sublines) {
    SublineVersion oldest = sublines.stream()
        .min(Comparator.comparing(SublineVersion::getValidFrom))
        .orElseThrow(() -> new NoSuchElementException("No sublines found"));
    SublineVersion latest = sublines.stream()
        .max(Comparator.comparing(SublineVersion::getValidTo))
        .orElseThrow(() -> new NoSuchElementException("No sublines found"));
    return new SublineVersionRange(oldest, latest);
  }

  private LineVersionRange getOldestAndLatestLine(List<LineVersion> lines) {
    LineVersion oldest = lines.stream()
        .min(Comparator.comparing(LineVersion::getValidFrom))
        .orElseThrow(() -> new NoSuchElementException("No line found"));
    LineVersion latest = lines.stream()
        .max(Comparator.comparing(LineVersion::getValidTo))
        .orElseThrow(() -> new NoSuchElementException("No line found"));
    return new LineVersionRange(oldest, latest);
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
