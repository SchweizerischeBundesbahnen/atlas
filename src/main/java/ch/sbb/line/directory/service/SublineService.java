package ch.sbb.line.directory.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.controller.NotFoundException;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.Subline_;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.enumaration.SublineType;
import ch.sbb.line.directory.exception.SublineConflictException;
import ch.sbb.line.directory.model.SearchRestrictions;
import ch.sbb.line.directory.repository.SublineRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Transactional
public class SublineService {

  private final SublineVersionRepository sublineVersionRepository;
  private final SublineRepository sublineRepository;
  private final VersionableService versionableService;
  private final LineService lineService;
  private final SpecificationBuilderProvider specificationBuilderProvider;

  public Page<Subline> findAll(SearchRestrictions<SublineType> searchRestrictions) {
    SpecificationBuilderService<Subline> specificationBuilderService = specificationBuilderProvider.getSublineSpecificationBuilderService();
    return sublineRepository.findAll(
        specificationBuilderService.buildSearchCriteriaSpecification(searchRestrictions.getSearchCriteria())
            .and(specificationBuilderService.buildValidOnSpecification(searchRestrictions.getValidOn()))
            .and(specificationBuilderService.buildEnumSpecification(searchRestrictions.getStatusRestrictions(), Subline_.status))
            .and(specificationBuilderService.buildEnumSpecification(searchRestrictions.getTypeRestrictions(), Subline_.type)),
        searchRestrictions.getPageable());
  }

  public List<SublineVersion> findSubline(String slnid) {
    return sublineVersionRepository.findAllBySlnidOrderByValidFrom(slnid);
  }

  public Optional<SublineVersion> findById(Long id) {
    return sublineVersionRepository.findById(id);
  }

  public SublineVersion save(SublineVersion sublineVersion) {
    sublineVersion.setStatus(Status.ACTIVE);
    List<SublineVersion> swissLineNumberOverlaps = sublineVersionRepository.findSwissLineNumberOverlaps(
        sublineVersion);
    if (!swissLineNumberOverlaps.isEmpty()) {
      throw new SublineConflictException(sublineVersion, swissLineNumberOverlaps);
    }
    if (lineService.findLineVersions(sublineVersion.getMainlineSlnid()).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Main line with SLNID " + sublineVersion.getMainlineSlnid() + " does not exist");
    }
    return sublineVersionRepository.save(sublineVersion);
  }

  public void deleteById(Long id) {
    if (!sublineVersionRepository.existsById(id)) {
      throw NotFoundException.getInstance().get();
    }
    sublineVersionRepository.deleteById(id);
  }

  public void deleteAll(String slnid) {
    List<SublineVersion> sublineVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        slnid);
    if (sublineVersions.isEmpty()) {
      throw NotFoundException.getInstance().get();
    }
    sublineVersionRepository.deleteAll(sublineVersions);
  }

  public void updateVersion(SublineVersion currentVersion, SublineVersion editedVersion) {
    List<SublineVersion> currentVersions = sublineVersionRepository.findAllBySlnidOrderByValidFrom(
        currentVersion.getSlnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(SublineVersion.class, versionedObjects, this::save,
        this::deleteById);
  }

}
