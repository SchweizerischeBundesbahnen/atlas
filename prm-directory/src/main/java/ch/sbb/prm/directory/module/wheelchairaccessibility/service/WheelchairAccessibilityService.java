package ch.sbb.prm.directory.module.wheelchairaccessibility.service;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.model.wheelchairaccessibility.WheelchairAccessibilityState;
import ch.sbb.prm.directory.module.platform.entity.PlatformVersion;
import ch.sbb.prm.directory.module.relation.entity.RelationVersion;
import ch.sbb.prm.directory.module.relation.repository.RelationRepository;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformCompleteAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.calculator.PlatformReducedAccessibilityCalculator;
import ch.sbb.prm.directory.module.wheelchairaccessibility.helper.ValidityHelper;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WheelchairAccessibilityService {

  private final PlatformReducedAccessibilityCalculator reducedCalculator;
  private final PlatformCompleteAccessibilityCalculator completeCalculator;
  private final RelationRepository relationRepository;

  public WheelchairAccessibilityState calculateForPlatform(PlatformVersion platform, boolean isReduced) {
    if (isReduced) {
      return reducedCalculator.calculate(platform);
    }
    List<RelationVersion> relations = loadCurrentRelations(platform.getSloid());
    return completeCalculator.calculatePlatform(platform, relations);
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

  private List<RelationVersion> loadCurrentRelations(String platformSloid) {
    return relationRepository
        .findAllBySloidAndReferencePointElementType(platformSloid, ReferencePointElementType.PLATFORM)
        .stream()
        .filter(r -> ValidityHelper.isValidToday(r.getValidFrom(), r.getValidTo()))
        .toList();
  }
}
