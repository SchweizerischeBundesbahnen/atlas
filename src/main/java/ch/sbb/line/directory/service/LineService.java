package ch.sbb.line.directory.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.controller.NotFoundException;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.Line_;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.exception.LineConflictException;
import ch.sbb.line.directory.exception.LineRangeSmallerThenSublineRangeException;
import ch.sbb.line.directory.model.SearchRestrictions;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.validation.LineValidation;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
  private final SublineVersionRepository sublineVersionRepository;
  private final LineValidation lineValidation;
  private final SpecificationBuilderProvider specificationBuilderProvider;


  public Page<Line> findAll(SearchRestrictions<LineType> searchRestrictions) {
    SpecificationBuilderService<Line> specificationBuilderService = specificationBuilderProvider.getLineSpecificationBuilderService();
    return lineRepository.findAll(
        specificationBuilderService.buildSearchCriteriaSpecification(
                                       searchRestrictions.getSearchCriteria())
                                   .and(specificationBuilderService.buildValidOnSpecification(
                                       searchRestrictions.getValidOn()))
                                   .and(specificationBuilderService.buildEnumSpecification(
                                       searchRestrictions.getStatusRestrictions(), Line_.status))
                                   .and(specificationBuilderService.buildEnumSpecification(
                                       searchRestrictions.getTypeRestrictions(), Line_.type))
                                   .and(specificationBuilderService.buildSingleStringSpecification(
                                       searchRestrictions.getSwissLineNumber())),
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
    List<LineVersion> swissLineNumberOverlaps = lineVersionRepository.findSwissLineNumberOverlaps(
        lineVersion);
    if (!swissLineNumberOverlaps.isEmpty()) {
      throw new LineConflictException(lineVersion, swissLineNumberOverlaps);
    }
    if (LineType.TEMPORARY.equals(lineVersion.getType())) {
      lineValidation.validateTemporaryLinesDuration(lineVersion,
          lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid()));
    }

    validateLineRangeOutsideOfLineRange(lineVersion);
    return lineVersionRepository.save(lineVersion);
  }

  private void validateLineRangeOutsideOfLineRange(LineVersion lineVersion) {

    List<SublineVersion> sublineVersions = sublineVersionRepository.getSublineVersionByMainlineSlnid(
        lineVersion.getSlnid());
    if (!sublineVersions.isEmpty()) {
      sublineVersions.sort(Comparator.comparing(SublineVersion::getValidFrom));
      SublineVersion firstSublineVersion = sublineVersions.get(0);
      SublineVersion lastSublineVersion = sublineVersions.get(sublineVersions.size() - 1);
      if (lineVersion.getValidFrom().isAfter(firstSublineVersion.getValidFrom())
          || lineVersion.getValidTo().isBefore(lastSublineVersion.getValidTo())) {
        throw new LineRangeSmallerThenSublineRangeException(lineVersion,
            firstSublineVersion.getSwissSublineNumber(), firstSublineVersion.getValidFrom(),
            lastSublineVersion.getValidTo());
      }
    }
  }

  public void deleteById(Long id) {
    if (!lineVersionRepository.existsById(id)) {
      throw NotFoundException.getInstance().get();
    }
    lineVersionRepository.deleteById(id);
  }

  public void deleteAll(String slnid) {
    List<LineVersion> currentVersions = lineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
    if (currentVersions.isEmpty()) {
      throw NotFoundException.getInstance().get();
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
