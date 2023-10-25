package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PrmRelatableVersionableService<T extends Relatable & PrmVersionable> extends PrmVersionableService<T> {

  protected final StopPointService stopPointService;
  protected final RelationService relationService;
  protected final ReferencePointRepository referencePointRepository;

  protected PrmRelatableVersionableService(VersionableService versionableService, StopPointService stopPointService,
      RelationService relationService, ReferencePointRepository referencePointRepository) {
    super(versionableService);
    this.stopPointService = stopPointService;
    this.relationService = relationService;
    this.referencePointRepository = referencePointRepository;
  }

  protected abstract ReferencePointElementType getReferencePointElementType();

  protected void createRelation(T version) {
    stopPointService.checkStopPointExists(version.getParentServicePointSloid());
    List<ReferencePointVersion> referencePointVersions = referencePointRepository.findByParentServicePointSloid(
        version.getParentServicePointSloid());
    referencePointVersions.forEach(referencePointVersion -> {
      RelationVersion relationVersion = RelationUtil.buildRelationVersion(version,
          referencePointVersion.getSloid(), getReferencePointElementType());
      relationService.save(relationVersion);
    });
  }

}
