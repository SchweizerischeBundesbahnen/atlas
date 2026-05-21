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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WheelchairAccessibilityService {

  private final StopPointService stopPointService;
  private final RelationService relationService;

  public WheelchairAccessibilityState calculateForPlatformToday(PlatformVersion platform) {
    Optional<StopPointVersion> stopPoint = stopPointService.findStopPointVersionValidToday(platform.getParentServicePointSloid());
    if (stopPoint.isEmpty()) {
      return WheelchairAccessibilityState.NO_INFO;
    }
    List<RelationVersion> relations = relationService.findRelationVersionValidTodayByPlatform(platform.getSloid());
    return WheelchairAccessibilityCalculator.calculateForPlatform(stopPoint.get(),
        PlatformWithRelations.builder().platform(platform).relations(relations).build());
  }

  public WheelchairAccessibilityState calculateForStopPointToday(StopPointVersion stopPoint,
      List<PlatformVersion> platformsValidToday) {
    List<PlatformWithRelations> platformsWithRelations = platformsValidToday.stream()
        .map(platformValidToday -> PlatformWithRelations.builder()
            .platform(platformValidToday)
            .relations(relationService.findRelationVersionValidTodayByPlatform(platformValidToday.getSloid()))
            .build())
        .toList();
    return WheelchairAccessibilityCalculator.calculateForStopPoint(stopPoint, platformsWithRelations);
  }

}
