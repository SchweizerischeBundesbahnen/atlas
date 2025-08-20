package ch.sbb.prm.directory.location.service;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.prm.directory.service.PrmVersionable;
import ch.sbb.prm.directory.service.Relatable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrmLocationService {

  private final LocationService locationService;

  public <T extends Relatable & PrmVersionable> void allocateSloid(T version, SloidType sloidType) {
    if (version.getSloid() != null) {
      locationService.claimSloid(sloidType, version.getSloid());
    } else {
      version.setSloid(locationService.generateSloid(sloidType, version.getParentServicePointSloid()));
    }
  }

}
