package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;

import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlatformService extends BaseRelatableService<PlatformVersion> {

  private final PlatformRepository platformRepository;

  public PlatformService(StopPlaceRepository stopPlaceRepository, RelationRepository relationRepository,
      PlatformRepository platformRepository, ReferencePointRepository referencePointRepository ) {
    super(stopPlaceRepository, relationRepository, referencePointRepository);
    this.platformRepository = platformRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return PLATFORM;
  }

  public List<PlatformVersion> getAllPlatforms() {
    return platformRepository.findAll();
  }

  public void createPlatformVersion(PlatformVersion version) {
    createRelation(version);
    platformRepository.save(version);
  }

}
