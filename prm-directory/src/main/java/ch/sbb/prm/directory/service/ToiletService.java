package ch.sbb.prm.directory.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ToiletVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.ToiletRepository;
import ch.sbb.prm.directory.search.ToiletSearchRestrictions;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ToiletService extends PrmRelatableVersionableService<ToiletVersion> {

  private final ToiletRepository toiletRepository;

  public ToiletService(ToiletRepository toiletRepository, StopPointService stopPointService,
      RelationService relationService, ReferencePointRepository referencePointRepository, VersionableService versionableService,
      PrmLocationService locationService) {
    super(versionableService, stopPointService, relationService, referencePointRepository, locationService);
    this.toiletRepository = toiletRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return TOILET;
  }

  @Override
  protected SloidType getSloidType() {
    return SloidType.TOILET;
  }

  @Override
  protected void incrementVersion(String sloid) {
    this.toiletRepository.incrementVersion(sloid);
  }

  @Override
  protected ToiletVersion save(ToiletVersion version) {
    return toiletRepository.saveAndFlush(version);
  }

  @Override
  public List<ToiletVersion> getAllVersions(String sloid) {
    return toiletRepository.findAllBySloidOrderByValidFrom(sloid);
  }

  @Override
  protected void applyVersioning(List<VersionedObject> versionedObjects) {
    versionableService.applyVersioning(ToiletVersion.class, versionedObjects, this::save,
        new ApplyVersioningDeleteByIdLongConsumer(toiletRepository));
  }
  public Page<ToiletVersion> findAll(ToiletSearchRestrictions searchRestrictions) {
    return toiletRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#version)")
  public ToiletVersion createToilet(ToiletVersion version) {
    createRelationWithSloidAllocation(version);
    return save(version);
  }

  @PreAuthorize("@prmUserAdministrationService.hasUserRightsToCreateOrEditPrmObject(#editedVersion)")
  public ToiletVersion updateToiletVersion(ToiletVersion currentVersion, ToiletVersion editedVersion) {
    return updateVersion(currentVersion, editedVersion);
  }

  public Optional<ToiletVersion> getToiletVersionById(Long id) {
    return toiletRepository.findById(id);
  }

  public void saveForImport(ToiletVersion version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());
    toiletRepository.saveAndFlush(version);
  }

  public ToiletVersion createToiletPointThroughImport(ToiletVersion version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());
    locationService.allocateSloid(version, SloidType.TOILET);
    return toiletRepository.saveAndFlush(version);
  }
}
