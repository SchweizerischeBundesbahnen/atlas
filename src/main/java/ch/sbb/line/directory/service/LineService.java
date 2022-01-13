package ch.sbb.line.directory.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.controller.NotFoundExcpetion;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.Line_;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.exception.ConflictExcpetion;
import ch.sbb.line.directory.model.SearchRestrictions;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class LineService {

  private final LineVersionRepository lineVersionRepository;
  private final LineRepository lineRepository;
  private final VersionableService versionableService;
  private final SpecificationBuilderService<Line> specificationBuilderService = new SpecificationBuilderService<Line>(
      List.of(Line_.swissLineNumber, Line_.number, Line_.description, Line_.businessOrganisation, Line_.slnid),
      Line_.validFrom,
      Line_.validTo,
      Line_.swissLineNumber
  );

  public Page<Line> findAll(SearchRestrictions<LineType> searchRestrictions) {
    return lineRepository.findAll(
        specificationBuilderService.buildSearchCriteriaSpecification(searchRestrictions.getSearchCriteria())
            .and(specificationBuilderService.buildValidOnSpecification(searchRestrictions.getValidOn()))
            .and(specificationBuilderService.buildEnumSpecification(searchRestrictions.getStatusRestrictions(), Line_.status))
            .and(specificationBuilderService.buildEnumSpecification(searchRestrictions.getTypeRestrictions(), Line_.type))
            .and(specificationBuilderService.buildSingleStringSpecification(searchRestrictions.getSwissLineNumber())),
        searchRestrictions.getPageable());
  }

  public Optional<Line> findLine(String slnid) {
    return lineRepository.findAllBySlnid(slnid);
  }

  public List<LineVersion> findLineVersions(String slnid) {
    return lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
  }

  public Optional<LineVersion> findById(Long id) {
    return lineVersionRepository.findById(id);
  }

  public LineVersion save(LineVersion lineVersion) {
    lineVersion.setStatus(Status.ACTIVE);
    if (!lineVersionRepository.hasUniqueSwissLineNumber(lineVersion)) {
      throw new ConflictExcpetion();
    }
    if (lineVersion.getType().equals(LineType.TEMPORARY)) {
      validateTemporaryLinesValidity(lineVersion);
    }
    return lineVersionRepository.save(lineVersion);
  }

  public void validateTemporaryLinesValidity(LineVersion lineVersion) {
    // check incoming version to not exceed 12 months
    if (isValidityLongerThan12Months(lineVersion.getValidFrom(), lineVersion.getValidTo())) {
      throw new ConflictExcpetion();
    }
    if (lineVersion.getSlnid() == null) {
      return;
    }
    // check affecting temporary versions with incoming to not exceed 12 months
    List<LineVersion> allBySlnidAndType = lineVersionRepository.findAllBySlnidAndTypeAndIdNot(
        lineVersion.getSlnid(), LineType.TEMPORARY, lineVersion.getId() == null ? 0 : lineVersion.getId());
    List<LineVersion> versionsWhichAffectCurrentLineVersion = allBySlnidAndType.stream()
        .filter(version -> doesItAffect(version.getValidTo(), lineVersion.getValidFrom()) || doesItAffect(version.getValidFrom(), lineVersion.getValidTo()))
        .collect(Collectors.toList());
    if (versionsWhichAffectCurrentLineVersion.isEmpty()) {
      return;
    }
    versionsWhichAffectCurrentLineVersion.add(lineVersion);
    sortLineVersionsOnValidity(versionsWhichAffectCurrentLineVersion);

    int affectiveVersionsSize;
    do {
      affectiveVersionsSize = versionsWhichAffectCurrentLineVersion.size();
      expandVersionChain(allBySlnidAndType, versionsWhichAffectCurrentLineVersion);
      sortLineVersionsOnValidity(versionsWhichAffectCurrentLineVersion);
    }
    while (versionsWhichAffectCurrentLineVersion.size() != affectiveVersionsSize);

    if (isValidityLongerThan12Months(versionsWhichAffectCurrentLineVersion.get(0).getValidFrom(),
        versionsWhichAffectCurrentLineVersion.get(versionsWhichAffectCurrentLineVersion.size() - 1).getValidTo())) {
      throw new ConflictExcpetion();
    }
  }

  // TODO: exceptions and encapsulate validation to seperate class

  private boolean isValidityLongerThan12Months(LocalDate date1, LocalDate date2) {
    return date1.until(date2, ChronoUnit.MONTHS) > 12 || date2.until(date1, ChronoUnit.MONTHS) > 12;
  }

  private boolean doesItAffect(LocalDate date1, LocalDate date2) {
    long dayDiff = date1.until(date2, ChronoUnit.DAYS);
    return dayDiff == 1 || dayDiff == -1;
  }

  private void sortLineVersionsOnValidity(List<LineVersion> versions) {
    versions.sort(Comparator.comparing(LineVersion::getValidFrom));
  }

  private void expandVersionChain(List<LineVersion> allBySlnidAndType, List<LineVersion> versionsWhichAffectCurrentLineVersion) {
    allBySlnidAndType.stream()
        .filter(version -> doesItAffect(version.getValidTo(), versionsWhichAffectCurrentLineVersion.get(0).getValidFrom()))
        .findFirst().ifPresent(versionsWhichAffectCurrentLineVersion::add);
    allBySlnidAndType.stream()
        .filter(version -> doesItAffect(version.getValidFrom(), versionsWhichAffectCurrentLineVersion.get(versionsWhichAffectCurrentLineVersion.size() - 1).getValidTo()))
        .findFirst().ifPresent(versionsWhichAffectCurrentLineVersion::add);
  }

  public void deleteById(Long id) {
    if (!lineVersionRepository.existsById(id)) {
      throw NotFoundExcpetion.getInstance().get();
    }
    lineVersionRepository.deleteById(id);
  }

  public void deleteAll(String slnid) {
    List<LineVersion> currentVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
    if (currentVersions.isEmpty()) {
      throw NotFoundExcpetion.getInstance().get();
    }
    lineVersionRepository.deleteAll(currentVersions);
  }

  public void updateVersion(LineVersion currentVersion,
      LineVersion editedVersion) {
    List<LineVersion> currentVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(
        currentVersion.getSlnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(LineVersion.class, versionedObjects, this::save,
        this::deleteById);
  }

}
