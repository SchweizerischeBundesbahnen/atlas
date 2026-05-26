package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.calculator.PlatformWheelchairAccessibilityCalculator;
import ch.sbb.atlas.wheelchairaccessibility.calculator.StopPointWheelchairAccessibilityCalculator;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.relation.service.RelationService;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WheelchairAccessibilityService {

  private final StopPointService stopPointService;
  private final PlatformService platformService;
  private final RelationService relationService;

  public WheelchairAccessibilityState calculateForPlatformToday(String platformSloid) {
    Optional<PlatformVersion> platform = platformService.findPlatformVersionValidToday(platformSloid);
    Optional<StopPointVersion> stopPoint = platform.flatMap(
        i -> stopPointService.findStopPointVersionValidToday(i.getParentServicePointSloid()));

    List<RelationVersion> relations = new ArrayList<>();
    platform.ifPresent(
        platformVersion -> relations.addAll(relationService.findRelationVersionValidTodayByPlatform(platformVersion.getSloid())));

    return PlatformWheelchairAccessibilityCalculator.calculateOnDate(
        AccessibilityRequest.builder()
            .stopPoint(stopPoint.stream().toList())
            .platform(platform.stream().toList())
            .relations(relations)
            .build());
  }

  public WheelchairAccessibilityState calculateForStopPointToday(String stopPointSloid) {
    Optional<StopPointVersion> stopPoint = stopPointService.findStopPointVersionValidToday(stopPointSloid);
    List<PlatformVersion> platformsValidToday = platformService.getPlatformsByStopPoint(stopPointSloid).stream()
        .filter(platform -> DateRange.fromVersionable(platform).containsToday())
        .toList();
    List<RelationVersion> relationsValidToday = relationService.getRelationsByParentServicePointSloid(
            stopPointSloid).stream()
        .filter(platform -> DateRange.fromVersionable(platform).containsToday())
        .toList();

    return StopPointWheelchairAccessibilityCalculator.calculateOnDate(AccessibilityRequest.builder()
        .stopPoint(stopPoint.stream().toList())
        .platform(platformsValidToday)
        .relations(relationsValidToday)
        .build());
  }

}
