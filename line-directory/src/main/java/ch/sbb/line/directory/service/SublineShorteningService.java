package ch.sbb.line.directory.service;

import ch.sbb.atlas.api.lidi.AffectedSublinesModel;
import ch.sbb.atlas.api.lidi.SublineShorteningRequest;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.model.SublineVersionRange;
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

  public AffectedSublinesModel checkAffectedSublines(LineVersion lineVersion, LocalDate validFrom, LocalDate validTo) {
    Map<String, List<SublineVersion>> sublineVersions = getAllSublinesByMainlineSlnid(lineVersion.getSlnid());

    List<String> allowedSublines = new ArrayList<>();
    List<String> notAllowedSublines = new ArrayList<>();

    boolean isShortening = isShortening(lineVersion, validFrom, validTo);

    if (!sublineVersions.isEmpty() && isShortening) {
      for (List<SublineVersion> versions : sublineVersions.values()) {
        SublineVersionRange sublineVersionValidityRange = getOldestAndLatest(versions);

        boolean isValidityAffected = isSublineValidityAffectedByUpdatedMainline(validFrom, validTo, sublineVersionValidityRange);
        boolean isShorteningAllowed = isShorteningAllowed(validFrom, validTo, sublineVersionValidityRange);

        if (isValidityAffected) {
          if (isShorteningAllowed) {
            allowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
          } else {
            notAllowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
          }
        }

      }
    }
    return new AffectedSublinesModel(allowedSublines, notAllowedSublines);
  }

  private List<SublineVersionRange> prepareSublinesToShort(LineVersion lineVersion,
      SublineShorteningRequest sublineShorteningRequest) {
    List<SublineVersionRange> sublinesToUpdate = new ArrayList<>();

    DateRange mainlineDateRange = new DateRange(
        sublineShorteningRequest.getMainlineValidity().getFrom(),
        sublineShorteningRequest.getMainlineValidity().getTo()
    );

    for (String slnid : sublineShorteningRequest.getSublinesToShort()) {
      List<SublineVersion> versions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
      SublineVersionRange sublineVersionRange = getOldestAndLatest(versions);

      if (!mainlineDateRange.getFrom().equals(lineVersion.getValidFrom())) {
        SublineVersion oldVersion = cloneSublineVersion(sublineVersionRange.getOldestVersion());
        SublineVersion editedVersion = cloneSublineVersion(sublineVersionRange.getOldestVersion());
        editedVersion.setValidFrom(mainlineDateRange.getFrom());
        SublineVersionRange sublineVersionToUpdate = new SublineVersionRange(oldVersion, editedVersion);
        sublinesToUpdate.add(sublineVersionToUpdate);
      }

      if (!mainlineDateRange.getTo().equals(lineVersion.getValidTo())) {
        SublineVersion latestVersion = cloneSublineVersion(sublineVersionRange.getLatestVersion());
        SublineVersion editedVersion = cloneSublineVersion(sublineVersionRange.getLatestVersion());
        editedVersion.setValidTo(mainlineDateRange.getTo());
        SublineVersionRange sublineVersionToUpdate = new SublineVersionRange(latestVersion, editedVersion);
        sublinesToUpdate.add(sublineVersionToUpdate);
      }
    }

    return sublinesToUpdate;
  }

  public List<SublineVersionRange> checkAndPrepareToShortSublines(LineVersion currentVersion, LineVersion editedVersion) {
    boolean isOnlyValidityChanged = isOnlyValidityChanged(currentVersion, editedVersion);
    boolean isShortening = isShortening(currentVersion, editedVersion.getValidFrom(), editedVersion.getValidTo());
    List<SublineVersionRange> sublinesToShort = new ArrayList<>();

    if (isOnlyValidityChanged && isShortening) {
      DateRange newMainlineValidity = new DateRange(editedVersion.getValidFrom(), editedVersion.getValidTo());
      AffectedSublinesModel affectedSublinesModel = checkAffectedSublines(currentVersion, editedVersion.getValidFrom(),
          editedVersion.getValidTo());

      if (!affectedSublinesModel.getAllowedSublines().isEmpty()) {
        SublineShorteningRequest sublineShorteningRequest = new SublineShorteningRequest(
            newMainlineValidity,
            affectedSublinesModel.getAllowedSublines());

        sublinesToShort = prepareSublinesToShort(currentVersion, sublineShorteningRequest);
      }
    }
    return sublinesToShort;
  }

  private static boolean isShortening(LineVersion currentVersion, LocalDate validFrom, LocalDate validTo) {
    return (validFrom.isAfter(currentVersion.getValidFrom()) || validTo
        .isBefore(currentVersion.getValidTo()));
  }

  private boolean isSublineValidityAffectedByUpdatedMainline(LocalDate validFrom, LocalDate validTo,
      SublineVersionRange sublineVersionRange) {
    DateRange dateRangeSubline =
        new DateRange(sublineVersionRange.getOldestVersion().getValidFrom(), sublineVersionRange.getLatestVersion().getValidTo());
    DateRange dateRangeMainline = new DateRange(validFrom, validTo);

    return !dateRangeSubline.isDateRangeContainedIn(dateRangeMainline);
  }

  private boolean isShorteningAllowed(LocalDate validFrom, LocalDate validTo, SublineVersionRange sublineVersionRange) {
    return (validFrom.isBefore(sublineVersionRange.getOldestVersion().getValidTo()) ||
        validFrom.isEqual(sublineVersionRange.getOldestVersion().getValidTo()))
        && (validTo.isAfter(sublineVersionRange.getLatestVersion().getValidFrom()) ||
        validTo.isEqual(sublineVersionRange.getLatestVersion().getValidFrom()));

  }

  private Map<String, List<SublineVersion>> getAllSublinesByMainlineSlnid(String mainlineSlnid) {
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

  private SublineVersionRange getOldestAndLatest(List<SublineVersion> sublines) {
    SublineVersion oldest = sublines.stream()
        .min(Comparator.comparing(SublineVersion::getValidFrom))
        .orElseThrow(() -> new NoSuchElementException("No sublines found"));
    SublineVersion latest = sublines.stream()
        .max(Comparator.comparing(SublineVersion::getValidTo))
        .orElseThrow(() -> new NoSuchElementException("No sublines found"));
    return new SublineVersionRange(oldest, latest);
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
}
