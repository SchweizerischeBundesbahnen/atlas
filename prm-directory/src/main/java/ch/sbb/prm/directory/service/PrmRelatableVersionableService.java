package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.location.ClaimSloidRequestModel;
import ch.sbb.atlas.api.location.GenerateSloidRequestModel;
import ch.sbb.atlas.api.client.location.LocationClient;
import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.exception.SloidAlreadyExistsException;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import feign.FeignException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PrmRelatableVersionableService<T extends Relatable & PrmVersionable> extends PrmVersionableService<T> {

  protected final StopPointService stopPointService;
  protected final RelationService relationService;
  protected final ReferencePointRepository referencePointRepository;
  private final LocationClient locationClient;

  protected PrmRelatableVersionableService(VersionableService versionableService, StopPointService stopPointService,
      RelationService relationService, ReferencePointRepository referencePointRepository, LocationClient locationClient) {
    super(versionableService);
    this.stopPointService = stopPointService;
    this.relationService = relationService;
    this.referencePointRepository = referencePointRepository;
    this.locationClient = locationClient;
  }

  protected abstract ReferencePointElementType getReferencePointElementType();

  protected void createRelationWithSloidAllocation(T version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());
    allocateSloid(version);
    createRelations(version);
  }

  protected void createRelation(T version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());
    createRelations(version);
  }

  private void allocateSloid(T version) {
    if (version.getSloid() != null) {
      try {
        locationClient.claimSloid(new ClaimSloidRequestModel(version.getSloid()));
      } catch (FeignException e) {
        throw new SloidAlreadyExistsException(version.getSloid());
      }
    } else {
      version.setSloid(
          locationClient.generateSloid(new GenerateSloidRequestModel(SloidType.AREA, version.getParentServicePointSloid())));
    }
  }

  private void createRelations(T version) {
    if (!stopPointService.isReduced(version.getParentServicePointSloid())) {
      List<ReferencePointVersion> referencePointVersions = referencePointRepository.findByParentServicePointSloid(
          version.getParentServicePointSloid());
      referencePointVersions.forEach(referencePointVersion -> {
        RelationVersion relationVersion = RelationUtil.buildRelationVersion(version,
            referencePointVersion.getSloid(), getReferencePointElementType());
        relationService.save(relationVersion);
      });
    }
  }

}
