package ch.sbb.exportservice.job.prm.wheelchairaccessibility.service;

import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AccessibilityRequestFactory {

  public static AccessibilityRequest forStopPoint(List<StopPointVersion> stopPointVersions,
      List<PlatformVersion> platformsOfStopPoint, List<RelationVersion> relationsOfStopPoint) {
    return AccessibilityRequest.builder()
        .stopPoint(stopPointVersions)
        .platform(platformsOfStopPoint)
        .relations(relationsOfStopPoint)
        .build();
  }

  public static AccessibilityRequest forPlatform(List<StopPointVersion> stopPointVersions, String platformSloid,
      List<PlatformVersion> platformVersions, List<RelationVersion> relationsOfStopPoint) {
    List<RelationVersion> relationsOfPlatform =
        relationsOfStopPoint.stream().filter(i -> platformSloid.equals(i.getSloid())).toList();
    return AccessibilityRequest.builder()
        .stopPoint(stopPointVersions)
        .platform(platformVersions)
        .relations(relationsOfPlatform)
        .build();
  }
}
