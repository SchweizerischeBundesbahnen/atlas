package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PrmRelatableVersionableService<T extends Relatable & PrmVersionable> extends PrmVersionableService<T> {

  protected final StopPointService stopPointService;
  protected final RelationService relationService;
  protected final ReferencePointRepository referencePointRepository;
  protected final PrmLocationService locationService;

  protected PrmRelatableVersionableService(VersionableService versionableService, StopPointService stopPointService,
      RelationService relationService, ReferencePointRepository referencePointRepository,
      PrmLocationService locationService) {
    super(versionableService);
    this.stopPointService = stopPointService;
    this.relationService = relationService;
    this.referencePointRepository = referencePointRepository;
    this.locationService = locationService;
  }

  protected abstract ReferencePointElementType getReferencePointElementType();

  protected abstract SloidType getSloidType();

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
    locationService.allocateSloid(version, getSloidType());
  }

  private void createRelations(T version) {
    if (!stopPointService.isReduced(version.getParentServicePointSloid())) {
      Map<String, List<ReferencePointVersion>> referencePoints = referencePointRepository.findByParentServicePointSloid(
          version.getParentServicePointSloid()).stream().collect(Collectors.groupingBy(ReferencePointVersion::getSloid));

      referencePoints.forEach((referencePointSloid, referencePointVersions) -> {
        RelationVersion relationVersion = RelationUtil.buildRelationVersion(version, referencePointVersions,
            getReferencePointElementType());
        relationService.save(relationVersion);
      });
    }
  }

  protected RecordingStatus getRecordingStatusIncludingRelation(String elementSloid, RecordingStatus elementRecordingStatus) {
    List<RelationVersion> relations = relationService.getRelationsBySloid(elementSloid);

    if (relations.isEmpty()) {
      return elementRecordingStatus;
    }

    boolean relationsIncomplete =
        OverviewDisplayBuilder.getPrioritizedVersion(relations).getRecordingStatus() == RecordingStatus.INCOMPLETE;

    if (relationsIncomplete) {
      return RecordingStatus.INCOMPLETE;
    }
    return elementRecordingStatus;
  }

}
