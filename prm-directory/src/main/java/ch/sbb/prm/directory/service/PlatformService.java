package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;

import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlatformService extends BaseRelationService<PlatformVersion> {

  private final PlatformRepository platformRepository;
  private final ReferencePointRepository referencePointRepository;

  public PlatformService(StopPlaceRepository stopPlaceRepository, RelationRepository relationRepository,
      PlatformRepository platformRepository, ReferencePointRepository referencePointRepository) {
    super(stopPlaceRepository, relationRepository);
    this.platformRepository = platformRepository;
    this.referencePointRepository = referencePointRepository;
  }

  public List<PlatformVersion> getAllPlatforms() {
    return platformRepository.findAll();
  }

  public List<PlatformVersion> getByServicePointParentSloid(String parentServicePointSloid) {
    return platformRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

  public void createPlatformVersion(PlatformVersion platformVersion) {
    checkStopPlaceExists(platformVersion.getParentServicePointSloid());
    List<ReferencePointVersion> referencePointVersions = referencePointRepository.findByParentServicePointSloid(
        platformVersion.getParentServicePointSloid());
    createRelation(referencePointVersions, platformVersion, PLATFORM);
    platformRepository.save(platformVersion);
  }

}
