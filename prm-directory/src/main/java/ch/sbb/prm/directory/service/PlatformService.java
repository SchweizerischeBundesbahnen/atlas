package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;

import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.enumeration.ReferencePointElementType;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlatformService extends RelatableService<PlatformVersion> {

  private final PlatformRepository platformRepository;

  public PlatformService(StopPlaceService stopPlaceService, RelationService relationService,
      PlatformRepository platformRepository, ReferencePointRepository referencePointRepository ) {
    super(stopPlaceService, relationService, referencePointRepository);
    this.platformRepository = platformRepository;
  }

  @Override
  protected ReferencePointElementType getReferencePointElementType() {
    return PLATFORM;
  }

  public List<PlatformVersion> getAllPlatforms() {
    return platformRepository.findAll();
  }

  public PlatformVersion createPlatformVersion(PlatformVersion version) {
    createRelation(version);
    return platformRepository.saveAndFlush(version);
  }

}
