package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformCompleteAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformReducedAccessibilityCalculator;
import java.util.Collection;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WheelchairAccessibilityService {

  private final PlatformReducedAccessibilityCalculator reducedCalculator;
  private final PlatformCompleteAccessibilityCalculator completeCalculator;

  public WheelchairAccessibilityState calculateForPlatform(PlatformVersion platform, boolean isReduced) {
    if (isReduced) {
      return reducedCalculator.calculate(platform);
    }
    return completeCalculator.calculate(platform);
  }

  public WheelchairAccessibilityState calculateForStopPoint(boolean isReduced, Collection<PlatformVersion> platforms) {
    if (platforms.isEmpty()) {
      return WheelchairAccessibilityState.NO_INFO;
    }
    return platforms.stream()
        .map(platform -> calculateForPlatform(platform, isReduced))
        .max(Comparator.comparingInt(WheelchairAccessibilityState::getRank))
        .orElse(WheelchairAccessibilityState.NO_INFO);
  }
}
