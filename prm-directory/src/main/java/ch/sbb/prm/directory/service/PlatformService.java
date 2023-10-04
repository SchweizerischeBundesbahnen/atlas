package ch.sbb.prm.directory.service;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;

import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.util.RelationUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class PlatformService {

  private final PlatformRepository platformRepository;
  private final ReferencePointRepository referencePointRepository;
  private final RelationService relationService;

  public List<PlatformVersion> getAllPlatforms() {
    return platformRepository.findAll();
  }

  public List<PlatformVersion> getByServicePointParentSloid(String parentServicePointSloid) {
    return platformRepository.findByParentServicePointSloid(parentServicePointSloid);
  }

  public void createPlatformVersion(PlatformVersion platformVersion) {
    //TODO: check if PRM SopPlace already exists
    List<ReferencePointVersion> referencePointVersions = referencePointRepository.findByParentServicePointSloid(
        platformVersion.getParentServicePointSloid());
    referencePointVersions.forEach(referencePointVersion -> {
      RelationVersion relationVersion = RelationUtil.buildReleaseVersion(platformVersion, PLATFORM);
      relationService.createRelation(relationVersion);
    });
    platformRepository.save(platformVersion);
  }

}
