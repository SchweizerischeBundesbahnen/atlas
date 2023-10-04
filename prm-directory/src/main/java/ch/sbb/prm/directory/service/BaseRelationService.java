package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseRelationService<T extends BasePrmEntityVersion> {

  protected final StopPlaceRepository stopPlaceRepository;
  protected final RelationRepository relationRepository;

  protected void checkStopPlaceExists(String sloid) {
    if (!stopPlaceRepository.existsBySloid(sloid)) {
      throw new IllegalStateException("StopPlace with sloid [" + sloid + "] does not exists!");
    }
  }

  protected void createRelation(List<ReferencePointVersion> referencePointVersions, T version,
      ReferencePointElementType referencePointElementType) {
    referencePointVersions.forEach(referencePointVersion -> {
      RelationVersion relationVersion = RelationUtil.buildReleaseVersion(version, referencePointElementType);
      relationRepository.save(relationVersion);
    });
  }

}
