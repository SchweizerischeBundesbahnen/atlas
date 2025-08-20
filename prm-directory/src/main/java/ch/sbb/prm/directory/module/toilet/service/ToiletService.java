package ch.sbb.prm.directory.module.toilet.service;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.TOILET;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.toilet.ToiletOverviewModel;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.versioning.consumer.ApplyVersioningDeleteByIdLongConsumer;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.location.service.PrmLocationService;
import ch.sbb.prm.directory.service.PrmRelatableVersionableService;
import ch.sbb.prm.directory.exception.ElementTypeDoesNotExistException;
import ch.sbb.prm.directory.module.toilet.entity.ToiletVersion;
import ch.sbb.prm.directory.module.toilet.mapper.ToiletVersionMapper;
import ch.sbb.prm.directory.module.referencepoint.repository.ReferencePointRepository;
import ch.sbb.prm.directory.module.relation.service.RelationService;
import ch.sbb.prm.directory.module.toilet.search.ToiletSearchRestrictions;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import ch.sbb.prm.directory.module.toilet.repository.ToiletRepository;
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
  public ToiletVersion save(ToiletVersion version) {
    initDefaultData(version);
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

  public List<ToiletVersion> findByParentServicePointSloid(String parentServicePointSloid) {
    return toiletRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

  public List<ToiletOverviewModel> buildOverview(List<ToiletVersion> toiletVersions) {
    List<ToiletVersion> mergedVersions = OverviewDisplayBuilder.mergeVersionsForDisplay(toiletVersions,
        ToiletVersion::getSloid);
    return mergedVersions.stream()
        .map(toilet -> ToiletVersionMapper.toOverviewModel(toilet, getRecordingStatusIncludingRelation(toilet.getSloid(),
            toilet.getRecordingStatus())))
        .toList();
  }

  public void checkToiletExists(String sloid, String type) {
    if (!toiletRepository.existsBySloid(sloid)) {
      throw new ElementTypeDoesNotExistException(sloid, type);
    }
  }
}
