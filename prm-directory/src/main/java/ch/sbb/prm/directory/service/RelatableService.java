package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class RelatableService<T extends Relatable> {

  protected final StopPlaceService stopPlaceService;
  protected final RelationService relationService;
  protected final ReferencePointRepository referencePointRepository;

  protected abstract ReferencePointElementType getReferencePointElementType();

  protected void createRelation(T version) {
    stopPlaceService.checkStopPlaceExists(version.getParentServicePointSloid());
    List<ReferencePointVersion> referencePointVersions = referencePointRepository.findByParentServicePointSloid(
        version.getParentServicePointSloid());
    referencePointVersions.forEach(referencePointVersion -> {
      RelationVersion relationVersion = RelationUtil.buildRelationVersion(version, getReferencePointElementType());
      relationService.createRelation(relationVersion);
    });
  }

}
