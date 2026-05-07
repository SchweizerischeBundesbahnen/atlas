package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.stoppoint.entity.StopPointVersion;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformCompleteAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformReducedAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.StopPointCompleteAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.combiner.PlatformCompleteAccessibilityCombiner;
import ch.sbb.prm.directory.module.wheelchairaccessibility.helper.WheelchairAccessibilityDataLoader;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WheelchairAccessibilityService {

  private final PlatformReducedAccessibilityCalculator platformReducedCalculator;
  private final PlatformCompleteAccessibilityCalculator platformCompleteCalculator;
  private final StopPointCompleteAccessibilityCalculator stopPointCompleteCalculator;
  private final PlatformCompleteAccessibilityCombiner combiner;
  private final WheelchairAccessibilityDataLoader dataLoader;

  public WheelchairAccessibilityState calculateForReducedPlatform(PlatformVersion platform) {
    return platformReducedCalculator.calculate(platform);
  }

  public WheelchairAccessibilityState calculateForCompletePlatform(PlatformVersion platform,
      StopPointVersion stopPoint,
      List<RelationVersion> relations) {
    WheelchairAccessibilityState platformState = platformCompleteCalculator.calculatePlatform(platform, relations);
    WheelchairAccessibilityState stopPointState = stopPointCompleteCalculator.calculateStopPoint(stopPoint);
    return combiner.combine(stopPointState, platformState);
  }

  public WheelchairAccessibilityState calculateForPlatformToday(PlatformVersion platform, boolean isReduced) {
    if (isReduced) {
      return calculateForReducedPlatform(platform);
    }
    StopPointVersion stopPoint = dataLoader.loadStopPointValidToday(platform.getParentServicePointSloid());
    List<RelationVersion> relations = dataLoader.loadRelationsValidToday(platform.getSloid());
    return calculateForCompletePlatform(platform, stopPoint, relations);
  }

  public WheelchairAccessibilityState calculateForStopPointToday(boolean isReduced, Collection<PlatformVersion> platforms) {
    if (platforms.isEmpty()) {
      return WheelchairAccessibilityState.NO_INFO;
    }
    return platforms.stream()
        .map(platform -> calculateForPlatformToday(platform, isReduced))
        .max(Comparator.comparingInt(WheelchairAccessibilityState::getRank))
        .orElse(WheelchairAccessibilityState.NO_INFO);
  }
}
