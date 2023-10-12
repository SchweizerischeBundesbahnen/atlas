package ch.sbb.prm.directory.service;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PrmRelatableVersionableService<T extends Relatable & PrmVersionable> extends PrmVersionableService<T> {

  protected final StopPlaceService stopPlaceService;
  protected final RelationService relationService;
  protected final ReferencePointRepository referencePointRepository;

  protected PrmRelatableVersionableService(VersionableService versionableService, StopPlaceService stopPlaceService,
      RelationService relationService, ReferencePointRepository referencePointRepository) {
    super(versionableService);
    this.stopPlaceService = stopPlaceService;
    this.relationService = relationService;
    this.referencePointRepository = referencePointRepository;
  }

  protected abstract ReferencePointElementType getReferencePointElementType();

  protected void createRelation(T version) {
    stopPlaceService.checkStopPlaceExists(version.getParentServicePointSloid());
    List<ReferencePointVersion> referencePointVersions = referencePointRepository.findByParentServicePointSloid(
        version.getParentServicePointSloid());
    referencePointVersions.forEach(referencePointVersion -> {
      RelationVersion relationVersion = RelationUtil.buildRelationVersion(version, getReferencePointElementType());
      relationService.save(relationVersion);
    });
  }

}
