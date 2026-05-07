package ch.sbb.prm.directory.module.stoppoint.service;

import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.mapper.StopPointVersionMapper;
import ch.sbb.prm.directory.module.wheelchairaccessibility.helper.ValidityHelper;
import ch.sbb.prm.directory.module.wheelchairaccessibility.service.WheelchairAccessibilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StopPointReadVersionService {

  private final StopPointService stopPointService;
  private final PlatformService platformService;
  private final WheelchairAccessibilityService wheelchairAccessibilityService;

  public List<ReadStopPointVersionModel> getAllVersionsWithCalculatedAccessibility(String sloid) {
    List<StopPointVersion> versions = stopPointService.findAllBySloidOrderByValidFrom(sloid);
    if (versions.isEmpty()) {
      return List.of();
    }
    boolean isReduced = stopPointService.isReduced(sloid);
    List<PlatformVersion> platforms = platformService.getPlatformsByStopPoint(sloid);

    return versions.stream()
        .map(version -> toReadModel(version, isReduced, platforms))
        .toList();
  }

  private ReadStopPointVersionModel toReadModel(StopPointVersion version, boolean isReduced, List<PlatformVersion> platforms) {
    if (!new DateRange(version.getValidFrom(), version.getValidTo()).containsToday()) {
      return StopPointVersionMapper.toModel(version);
    }
    List<PlatformVersion> currentPlatforms = platforms.stream()
        .filter(platformVersion -> ValidityHelper.isValidToday(platformVersion.getValidFrom(), platformVersion.getValidTo()))
        .toList();
    WheelchairAccessibilityState state = wheelchairAccessibilityService.calculateForStopPointToday(isReduced, currentPlatforms);
    return StopPointVersionMapper.toModelWithAccessibility(version, state);
  }

}
