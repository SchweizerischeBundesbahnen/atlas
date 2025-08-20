package ch.sbb.prm.directory.module.platform.controller;

import ch.sbb.atlas.api.prm.model.platform.PlatformOverviewModel;
import ch.sbb.prm.directory.module.platform.api.PlatformApiInternal;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PlatformApiInternalController implements PlatformApiInternal {

  private final PlatformService platformService;

  @Override
  public List<PlatformOverviewModel> getPlatformOverview(String parentSloid) {
    return platformService.mergePlatformsForOverview(platformService.getPlatformsByStopPoint(parentSloid), parentSloid);
  }

}
