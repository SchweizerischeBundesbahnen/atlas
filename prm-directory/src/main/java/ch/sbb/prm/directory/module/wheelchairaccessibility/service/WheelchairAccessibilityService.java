package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.wheelchairaccessibility.calculator.WheelchairAccessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.relation.service.RelationService;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
import java.time.LocalDate;
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

    return WheelchairAccessibility.calculatePlatformOnDate(
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

    return WheelchairAccessibility.calculateStopPointOnDate(AccessibilityRequest.builder()
        .stopPoint(stopPoint.stream().toList())
        .platform(platformsValidToday)
        .relations(relationsValidToday)
        .build());
  }

  public Accessibility calculateForPlatform(String platformSloid, LocalDate startingFrom) {
    List<PlatformVersion> platform = platformService.getAllVersions(platformSloid);
    String stopPointSloid = platform.getFirst().getParentServicePointSloid();
    List<StopPointVersion> stopPoint = stopPointService.findAllBySloidOrderByValidFrom(stopPointSloid);
    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(stopPointSloid);

    return WheelchairAccessibility.calculatePlatform(
        AccessibilityRequest.builder().stopPoint(stopPoint).platform(platform).relations(relations).build(),
        new AccessibilityFilter(startingFrom));
  }

  public Accessibility calculateForStopPoint(String stopPointSloid, LocalDate startingFrom) {
    List<StopPointVersion> stopPoint = stopPointService.findAllBySloidOrderByValidFrom(stopPointSloid);
    List<PlatformVersion> platform = platformService.getPlatformsByStopPoint(stopPointSloid);
    List<RelationVersion> relations = relationService.getRelationsByParentServicePointSloid(stopPointSloid);

    return WheelchairAccessibility.calculatePlatform(
        AccessibilityRequest.builder().stopPoint(stopPoint).platform(platform).relations(relations).build(),
        new AccessibilityFilter(startingFrom));
  }

}
