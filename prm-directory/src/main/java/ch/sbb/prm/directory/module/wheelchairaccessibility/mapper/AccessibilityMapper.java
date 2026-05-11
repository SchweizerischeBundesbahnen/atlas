package ch.sbb.prm.directory.module.wheelchairaccessibility.mapper;

import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityPlatform;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRelation;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityStopPoint;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import java.util.List;

public class AccessibilityMapper {

  public static AccessibilityPlatform toAccessibilityPlatform(PlatformVersion platform) {
    return AccessibilityPlatform.builder()
        .shuttle(platform.getShuttle())
        .vehicleAccess(platform.getVehicleAccess())
        .levelAccessWheelchair(platform.getLevelAccessWheelchair())
        .boardingDevice(platform.getBoardingDevice())
        .superelevation(platform.getSuperelevation())
        .build();
  }

  public static AccessibilityStopPoint toAccessibilityStopPoint(StopPointVersion stopPoint) {
    return AccessibilityStopPoint.builder()
        .alternativeTransport(stopPoint.getAlternativeTransport())
        .assistanceService(stopPoint.getAssistanceService())
        .assistanceAvailability(stopPoint.getAssistanceAvailability())
        .assistanceRequestFulfilled(stopPoint.getAssistanceRequestFulfilled())
        .build();
  }

  public static List<AccessibilityRelation> toAccessibilityRelations(List<RelationVersion> relations) {
    return relations.stream()
        .map(AccessibilityMapper::toAccessibilityRelation)
        .toList();
  }

  private static AccessibilityRelation toAccessibilityRelation(RelationVersion relation) {
    return AccessibilityRelation.builder()
        .stepFreeAccess(relation.getStepFreeAccess())
        .build();
  }

}
