package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.repository.PlatformRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class PlatformService {

  private final PlatformRepository platformRepository;

  public List<PlatformVersion> getAllPlatforms() {
   return platformRepository.findAll();
  }

}
