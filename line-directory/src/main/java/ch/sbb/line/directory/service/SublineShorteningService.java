package ch.sbb.line.directory.service;

import ch.sbb.atlas.api.lidi.AffectedSublines;
import ch.sbb.atlas.api.lidi.SublineShorteningRequest;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.model.SublineVersionRange;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SublineShorteningService {

  private final SublineService sublineService;
  private final SublineVersionRepository sublineVersionRepository;
  private final LineVersionRepository lineVersionRepository;

  public AffectedSublines checkAffectedSublines(Long id, LocalDate validFrom, LocalDate validTo) {
    LineVersion lineVersion = findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

    Map<String, List<SublineVersion>> sublineVersions = getAllSublinesByMainlineSlnid(lineVersion.getSlnid());

    List<String> allowedSublines = new ArrayList<>();
    List<String> notAllowedSublines = new ArrayList<>();

    boolean isShortening = (validFrom.isAfter(lineVersion.getValidFrom()) || validTo.isBefore(lineVersion.getValidTo()));

    if (!sublineVersions.isEmpty() && isShortening) {
      for (List<SublineVersion> versions : sublineVersions.values()) {
        SublineVersionRange sublineVersionValidityRange = getOldestAndLatest(versions);

        boolean shorteningAllowed = isShorteningAllowed(validFrom, validTo, sublineVersionValidityRange);
        boolean isValidityAffected = isSublineValidityAffectedByUpdatedMainline(validFrom, validTo, sublineVersionValidityRange);

        if (isValidityAffected) {
          if (shorteningAllowed) {
            allowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
          } else {
            notAllowedSublines.add(sublineVersionValidityRange.getLatestVersion().getSlnid());
          }
        }

      }
    }
    return new AffectedSublines(allowedSublines, notAllowedSublines);
  }

  public void shortSublines(Long id, SublineShorteningRequest sublineShorteningRequest) {
    LineVersion lineVersion = findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

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
        sublineService.updateVersion(oldVersion, editedVersion);
      }

      if (!mainlineDateRange.getTo().equals(lineVersion.getValidTo())) {
        SublineVersion latestVersion = cloneSublineVersion(sublineVersionRange.getLatestVersion());
        SublineVersion editedVersion = cloneSublineVersion(sublineVersionRange.getLatestVersion());
        editedVersion.setValidTo(mainlineDateRange.getTo());
        sublineService.updateVersion(latestVersion, editedVersion);
      }
    }
  }

  public void checkAndShortSublines(LineVersion currentVersion, LineVersion editedVersion) {
    DateRange newMainlineValidity = new DateRange(editedVersion.getValidFrom(), editedVersion.getValidTo());
    AffectedSublines affectedSublines = checkAffectedSublines(currentVersion.getId(), editedVersion.getValidFrom(),
        editedVersion.getValidTo());

    SublineShorteningRequest sublineShorteningRequest = new SublineShorteningRequest(
        newMainlineValidity,
        affectedSublines.getAllowedSublines());

    if (affectedSublines.getNotAllowedSublines().isEmpty() && !affectedSublines.getAllowedSublines().isEmpty()) {
      shortSublines(currentVersion.getId(), sublineShorteningRequest);
    }
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

  public Optional<LineVersion> findById(Long id) {
    return lineVersionRepository.findById(id);
  }

  private Map<String, List<SublineVersion>> getAllSublinesByMainlineSlnid(String mainlineSlnid) {
    List<SublineVersion> sublineVersions = sublineVersionRepository.getSublineVersionByMainlineSlnid(mainlineSlnid);
    return sublineVersions.stream()
        .collect(Collectors.groupingBy(SublineVersion::getSlnid));
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
