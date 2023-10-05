package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseRelatableService<T extends BasePrmEntityVersion> {

  protected final StopPlaceRepository stopPlaceRepository;
  protected final RelationRepository relationRepository;
  protected final ReferencePointRepository referencePointRepository;

  protected abstract ReferencePointElementType getReferencePointElementType();

  private void checkStopPlaceExists(String sloid) {
    if (!stopPlaceRepository.existsBySloid(sloid)) {
      throw new IllegalStateException("StopPlace with sloid [" + sloid + "] does not exists!");
    }
  }

  protected void createRelation(T version) {
    checkStopPlaceExists(version.getParentServicePointSloid());
    List<ReferencePointVersion> referencePointVersions = referencePointRepository.findByParentServicePointSloid(
        version.getParentServicePointSloid());
    referencePointVersions.forEach(referencePointVersion -> {
      RelationVersion relationVersion = RelationUtil.buildReleaseVersion(version, getReferencePointElementType());
      relationRepository.save(relationVersion);
    });
  }

}
