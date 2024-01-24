package ch.sbb.prm.directory.service;

import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class SloidService {

  public void generateNewSloidIfNotGiven(BasePrmEntityVersion version) {
    if (version.getSloid() == null) {
      version.setSloid(generateNewSloid(version));
    }
  }

  // Replace this with call to new location-service
  public String generateNewSloid(BasePrmEntityVersion version) {
    return version.getParentServicePointSloid() + ":fake-sloid-" + UUID.randomUUID();
  }
}
