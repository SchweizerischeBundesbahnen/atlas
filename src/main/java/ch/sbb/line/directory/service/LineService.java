package ch.sbb.line.directory.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.controller.NotFoundExcpetion;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.Line_;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.exception.ConflictExcpetion;
import ch.sbb.line.directory.model.LineSearchRestrictions;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
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
  private final SpecificationBuilderService<Line> specificationBuilderService = new SpecificationBuilderService<Line>(
      List.of(Line_.swissLineNumber, Line_.number, Line_.description, Line_.businessOrganisation, Line_.slnid),
      Line_.validFrom,
      Line_.validTo,
      Line_.swissLineNumber
  );

  public Page<Line> findAll(LineSearchRestrictions lineSearchRestrictions) {
    return lineRepository.findAll(
        specificationBuilderService.buildSearchCriteriaSpecification(lineSearchRestrictions.getSearchCriteria())
            .and(specificationBuilderService.buildValidOnSpecification(lineSearchRestrictions.getValidOn()))
            .and(specificationBuilderService.buildEnumSpecification(lineSearchRestrictions.getStatusRestrictions(), Line_.status))
            .and(specificationBuilderService.buildEnumSpecification(lineSearchRestrictions.getTypeRestrictions(), Line_.type))
            .and(specificationBuilderService.buildSingleStringSpecification(lineSearchRestrictions.getSwissLineNumber())),
        lineSearchRestrictions.getPageable());
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
    return lineVersionRepository.save(lineVersion);
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
