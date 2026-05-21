package ch.sbb.atlas.wheelchairaccessibility.model;

import ch.sbb.atlas.model.DateRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformAccessibilityRequest {

  @Builder.Default
  private final List<AccessibilityStopPoint> stopPoint = new ArrayList<>();
  @Builder.Default
  private final List<AccessibilityPlatform> platform = new ArrayList<>();
  @Builder.Default
  private final List<AccessibilityRelation> relations = new ArrayList<>();

  public List<DateRange> getAllDateRanges() {
    return Stream.of(stopPoint, platform, relations)
        .flatMap(Collection::stream)
        .map(AccessibilityVersion::toDateRange)
        .toList();
  }

}
