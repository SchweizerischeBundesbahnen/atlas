package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.wheelchairaccessibility.calculator.WheelchairAccessibilityCalculator;
import ch.sbb.atlas.wheelchairaccessibility.model.PlatformWithRelations;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.relation.service.RelationService;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WheelchairAccessibilityService {

  private final StopPointService stopPointService;
  private final RelationService relationService;

  public WheelchairAccessibilityState calculateForPlatformToday(PlatformVersion platform) {
    StopPointVersion stopPoint = stopPointService.findValidToday(platform.getParentServicePointSloid());
    List<RelationVersion> relations = relationService.findValidTodayByPlatform(platform.getSloid());
    return WheelchairAccessibilityCalculator.calculateForPlatform(platform, stopPoint, relations);
  }

  public WheelchairAccessibilityState calculateForStopPointToday(StopPointVersion stopPoint,
      List<PlatformVersion> platforms) {
    List<PlatformWithRelations> platformsWithRelations = platforms.stream()
        .map(platform -> new PlatformWithRelations(platform, relationService.findValidTodayByPlatform(platform.getSloid())))
        .toList();
    return WheelchairAccessibilityCalculator.calculateForStopPoint(stopPoint, platformsWithRelations);
  }

}
