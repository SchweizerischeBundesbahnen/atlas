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
import ch.sbb.line.directory.validation.LineValidation;
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
  private final LineValidation lineValidation;
  private final SpecificationBuilderProvider specificationBuilderProvider;

  public Page<Line> findAll(SearchRestrictions<LineType> searchRestrictions) {
    SpecificationBuilderService<Line> specificationBuilderService = specificationBuilderProvider.getLineSpecificationBuilderService();
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
      throw new ConflictExcpetion(ConflictExcpetion.SWISS_NUMBER_NOT_UNIQUE_MESSAGE);
    }
    if (LineType.TEMPORARY.equals(lineVersion.getType())) {
      lineValidation.validateTemporaryLinesDuration(lineVersion, lineVersionRepository.findAllBySlnidOrderByValidFrom(lineVersion.getSlnid()));
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
